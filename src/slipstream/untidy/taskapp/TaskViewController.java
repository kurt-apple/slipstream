/*
 * This license header is intentionally left blank.
 */
package slipstream.untidy.taskapp;

import java.io.File;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.function.Function;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import slipstream.untidy.taskdb.Task;
import slipstream.untidy.utils.UI;
import slipstream.tidy.utils.Pips;

public class TaskViewController implements Initializable {
    @FXML TreeTableView<Task> tasktable;
    @FXML static TreeItem<Task> root;
    @FXML TreeTableColumn<Task, String> tasknamecol;
    @FXML TreeTableColumn<Task, Integer> taskprioritycol;
    @FXML Button addTaskButton, saveButton, exitButton, modifyTaskButton, 
            deleteButton, BoggleButton;

    private static Comparator<TreeItem<Task>> priorityComparator = (x, y) -> x.getValue().getPriority() > y.getValue().getPriority() ? -1 : 1;

    private PseudoClass childOfSelected     = PseudoClass.getPseudoClass("child-of-selected");
    private PseudoClass parentOfSelected    = PseudoClass.getPseudoClass("parent-of-selected");
    private PseudoClass needsBreakdown      = PseudoClass.getPseudoClass("needs-breakdown");
    private PseudoClass wow_so_important    = PseudoClass.getPseudoClass("wow-so-important");
    private PseudoClass also_important      = PseudoClass.getPseudoClass("implication-important");

    private TreeItem<Task> getSelectedItem() {
        return tasktable.getSelectionModel().getSelectedItem();
    }

    private ObservableList<TreeItem<Task>> getSelectedItems() {
        return tasktable.getSelectionModel().getSelectedItems();
    }

    public static void populate_list() {
        //System.out.println("populateList()");
        SGUI3.getList().reSort();
        root.getChildren().clear();
        root.getChildren().addAll(SGUI3.getList().toTree());
        //refresh priority calculation
        //System.out.println("refresh priority calculation in populateList");
        root.getChildren().forEach(x -> x.getValue().getPriority());
        root.getChildren().sort(priorityComparator);
        return;
    }

    @Override public void initialize(URL url, ResourceBundle rb) {
        root = new TreeItem<>();
        tasktable.setFixedCellSize(25.0);
        tasktable.setRoot(root);
        tasktable.showRootProperty().setValue(Boolean.FALSE);
        tasktable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //https://stackoverflow.com/questions/39078471/apply-css-on-specific-rows-on-javafx
        tasktable.setRowFactory(ttv -> {
            TreeTableRow<Task> row = new TreeTableRow<Task>() {
                @Override protected void updateItem(Task t, boolean empty) {
                    super.updateItem(t, empty);
                    if (empty) {
                        Pips.disableCSS(this, parentOfSelected);
                        Pips.disableCSS(this, childOfSelected);
                        Pips.disableCSS(this, needsBreakdown);
                        Pips.disableCSS(this, wow_so_important);
                        Pips.disableCSS(this, also_important);
                    } else updateState(this);
                }
            };

            getSelectedItems().addListener(
                    (Change<? extends TreeItem<Task>> c) -> updateState(row));
            return row;
        });

        tasknamecol = column("NAME", Task::NAMEProperty);
        taskprioritycol = new TreeTableColumn<>("Priority");
        taskprioritycol.setCellValueFactory(d -> d.getValue().getValue().priorityProperty().asObject());
        tasktable.getColumns().add(tasknamecol);
        tasktable.getColumns().add(taskprioritycol);
        taskprioritycol.prefWidthProperty().setValue(75);
        tasknamecol.prefWidthProperty().bind(tasktable.widthProperty().subtract(taskprioritycol.prefWidthProperty()));
        //StackOverflow @Denis /3310688/ beersOwed++
        tasktable.setOnSort(x -> tasktable.getSelectionModel().clearSelection());
        tasktable.setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
    }

    private <S,T> TreeTableColumn<S,T> column(String title, Function<S, ObservableValue<T>> property) {
        TreeTableColumn<S,T> column = new TreeTableColumn<>(title);
        column.setCellValueFactory(cellData -> property.apply(cellData.getValue().getValue()));
        return column ;
    }

    private MenuItem setWBS;
    private MenuItem markDone;
    private MenuItem flagImportant;
    private ContextMenu taskMenu;

    private void setTaskContextMenu(TreeTableRow<Task> row) {
        setWBS = new MenuItem("toggle needs breakdown");
        markDone = new MenuItem("mark complete");
        flagImportant = new MenuItem("flag as important");
        setWBS.setOnAction(e -> row.getItem().flipBreakdown());
        markDone.setOnAction(e -> handleMarkComplete(row.getItem()));
        flagImportant.setOnAction(e -> row.getItem().flipImportance());
        taskMenu = new ContextMenu();
        taskMenu.getItems().addAll(setWBS, markDone, flagImportant);
        row.setContextMenu(taskMenu);
    }

    private void expandify(TreeItem<Task> item) {
        item.getValue().makeExpanded();
        item.setExpanded(true);
    }

    private void updateState(TreeTableRow<Task> row) {
        TreeTableView<Task> table = row.getTreeTableView() ;
        TreeItem<Task> item = row.getTreeItem();

        if(item == null) {
            Pips.disableCSS(row,
                    parentOfSelected,
                    childOfSelected,
                    needsBreakdown,
                    wow_so_important,
                    also_important);
            return;
        }
        
        Task task = item.getValue();
        if(task == null) {
            setTaskContextMenu(row);
            return;
        }

        if(getSelectedItem() != null) {
            if(getSelectedItem().equals(item)) {
                Pips.disableCSS(row,
                        parentOfSelected,
                        childOfSelected,
                        needsBreakdown,
                        wow_so_important,
                        also_important);
                return;
            }

            Pips.decideIf(row, t -> t.getBreakdown() > 0, needsBreakdown);

            if(task.getImportance() > 0) {
                Pips.enableCSS(row, wow_so_important);
                expandify(item);
                return;
            }
            Pips.disableCSS(row, wow_so_important);

            if(task.isPreOfImportantTask()) {
                Pips.enableCSS(row, also_important);
                expandify(item);
                return;
            }
            Pips.disableCSS(row, also_important);

            // check to see if item is parent of any selected item:
            for (TreeItem<Task> parent = getSelectedItem().getParent(); parent != null; parent = parent.getParent()) {
                if (parent == item) {
                    Pips.enableCSS(row, parentOfSelected);
                    Pips.disableCSS(row, childOfSelected);
                    return;
                }
            }

            // check to see if item is child of any selected item:
            for (TreeItem<Task> ancestor = item.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
                if (getSelectedItem().equals(ancestor)) {
                    Pips.enableCSS(row, childOfSelected);
                    Pips.disableCSS(row, parentOfSelected);
                    return;
                }
            }
        }

        Pips.disableCSS(row, childOfSelected, parentOfSelected);
    }

    @FXML public void handleAddTaskButton() {
        tasktable.getSelectionModel().clearSelection();
        UI.spawnAddTaskBox(new Task(), false);
        populate_list();
    }
    
    @FXML public void handleExitButton() {
        if(SGUI3.getList().isOpen()) {
            if(UI.spawnChoiceBox("Do you want to save before you exit?")) {
                SGUI3.getList().saveFile();
            }
        }
        UI.closeWindow(exitButton);
    }
    
    @FXML public void handleSaveButton() {
        tasktable.getSelectionModel().clearSelection();
        if(!SGUI3.getList().getFileName().equalsIgnoreCase("")) {
            if(!SGUI3.getList().saveFile()) {
                UI.spawnErrorBox("Error saving file.");
                return;
            }
            UI.spawnErrorBox("Successfully saved file.");
        }
        else {
            UI.spawnErrorBox("No file open yet.");
        }
    }
    
    @FXML public void handleDeleteButton() {
        if(UI.spawnChoiceBox("Are you sure you want to delete the selected task(s)?")) {
            //TODO: figure out how to handle done lists
            //UI.appendDoneList(selectedItem.getValue().getNAME());
            SGUI3.getList().deleteTask(getSelectedItem().getValue());
            tasktable.getSelectionModel().clearSelection();
            populate_list();
        }
    }
    
    @FXML public void handleOpenButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        Stage filestage = new Stage();
        UI.centerStage(filestage);
        File file = fileChooser.showOpenDialog(filestage);
        if(file != null) {
            SGUI3.newList(file.getAbsolutePath());
            populate_list();
    }   }

    //todo: nullpointerexception maybe needs to be caught at getvalue by initializing before spawn box
    @FXML public void handleModifyTaskButton() {
        try { UI.spawnAddTaskBox(getSelectedItem().getValue(), true);
        } catch (NullPointerException e) {
            e.printStackTrace(System.err);
            UI.spawnErrorBox("No task is selected.");
            tasktable.getSelectionModel().clearSelection();
            return;
        }
        tasktable.getSelectionModel().clearSelection();
        populate_list();
    }
    
    @FXML Button searchButton;
    @FXML public void searchTasks() {
        tasktable.getSelectionModel().clearSelection();
        TidierFXML txx = new TidierFXML("FXMLSearchWindow.fxml", Modality.APPLICATION_MODAL);
        UI.centerStage(txx.getStage());
        txx.getStage().showAndWait();
        populate_list();
    }

    @FXML public void handleBoggle() {
        tasktable.getSelectionModel().clearSelection();
        if(!SGUI3.getList().isOpen()) {
            UI.spawnErrorBox("No file is open.");
            return;
        }
        else {
            TDLMomentController.recieveTaskListReference(null);
            if(!TDLMomentController.init()) return;
        }
        TidierFXML txx = new TidierFXML("FXMLTDLMoment.fxml", Modality.APPLICATION_MODAL);
        UI.centerStage(txx.getStage());
        txx.getStage().showAndWait();
        populate_list();
    }

    public void handleMarkComplete(Task t) {
        if(!SGUI3.getList().isOpen()) {
            UI.spawnErrorBox("No file is open.");
            return;
        }
        else {
            SGUI3.getList().markComplete(t);
            tasktable.getSelectionModel().clearSelection();
            populate_list();
        }
    }
}
