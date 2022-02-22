package slipstream.untidy.taskdb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import slipstream.tidy.utils.UI;

/**
 * <h1>TaskList</h1>
 * The TaskList is the data structure that holds the Tasks. <br>
 * The data structure is a Directed Graph (which may be abbreviated as DG. It is similar to a tree, except instead of being able to have one parent node, you may have many.
 * In this case, however, it is stored in an ObservableList<LinkedList<Task>>. The ObservableList serves as the levels, or depths, of the DG. There is no root node. Level 0 will be populated by Tasks that have no prerequisite.
 * Next come the LinkedLists. These are the collections at each level. TODO: consider switching to hashed list of some sort. <br>
 * This data structure affords us the ability to iterate through all the tasks in the list in O(n).
 */
public class TaskList {
    private ObservableList<LinkedList<Task>> AllTasks;
    
    private String FileName;
    private String Line;
    private BufferedReader TaskReader;
    private BufferedWriter TaskWriter;
    private int nextUniqueTaskID;
    
    public boolean isOpen() {
        return !this.FileName.isEmpty();
    }
    
    public final void setFileName(String NewFileName) {
        if(NewFileName == null || NewFileName.isEmpty()) {
            UI.spawnErrorBox("please enter a valid file name.");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File As");
            Stage filestage = new Stage();
            UI.centerStage(filestage);
            File file = fileChooser.showOpenDialog(filestage);
            if(file != null) {
                setFileName(file.getAbsolutePath());
            }
        }
        FileName = NewFileName;
    }
    
    public TaskList() {
        total_size = 0;
        FileName = "";
        AllTasks = FXCollections.observableList(new LinkedList<>());
        AllTasks.add(new LinkedList<>());
        nextUniqueTaskID = 0;
    }
    
    public String readLine(BufferedReader x) {
        try { return x.readLine(); }
        catch(IOException e) {
            UI.spawnErrorBox("error reading line.");
            return null;
        }
    }
    
    public String getFileName() {
        return this.FileName;
    }

    /**TaskList(String NewFileName)
     * Opens the tasklist and regenerates the DAG.
     * TODO: run profiler and find opportunities for improvement
     * TODO: research implementing iterators and methods for LinkedHashSet
     * TODO: reimplement AllTasks as a LockStepHashSet
     */
    public TaskList(String NewFileName) {
        //System.out.println("TaskList(String)");
        total_size = 0;
        AllTasks = FXCollections.observableList(new LinkedList<>());
        AllTasks.add(new LinkedList<>());
        setFileName(NewFileName);
        try {
            TaskReader = new BufferedReader(new FileReader(NewFileName));
        } catch (FileNotFoundException e) {}

        if (TaskReader != null) {
            String RuleTemp[];
            Line = readLine(TaskReader);
            LinkedList<Task> PermsToFlag = new LinkedList<>();
            while (Line != null) {
                if (Line.startsWith("t ")) {
                    if (!this.addTask(Line.substring(2))) {
                        //UI.spawnErrorBox("Duplicate task lines detected in file.");
                    }
                }
                else if (Line.startsWith("r ")) {
                    RuleTemp = Line.substring(2).split("\t");
                    if (!addRule(RuleTemp[0], RuleTemp[1])) {
                    }
                }
                else if (Line.startsWith("p ")) {
                    RuleTemp = Line.substring(2).split("\t");
                    if (!addPerm(RuleTemp[0], RuleTemp[1])) {
                    }
                }
                Line = readLine(TaskReader);
            }
            PermsToFlag.forEach(x -> x.flagPerm());
            try {
                TaskReader.close();
            }
            catch (IOException e) {
                UI.spawnErrorBox("error releasing file from reader instance");
            }
            //System.out.println("reSorting");
            reSort();
        }
    }
    
    public TaskList(TaskList toCopy) {
        total_size = 0;
        AllTasks = toCopy.AllTasks;
        FileName = toCopy.FileName;
        nextUniqueTaskID = toCopy.nextUniqueTaskID;
    }

    public boolean saveFile() {
        if(AllTasks.isEmpty()) {                                                                //no file
            UI.spawnErrorBox("There is no file opened.");
            return false;
        }
        LinkedList<String> LinesToWrite = new LinkedList<>();
        LinkedList<Task> flaggedFromPerm = new LinkedList<>();
        for(int a = 0; a < AllTasks.size(); a++) {
            for(int b = 0; b < AllTasks.get(a).size(); b++) {
                LinesToWrite.add("t " + AllTasks.get(a).get(b).getNAME());
            }
        }
        LinesToWrite.add("\n");
        for(int a = 0; a < AllTasks.size(); a++) {
            for(int b = 0; b < AllTasks.get(a).size(); b++) {
                for(int c = 0; c < AllTasks.get(a).get(b).getPres().size(); c++) {
                    LinesToWrite.add("r " + AllTasks.get(a).get(b).getPre(c).getNAME() + "\t" + AllTasks.get(a).get(b).getNAME());
                }
                if(AllTasks.get(a).get(b).getPermPost() != null && !AllTasks.get(a).get(b).getPermPost().isFlaggedFromPerms()) {
                    LinesToWrite.add("p " + AllTasks.get(a).get(b).getNAME() + "\t" + AllTasks.get(a).get(b).getPermPost().getNAME());
                }
            }
        }
        try {
            TaskWriter = new BufferedWriter(new FileWriter(FileName));
            for (String i : LinesToWrite) {
                TaskWriter.append(i);
                TaskWriter.newLine();
            }
            TaskWriter.flush();
            TaskWriter.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            UI.spawnErrorBox("could not save to file.");
            return false;
        }
        return true;
    }
    
    public ObservableList<LinkedList<Task>> getTaskList() { 
        return AllTasks; }
    
    public LinkedList<Task> getLevel(int i) {
        return AllTasks.get(i); 
    }

    /**
     * TODO: overload/override
     * @param t
     * @return true if both tasklists are the same
     */
    public boolean equals(TaskList t) {
        if(this.AllTasks.size() != t.AllTasks.size()) {
            System.err.println("TaskList.equals: error 1");
            return false; 
        }
        for(int a = 0; a < this.AllTasks.size(); a++) {
            if(this.AllTasks.get(a).size() != t.AllTasks.get(a).size()) {
                System.err.println("TaskList.equals: error 2");
                return false;
            }
            for(int b = 0; b < this.AllTasks.get(a).size(); b++) {
                if(!this.AllTasks.get(a).get(b).eq(t.AllTasks.get(a).get(b).getNAME())) {
                    System.err.println("TaskList.equals: error 3");
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean taskExists(Task t) {
        for(LinkedList<Task> level : AllTasks) {
            if(level.stream().anyMatch(y -> y.is_ID(t.uniqueID))) return true;
        }
        return false;
    }

    public boolean taskExists(String s) {
        for(LinkedList<Task> level: AllTasks) {
            if(level.stream().anyMatch(y -> y.eq(s))) return true;
        }
        return false;
    }
    
    public Task findTask(String s) {
        if(AllTasks.get(0).size() == 0) return null;
        for(int a = 0; a < AllTasks.size(); a++) {
            for(int b = 0; b < AllTasks.get(a).size(); b++) {
                if(AllTasks.get(a).get(b).eq(s)) {
                    return AllTasks.get(a).get(b);
                }
            }
        }
        return null;
    }

    public Task findTask(Task s) {
        if(AllTasks.get(0).size() == 0) return null;
        for(int a = 0; a < AllTasks.size(); a++) {
            for(int b = 0; b < AllTasks.get(a).size(); b++) {
                if(AllTasks.get(a).get(b).is_ID(s.uniqueID)) {
                    return AllTasks.get(a).get(b);
                }
            }
        }
        return null;
    }

    public Task findTaskByID(int id) {
        if(AllTasks.get(0).size() == 0) return null;
        for(int a = 0; a < AllTasks.size(); a++) {
            for(int b = 0; b < AllTasks.get(a).size(); b++) {
                if(AllTasks.get(a).get(b).is_ID(id)) {
                    return AllTasks.get(a).get(b);
                }
            }
        }
        return null;
    }

    public ArrayList<Task> query(String s) {
        ArrayList<Task> queryresults = new ArrayList<>();
        for(int a = 0; a < AllTasks.size(); a++) {
            for(int b = 0; b < AllTasks.get(a).size(); b++) {
                if(AllTasks.get(a).get(b).getNAME().toLowerCase().contains(s.toLowerCase())) {
                    queryresults.add(AllTasks.get(a).get(b));
                }
            }
        }
        return queryresults;
    }
    
    public boolean isTopListed(String TaskToFind) {
        return AllTasks.get(0).contains(findTask(TaskToFind));
    }

    /**
     * allocates a new empty LinkedList to the end of the structure in order to hold entries pushed deeper down
     * @param x
     * @return the input variable x is now the max depth of the list.
     */
    public int ensureSize(int x) {
        while(AllTasks.size() <= x) AllTasks.add(new LinkedList<>());
        return x;
    }

    /**
     * when a task is marked complete, or a pre or post is added, depths of tasks may change. This resorts/rearranges the tasks into their appropriate level.
     */
    public void reSort() {
        for(int a = 0; a < AllTasks.size(); a++) {
            for(int b = 0; b < AllTasks.get(a).size(); b++) {
                Task task = AllTasks.get(a).get(b);
                int depth = task.getDepth();
                if(depth != a) {
                    //if(task.getPermPre() != null || task.getPermPost() != null) System.out.println("'" + task.getNAME() + "' out of order; moving to depth of " + depth);
                    AllTasks.get(a).remove(b);
                    AllTasks.get(ensureSize(depth)).add(task);
                    task.getPriority();
                    if(a > depth) {
                        a = depth;
                        b = 0;
                    }
                    else b--;
                }
            }
        }
    }
    
    public final boolean addTask(String s) {
        return addTask_core(new Task(s));
    }

    //TODO: review, clean, test
    public final boolean addTask_core(Task t) {
        if(t == null) {
            return false;
        }
        if(this.taskExists(t)) {
            return false;
        }
        AllTasks.get(0).add(t);
        t.setList(this);
        t.getDepth();
        total_size++;
        return true;
    }
    
    public final boolean addTask(Task t) {
        return addTask_core(t);
    }

    public final boolean addPerm_core(Task A, Task B) {
        if(A == null || B == null) {
            System.err.println("tasks not found.");
            return false;
        }
        if (A.setPermPost(B)) { return true; }
        System.err.println("error.");
        return false;
    }

    public final boolean addRule_core(Task A, Task B) {
        if(A == null || B == null) {
            System.err.println("Tasks not found.");
            return false;
        }
        if(A.addPost(B)) { return true; }
        System.err.println("error.");
        return false;
    }

    //TODO: review, clean, test
    public final boolean addRule(String TaskA, String TaskB) {
        Task A = findTask(TaskA);
        Task B = findTask(TaskB);
        if(!addRule_core(A, B)) {
            UI.spawnErrorBox("error adding a rule.");
            return false;
        }
        return true;
    }

    //TODO: review, clean, test
    public final boolean addPerm(String TaskA, String TaskB) {
        Task A = findTask(TaskA);
        Task B = findTask(TaskB);
        if(!addPerm_core(A, B)) {
            UI.spawnErrorBox("error adding a perm.");
            return false;
        }
        return true;
    }

    public void deleteTask(Task t) {
        if (AllTasks.isEmpty()) {
            UI.spawnErrorBox("the list looks empty.");
            return;
        }
        if (t == null) {
            UI.spawnErrorBox("no task selected.");
            return;
        }
        if (!taskExists(t)) {
            UI.spawnErrorBox("task wasn't located.");
            return;
        }
        int idepth = t.getDepth();

        if(t.flaggedFromPerms) {
            if(t.getPermPost() != null) {
                t.flagPerm();
            }
            else t.flaggedFromPerms = false;
        }

        while(!t.DEPENDENCY.isPreEmpty()) {
            t.getPre(0).removePost(t);
        }

        if(!t.DEPENDENCY.isPermPreEmpty()) {
            t.getPermPre().removePermPost();
        }

        while(!t.DEPENDENCY.isPostEmpty()) {
            t.getPost(0).removePre(t);
        }

        if(!t.DEPENDENCY.isPermPostEmpty()) {
            t.DEPENDENCY.getPermPost().removePermPre();
        }

        AllTasks.get(idepth).remove(t);
        total_size--;
    }

    /**
     * Generates a LinkedList of TreeItems, which are renderable in JavaFX
     * @return
     */
    public LinkedList<TreeItem<Task>> toTree() {
        if(AllTasks.isEmpty()) return new LinkedList<>();
        LinkedList<TreeItem<Task>> tl2tree = new LinkedList<>();
        AllTasks.get(0).forEach(x -> tl2tree.add(x.toTreeItem()));
        return tl2tree;
    }

    public int getNextUniqueTaskID() {
        return ++nextUniqueTaskID;
    }
    
    public TaskDescIterator getDescIterator() {
        return new TaskDescIterator(this);
    }
    
    public TaskAscIterator getAscIterator() {
        return new TaskAscIterator(this);
    }

    //TODO: check total_size variable and see what it is used for
    private int total_size;
    public int getTotalSize() {
        return total_size;
    }

    public void markComplete(Task t) {
        if(t == null) return;
        for(int i = 0; i < t.getPosts().size(); i++)
            if(!t.getPost(i).getNAME().startsWith("PERM: ")) t.removePost(t.getPost(i--));
        if(t.getPermPost() != null) t.getPermPost().flagPerm();
        else deleteTask(t);
    }
}
