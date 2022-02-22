/*
 * This license header is intentionally left blank.
 */

package slipstream.untidy.taskdb;

import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;

public class CustomTreeItem extends TreeItem<Task> {
    private boolean isFirstTimeChildren = true;
    
    private static Comparator<TreeItem<Task>> priorityComparator2 = (x, y) -> x.getValue().getPriority() > y.getValue().getPriority() ? -1 : 1;

    //TODO: remove perm post treeitems if they can be found below any of the non perm posts.
    //TODO: ********************************************************************************
    //TODO: Custom Tree Items occasionally are unclickable.
    //TODO: see if tests can be made for this
    //TODO: make sure subtasks are sorted as intended
    private ObservableList<TreeItem<Task>> buildChildren(TreeItem<Task> ti) {
        Task t = ti.getValue();
        if(t != null && !t.isBottom()) {
            ObservableList<TreeItem<Task>> children = FXCollections.observableArrayList();
            t.getPosts().stream().forEach(x -> children.add(new CustomTreeItem(x)));
            if(t.getPermPost() != null && !t.getPermPost().flaggedFromPerms) {
                boolean isNotPreceded = true;
                for(Task tt : t.getPosts()) {
                    if(tt.findBelow(t.getPermPost()) != null) {
                        isNotPreceded = false;
                        break;
                    }
                }
                if(isNotPreceded) {
                    children.add(new CustomTreeItem(t.getPermPost()));
                }
            }
            if(children.size() > 0) {
                children.sort(priorityComparator2);
                return children;
            }
        }
        return FXCollections.emptyObservableList();
    }
    
    @Override public ObservableList<TreeItem<Task>> getChildren() {
        if(isFirstTimeChildren) {
            isFirstTimeChildren = false;
            super.getChildren().setAll(buildChildren(this));
        }
        return super.getChildren();
    }

    @Override public boolean isLeaf() {
        return this.getValue().isBottom();
    }

    private final EventHandler<TreeItem.TreeModificationEvent<Task>> branchCollapsedHandler =
            te -> setExpanded(getValue().isImportantInSomeWay());

    public CustomTreeItem(Task t) {
        super(t);
        setExpanded(t.isExpanded);
        addEventHandler(TreeItem.branchExpandedEvent(), x -> t.isExpanded = true);
        addEventHandler(TreeItem.branchCollapsedEvent(), branchCollapsedHandler);
    }
}