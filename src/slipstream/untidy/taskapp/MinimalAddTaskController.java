/*
 * This license header is intentionally left blank.
 */
package slipstream.untidy.taskapp;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import slipstream.tidy.utils.UI;
import slipstream.untidy.taskdb.Task;

public class MinimalAddTaskController implements Initializable {
    Task originalTask;
    private static Task createdTask;
    boolean isAbove;
    boolean isPerm;
    public void receiveInfo(Task org, boolean NewisAbove, boolean NewisPerm) {
        this.isAbove = NewisAbove;
        this.isPerm = NewisPerm;
        this.originalTask = org;
    }
    @FXML public TextField NEWTASKFIELD;
    @FXML public Button addButton, cancelButton;
    Task temptask;
    @Override public void initialize(URL url, ResourceBundle rb) {}
    @FXML public void handleAdd() {
        if(SGUI3.getList().findTask(NEWTASKFIELD.getText()) != null) {
            UI.spawnErrorBox("Task already exists.");
            createdTask = null;
        }
        else if(!NEWTASKFIELD.getText().isEmpty()) {
            createdTask = new Task(NEWTASKFIELD.getText());
            UI.closeWindow(addButton);
        }
    }
    @FXML public void handleCancel() {
        if(UI.spawnChoiceBox("Are you sure you want to cancel?")) {
            createdTask = null;
            UI.closeWindow(cancelButton);
        }
    }
    public static Task getCreatedTask() {
        return createdTask;
    }
}
