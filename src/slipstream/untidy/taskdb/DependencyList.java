package slipstream.untidy.taskdb;

import java.util.LinkedList;

class DependencyList {
    private LinkedList<Task> ABOVE;
    private LinkedList<Task> BELOW;
    private Task PermPre;
    private Task PermPost;
    public DependencyList() {
        ABOVE = new LinkedList<>();
        BELOW = new LinkedList<>();
        PermPre = null;
        PermPost = null;
    }
    public void updateTo(LinkedList<Task> ABOVE, LinkedList<Task> BELOW, Task permpre, Task permpost) {
        this.ABOVE.clear();     this.ABOVE.addAll(ABOVE);
        this.BELOW.clear();     this.BELOW.addAll(BELOW);
        PermPre = permpre;      PermPost = permpost;
    }
    public boolean removePre(Task toRemove) {
        return ABOVE.remove(toRemove);
    }
    public boolean removePost(Task toRemove) {
        return BELOW.remove(toRemove);
    }
    public void removePermPre() {
        PermPre = null;
    }
    public void removePermPost() {
        PermPost = null;
    }
    public LinkedList<Task> getPres() {
        return ABOVE;
    }
    public LinkedList<Task> getPosts() {
        return BELOW;
    }
    public Task getPermPre() {
        return PermPre;
    }
    public Task getPermPost() {
        return PermPost;
    }
    public void addPre(Task t) {
        ABOVE.push(t);
    }
    public void addPost(Task t) {
        BELOW.push(t);
    }
    public void setPermPre(Task t) {
        PermPre = t;
    }
    public void setPermPost(Task t) {
        PermPost = t;
    }
    public Task getPre(int i) {
        return ABOVE.get(i);
    }
    public Task getPost(int i) {
        return BELOW.get(i);
    }
    public boolean isPreEmpty() {
        return ABOVE.isEmpty();
    }
    public boolean isPostEmpty() {
        return BELOW.isEmpty();
    }
    public boolean isPermPostEmpty() {
        return PermPost == null;
    }
    public boolean isPermPreEmpty() {
        return PermPre == null;
    }
}
