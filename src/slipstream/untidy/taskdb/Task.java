package slipstream.untidy.taskdb;
import java.util.*;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;
import slipstream.untidy.taskapp.SGUI3;

//TODO: create unique ID for each task
//TODO: modify findabove and findbelow to use uniqueID
//TODO: modify AddTaskController to use a LinkedList of uniqueID, or structure with uniqueID next to String Title
//TODO: modify AddTaskController to use findabove and findbelow by uniqueID

public class Task {
    protected final DependencyList DEPENDENCY;
    protected final StringProperty NAME;
    protected final StringProperty PATH;
    protected IntegerProperty priority;
    protected IntegerProperty breakdown;
    protected boolean isExpanded;
    protected boolean flaggedFromAbove;
    protected boolean flaggedFromBelow;
    protected boolean flaggedFromPerms;
    protected final IntegerProperty Depth;
    protected int uniqueID;
    private TaskList inList;
    private IntegerProperty importance;

    //simple getters
    public Integer getImportance() { return importance.getValue(); }
    public TaskList getList() { return inList; }
    public Integer getBreakdown() { return breakdown.getValue(); }

    public boolean isPreOfImportantTask() {
        return this.getAllBelow().stream().anyMatch(t -> t.getImportance() > 0);
    }

    public boolean isImportantInSomeWay() {
        return this.getImportance() > 0 || this.isPreOfImportantTask();
    }

    /**
     * As of now, there may only be one perm pre and perm post.
     * @return Task PermPre
     */
    public Task getPermPre() {
        return DEPENDENCY.getPermPre();
    }
    /**
     * As of now, there may only be one perm pre and perm post.
     * @return Task PermPost
     */
    public Task getPermPost() {
        return DEPENDENCY.getPermPost();
    }

    //property returns
    public IntegerProperty importanceProperty() { return importance; }
    /**
     * @return IntegerProperty Depth
     */
    public IntegerProperty DepthProperty() { return Depth; }
    public final IntegerProperty priorityProperty() { return this.priority; }

    @Override
    public String toString() {
        return NAME.toString();
    }

    /**
     * Recurse over posts, and count. Can traverse over the same task multiple times. Stop at flagged perms to avoid infinite loop.
     * @return Integer: recursive sum of post count
     */
    public Integer getPriority() {
        int temp = 0;
        if(flaggedFromBelow) {
            for(Task p : getPosts()) {
                temp += p.getPriority() + 1;
            }
            if(getPermPost() != null && !getPermPost().flaggedFromPerms) {
                temp += getPermPost().getPriority() + 1;
            }
            priority.set(temp);
            flaggedFromBelow = false;
        }
        return priority.getValue();
    }

    public void makeExpanded() { isExpanded = true; }

    /**
     * Traverses above the task to get the longest chain of prerequisites. <br>
     * A -> B -> C -> D: D depth is 3. Depth count starts at 0. <br>
     * Used to index the tasks in a LinkedList of LinkedLists.
     * @return Depth of a task.
     */
    public Integer getDepth() {
        int temp = 0;
        if(flaggedFromAbove) {
            for(Task p : getPres()) {
                temp = max(p.getDepth() + 1, temp);
            }
            if(!flaggedFromPerms && getPermPre() != null) {
                temp = max(getPermPre().getDepth() + 1, temp);
            }
            Depth.set(temp);
            flaggedFromAbove = false;
        }
        return Depth.getValue();
    }

    /**
     * flagBelow: used to indicate that a Task's depth needs to be updated.
     */
    public void flagBelow() {
        flaggedFromAbove = true;
        getPosts().forEach(x -> {
            if(!x.flaggedFromAbove) {
                x.flagBelow();
            }
        });
        if(getPermPost() != null && !getPermPost().flaggedFromAbove) {
            getPermPost().flagBelow();
        }
    }

    /**
     * flagAbove: used to indicate that a Task's priority needs to be updated.
     */
    public void flagAbove() {
        flaggedFromBelow = true;
        getPres().forEach(x -> {
            if(!x.flaggedFromBelow) {
                x.flagAbove();
            }
        });
        if(getPermPre() != null && !getPermPre().flaggedFromBelow) {
            getPermPre().flagAbove();
        }
    }

    /**
     * flagPerm: used to indicate that a Task is next in its perm loop.
     */
    public void flagPerm() {
        flaggedFromPerms = true;
        Task t = getPermPost();
        if(getPermPre() != null && t != null) {
            while(!t.eq(this.getNAME())) {
                t.flaggedFromPerms = false;
                t = t.getPermPost();
            }
            getPermPre().flagAbove();
            flagBelow();
        }
    }

    public boolean isFlaggedFromPerms() {
        return flaggedFromPerms;
    }
    public boolean isFlaggedFromAbove() { return flaggedFromAbove; }
    public boolean isFlaggedFromBelow() { return flaggedFromBelow; }
    
    public void setPriority(int x) {}

    public void flip(IntegerProperty x) { x.set(x.get() > 0 ? 0 : 1);}
    public void setBreakdown(int x) { breakdown.set(x); }
    public void flipBreakdown() { flip(breakdown); }
    public final IntegerProperty breakdownProperty() { return this.breakdown; }
    
    public boolean eq(String s) { return this.NAME.get().equalsIgnoreCase(s); }

    public boolean eq(Task t) { return eq(t.getNAME()); }

    public boolean is_ID(int id) { return uniqueID == id; }

    public Task() {
        DEPENDENCY = new DependencyList();
        NAME = new SimpleStringProperty("");
        PATH = new SimpleStringProperty("");
        isExpanded = false;
        flaggedFromAbove = true;
        flaggedFromBelow = true;
        flaggedFromPerms = false;
        Depth = new SimpleIntegerProperty(0);
        priority = new SimpleIntegerProperty(0);
        breakdown = new SimpleIntegerProperty(0);
        importance = new SimpleIntegerProperty(0);
    }
    
    public Task(String s) {
        DEPENDENCY = new DependencyList();
        NAME = new SimpleStringProperty(s);
        PATH = new SimpleStringProperty("");
        isExpanded = false;
        flaggedFromAbove = true;
        flaggedFromBelow = true;
        flaggedFromPerms = false;
        Depth = new SimpleIntegerProperty(0);
        priority = new SimpleIntegerProperty(0);
        breakdown = new SimpleIntegerProperty(0);
        importance = new SimpleIntegerProperty(0);
    }
    
    public void setList(TaskList list) {
        inList = list;
        uniqueID = list.getNextUniqueTaskID();
    }

    /**
     * Is this a Task with no posts?
     * @return true if there are no posts or perm posts
     */
    public boolean isBottom() {
        if(DEPENDENCY.isPostEmpty()) {
            if(DEPENDENCY.isPermPostEmpty()) return true;
            if(!getPermPost().flaggedFromPerms) return false;
            return true;
        }
        return false;
    }

    public boolean isTop() {
        return DEPENDENCY.isPreEmpty() && (DEPENDENCY.isPermPreEmpty() ^ flaggedFromPerms);
    }

    public String getNAME() {
        return NAME.get();
    }
    
    public void setNAME(String name) {
        this.NAME.set(name);
    }
    
    public StringProperty NAMEProperty() {
        return this.NAME;
    }

    /**
     * searches for a task in the perm loop. Keeps searching until it hits the flagged perm in the loop, which is the head, top, or next to do.
     * @param s
     * @return Task if a match is found in perm loop
     */
    public Task findPermPre(String s) {
        if(eq(s)) return null;
        if(getPermPre() == null) return null;
        if(flaggedFromPerms) return null;
        LinkedList<Task> Q = new LinkedList<>();
        Q.push(this);
        //add if contains if multiple perm pres per task
        while(Q.peek().getPermPre() != null) {
            if(Q.peek().getPermPre().eq(s)) return Q.peek().getPermPre();
            if(Q.peek().getPermPre().flaggedFromPerms) return null;
            Q.push(Q.peek().getPermPre());
        }
        return null;
    }

    public Task findPermPost(String s) {
        if(eq(s)) return null;
        if(getPermPost() == null) return null;
        if(getPermPost().flaggedFromPerms) return null;
        LinkedList<Task> Q = new LinkedList<>();
        Q.push(getPermPost());
        while(Q.peek() != null) {
            if(Q.peek().eq(s)) return Q.peek();
            if(Q.peek().getPermPost().flaggedFromPerms) return null;
            Q.push(Q.peek().getPermPost());
        }
        return null;
    }

    public Task findPermPre(Task t) {
        if(uniqueID == t.uniqueID) return null;
        if(getPermPre() == null) return null;
        if(flaggedFromPerms) return null;
        LinkedList<Task> Q = new LinkedList<>();
        Q.push(this);
        //add if contains if multiple perm pres per task
        while(Q.peek().getPermPre() != null) {
            if(Q.peek().getPermPre().is_ID(t.uniqueID)) return Q.peek().getPermPre();
            if(Q.peek().getPermPre().flaggedFromPerms) return null;
            Q.push(Q.peek().getPermPre());
        }
        return null;
    }

    public Task findPermPost(Task t) {
        if(uniqueID == t.uniqueID) return null;
        if(getPermPost() == null) return null;
        if(getPermPost().flaggedFromPerms) return null;
        LinkedList<Task> Q = new LinkedList<>();
        Q.push(getPermPost());
        while(Q.peek() != null) {
            if(Q.peek().is_ID(t.uniqueID)) return Q.peek();
            if(Q.peek().getPermPost().flaggedFromPerms) return null;
            Q.push(Q.peek().getPermPost());
        }
        return null;
    }

    /**
     * a similar method to findPermPre and findPermPost but ignores perm flags.
     * @param t: the task to search for.
     * @return true if task is in the same perm loop as the input
     */
    public boolean isLoopedWith(Task t) {
        LinkedList<Task> Q = new LinkedList<>();
        Q.push(this);
        while(Q.peek().getPermPost() != null && !Q.contains(Q.peek().getPermPost())) {
            if(Q.peek().getPermPost().eq(t.getNAME())) return true;
            else Q.push(Q.peek().getPermPost());
        }
        return false;
    }


    public Task getFlaggedInLoop() {
        if(flaggedFromPerms) {
            //System.out.println(getNAME() + " is the flagged perm.");
            return this;
        }
        else {
            //System.out.println(getNAME() + " is not a flagged perm.");
            LinkedList<Task> Q = new LinkedList<>();
            Q.push(this);
            while(Q.peek().getPermPost() != null && !Q.contains(Q.peek().getPermPost())) {
                if(Q.peek().getPermPost().isFlaggedFromPerms()) {
                    //System.out.println(Q.peek().getPermPost().getNAME() + " is a flagged perm! returning.");
                    return Q.peek().getPermPost();
                }
                else {
                    //System.out.println(Q.peek().getPermPost().getNAME() + " is not a flagged perm.");
                    Q.push(Q.peek().getPermPost());
                }
            }
            //System.out.println("No flagged tasks found in a loop with " + getNAME());
            return null;
        }
    }

    public Task findAbove(String tname) {
        if(eq(tname)) return null;
        boolean preIsNotNull = getPermPre() != null;
        if(getPres().size() > 0 || preIsNotNull) {
            LinkedList<Task> Q = new LinkedList<>();
            Q.addAll(getPres());
            //added to include perm pre in initial queue
            if(preIsNotNull) Q.add(getPermPre());
            Task current;
            int i = 0;
            while(i < Q.size()) {
                current = Q.get(i);
                if(current.eq(tname)) { return current; }
                if(current.getPermPre() != null) {
                    if(current.findPermPre(tname) != null) {
                        return current.findPermPre(tname);
                    }
                }
                current.getPres().stream().filter(x -> !Q.contains(x)).forEach(y -> {
                    Q.add(y);
                });
                i++;
            }
        }
        return null;
    }

    public Task findBelow(String tname) {
        if(eq(tname)) return null;
        boolean postIsNotNull = getPermPost() != null;
        if(getPosts().size() > 0 || postIsNotNull) {
            LinkedList<Task> Q = new LinkedList<>();
            Q.addAll(getPosts());
            if(postIsNotNull && !getPermPost().flaggedFromPerms) Q.add(getPermPost());
            Task current;
            int i = 0;
            while(i < Q.size()) {
                current = Q.get(i);
                if(current.eq(tname)) { return current; }
                if(current.getPermPost() != null) {
                    if(current.findPermPost(tname) != null) {
                        return current.findPermPost(tname);
                    }
                }
                current.getPosts().stream().filter(x -> !Q.contains(x)).forEach(y -> {
                    Q.add(y);
                });
                i++;
            }
        }
        return null;
    }

    /**
     * @param t
     * @return Task if it matches the query exactly
     */
    public Task findBelow(Task t) {
        if(t.uniqueID == uniqueID) return null;
        if(getAllBelow().contains(t)) return t;
        return null;
    }

    public Task findAbove(Task t) {
        if(t.uniqueID == uniqueID) return null;
        if(getAllAbove().contains(t)) return t;
        return null;
    }


    private void syncLists(ArrayList<Task> ll, LinkedHashSet<Task> lhs) {
        int llsize = ll.size();
        if(llsize < lhs.size()) {
            Iterator<Task> lhsi = lhs.iterator();
            int i = 0;
            while(lhsi.hasNext() && i++ < llsize) lhsi.next();
            lhsi.forEachRemaining(x -> ll.add(x));
        }
    }

    /**
     * piles tasks below this one into a zippy data structure.
     * "who hurt you?"
     * @return
     */
    public LinkedHashSet<Task> getAllBelow() {
        ArrayList<Task> Q = new ArrayList<>();
        LinkedHashSet<Task> U = new LinkedHashSet<>();
        Q.addAll(getPosts());
        U.addAll(getPosts());
        if(getPermPost() != null && !getPermPost().flaggedFromPerms) {
            Q.add(getPermPost());
            U.add(getPermPost());
        }
        Task current;
        int i = 0;
        while(i < Q.size()) {
            current = Q.get(i);
            if(current.getPermPost() != null && !current.getPermPost().flaggedFromPerms) U.add(current.getPermPost());
            U.addAll(current.getPosts());
            syncLists(Q, U);
            i++;
        }
        return U;
    }

    /**
     * piles tasks above this one into a zippy data structure.
     * @return
     */
    //TODO: linkedlinkedhashset
    public LinkedHashSet<Task> getAllAbove() {
        ArrayList<Task> Q = new ArrayList<>();
        LinkedHashSet<Task> U = new LinkedHashSet<>();
        Q.addAll(getPres());
        U.addAll(getPres());
        if(getPermPre() != null && !flaggedFromPerms) {
            U.add(getPermPre());
            Q.add(getPermPre());
        }
        Task current;
        int i = 0;
        while(i < Q.size()) {
            current = Q.get(i);
            if(current.getPermPre() != null && !current.flaggedFromPerms) U.add(current.getPermPre());
            U.addAll(current.getPres());
            syncLists(Q, U);
            i++;
        }
        return U;
    }

    public LinkedHashSet<Task> getAllBoth() {
        LinkedHashSet<Task> lhs = getAllAbove();
        lhs.addAll(getAllBelow());
        return lhs;
    }

    public LinkedList<Task> getPres() {
        return DEPENDENCY.getPres();
    }
    
    public LinkedList<Task> getPosts() {
        return DEPENDENCY.getPosts();
    }
    
    public Task getPre(int i) {
        return DEPENDENCY.getPre(i);
    }
    
    public Task getPost(int i) {
        return DEPENDENCY.getPost(i);
    }

    public void pushPre(Task task) {
        DEPENDENCY.addPre(task);
        this.flagBelow();
        task.flagAbove();
    }

    public void pushPost(Task task) {
        DEPENDENCY.addPost(task);
        task.flagBelow();
        this.flagAbove();
    }

    public boolean setPermPre(Task AAA) { return AAA.setPermPost(this); }

    public boolean setPermPost(Task BBB) {
        Task AAA = this;
        if(BBB != null) {
            //System.out.println("\n");
            //System.out.println("Setting " + AAA.getNAME() + " --> " + BBB.getNAME());
            //System.out.println("getting " + AAA.getNAME() + " flagged task");
            Task Aflagged = AAA.getFlaggedInLoop();
            //System.out.println("getting " + BBB.getNAME() + " flagged task");
            Task Bflagged = BBB.getFlaggedInLoop();
            if(eq(BBB) || isLoopedWith(BBB)) { return false; }
            if(findBelow(BBB) != null) { return false; }
            if(findAbove(BBB) != null) { return false; }
            getPosts().removeIf(x -> {
                if(x.eq(BBB)) return true;
                if(BBB.findBelow(x) != null) {
                    x.DEPENDENCY.removePre(this);
                    x.flagBelow();
                    flagAbove();
                    return true;
                }
                return false;
            });
            BBB.getPres().removeIf(x -> {
                if(x.eq(this)) return true;
                if(findAbove(x) != null) {
                    x.DEPENDENCY.removePost(BBB);
                    x.flagAbove();
                    flagBelow();
                    return true;
                }
                return false;
            });
            Task xxX = AAA.getPermPre();
            Task yyY = BBB.getPermPre();
            Task Xxx = AAA.getPermPost();
            Task Yyy = BBB.getPermPost();
            AAA.DEPENDENCY.setPermPost(BBB);
            BBB.DEPENDENCY.setPermPre(AAA);
            if(yyY != null && Yyy != null) {
                if(Bflagged != null) {
                    //System.out.println(BBB.getNAME() + " had a perm pre.");
                    //System.out.println(BBB.getNAME() + " had a perm post.");
                    if(Xxx != null && xxX != null && Aflagged != null) {
                        //System.out.println(AAA.getNAME() + " had a perm pre.");
                        //System.out.println(AAA.getNAME() + " had a perm post.");
                        yyY.DEPENDENCY.setPermPost(Xxx);
                        Xxx.DEPENDENCY.setPermPre(yyY);
                        //System.out.println(Bflagged.getNAME() + " will now be unflagged.");
                        Bflagged.flaggedFromPerms = false;
                    }
                    else {
                        //System.out.println(AAA.getNAME() + " didn't have a perm pre.");
                        //System.out.println(AAA.getNAME() + " didn't have a perm post.");
                        yyY.DEPENDENCY.setPermPost(AAA);
                        AAA.DEPENDENCY.setPermPre(yyY);
                    }
                }
                else {
                    //System.out.println(BBB.getNAME() + " says it has perms but nothing is flagged.");
                }
            }
            else if(Xxx != null && xxX != null) {
                if(Aflagged != null) {
                    //System.out.println(AAA.getNAME() + " didn't have perm dependencies.");
                    //System.out.println(AAA.getNAME() + " has a perm pre.");
                    //System.out.println(AAA.getNAME() + " has a perm post.");
                    Xxx.DEPENDENCY.setPermPre(BBB);
                    BBB.DEPENDENCY.setPermPost(Xxx);
                }
                else {
                    //System.out.println(AAA.getNAME() + " says it has perms but nothing is flagged.");
                }
            }
            else {
                //System.out.println("Neither task has perm dependencies yet.");
                AAA.DEPENDENCY.setPermPre(BBB);
                BBB.DEPENDENCY.setPermPost(AAA);
                AAA.flaggedFromPerms = true;
                //System.out.println(AAA.getNAME() + " is flagged now.");
            }
            BBB.flagAbove();
            AAA.flagBelow();
        }
        //System.out.println("Finished setting perm dependency.\n");
        return true;
    }

    //A +> B
    //A -> C
    //B -> C ?
    //A -> C will still be there but it should be removed since B -> C covers it.
    public boolean addPost(Task task) {
        if(uniqueID == task.uniqueID) return false;
        if(findBelow(task) != null || findAbove(task) != null) return false;
        if(task.findAbove(this) != null || task.findBelow(this) != null) return false;

        getPosts().removeIf(t -> {
            if(task.findBelow(t) != null) {
                t.DEPENDENCY.removePre(this);
                t.flagBelow();
                flagAbove();
                return true;
            }
            return false;
        });

        task.getPres().removeIf(t -> {
            if (findAbove(t) != null) {
                t.DEPENDENCY.removePost(task);
                t.flagAbove();
                flagBelow();
                return true;
            }
            return false;
        });

        pushPost(task);
        task.pushPre(this);
        return true;
    }

    //TODO: solve perms issue, and last unit test in tasktest
    public boolean addPre(Task task) {
        if(uniqueID == task.uniqueID) return false;
        //one possibility is that findAbove isn't finding things
        //another is that adding Pres is somehow really broken
        if(findAbove(task) != null || findBelow(task) != null) return false;
        if(task.findBelow(this) != null || task.findAbove(this) != null) return false;

        //TODO: findabove might not be finding perms above.
        getPres().removeIf(t -> {
           if(task.findAbove(t) != null) {
               t.DEPENDENCY.removePost(this);
               t.flagAbove();
               flagBelow();
               return true;
           }
           return false;
        });

        task.getPosts().removeIf(t -> {
            if(findBelow(t) != null) {
                t.DEPENDENCY.removePre(task);
                t.flagBelow();
                flagAbove();
                return true;
            }
            return false;
        });

        pushPre(task);
        task.pushPost(this);
        return true;
    }

    public boolean removePre(Task t) {
        if(DEPENDENCY.removePre(t)) {
            t.DEPENDENCY.removePost(this);
            this.flagBelow();
            t.flagAbove();
            return true;
        }
        return false;
    }

    public boolean removePost(Task t) {
        if(DEPENDENCY.removePost(t)) {
            t.DEPENDENCY.removePre(this);
            t.flagBelow();
            this.flagAbove();
            return true;
        }
        return false;
    }

    public boolean removePost(String t_name) {
        for(Task t : getPosts()) {
            if(t.eq(t_name)) {
                if(!DEPENDENCY.removePost(t)) {
                    return false;
                }
                if(!t.removePre(this)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean removePre(String t_name) {
        for(Task t : getPres()) {
            if(t.eq(t_name)) {
                if(!DEPENDENCY.removePre(t)) {
                    return false;
                }
                if(!t.removePost(this)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void removePermPost() {
        Task ppt = getPermPost();
        if(ppt != null) {
            Task ppppt = ppt.getPermPost();
            if(ppppt.eq(this)) {
                DEPENDENCY.removePermPost();
                DEPENDENCY.removePermPre();
            }
            else {
                if(ppt.isFlaggedFromPerms()) { flagPerm(); }
                DEPENDENCY.setPermPost(ppppt);
                ppppt.DEPENDENCY.setPermPre(this);
            }
            ppt.DEPENDENCY.removePermPre();
            ppt.DEPENDENCY.removePermPost();
            ppt.flagBelow();
            ppt.flagAbove();
            flagAbove();
            flagBelow();
        }
    }

    public void removePermPre() {
        getPermPre().getPermPre().removePermPost();
    }
    
    private static Comparator<Task> priorityComparator = (x, y) -> x.getPriority() > y.getPriority() ? -1 : 1;
    
    public TreeItem<Task> toTreeItem() {
        return new CustomTreeItem(this);
    }

    public void updateTo(Task t) {
        this.DEPENDENCY.updateTo(t.getPres(), t.getPosts(), t.getPermPre(), t.getPermPost());
        this.setNAME(t.getNAME());
        this.flagBelow();
        this.flagAbove();
        SGUI3.getList().reSort();
    }
    
    int max(int a, int b) {
        return a >= b ? a : b;
    }

    public String getPATH() {
        String pathString = "";
        LinkedList<String> path = new LinkedList<>();
        if(!path.contains(this.getNAME())) path.addFirst(this.getNAME());
        if(this.getPres().size() > 0) path.addFirst(this.getPre(0).getPATH());
        else if(this.getPermPre() != null) path.addFirst(this.getPermPre().getPATH());
        for(int i = 0; i < path.size(); i++) {
            pathString += (i != path.size() - 1 ? path.get(i) + " -> " : path.get(i));
        }
        PATH.set(pathString);
        return PATH.getValue();
    }

    public void flipImportance() {
        importance.set(importance.get() > 0 ? 0 : 1);
    }
}
