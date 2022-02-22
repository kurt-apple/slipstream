package slipstream.tidy.utils;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import slipstream.untidy.taskapp.*;
import slipstream.untidy.taskdb.Task;

public class UI {
    /*
    spawnChoiceBox(String message)
    A pop-up that asks for "Yes" or "No" to the message provided as argument.
    Yes: return true
    No: return false
     */
    public static boolean spawnChoiceBox(String message) {
        TidierFXML choiceBoxFXML = new TidierFXML("FXMLChoiceBox.fxml", Modality.APPLICATION_MODAL);
        ChoiceBoxController choiceBoxController = ((ChoiceBoxController) choiceBoxFXML.getController());
        choiceBoxController.setMessage(message);
        centerStage(choiceBoxFXML.getStage());
        choiceBoxFXML.getStage().showAndWait();
        /*
        testing non-static choice variable;
        if errors arise then check initialization point and/or revert to static
         */
        return choiceBoxController.getChoice();
    }
    /*
    spawnErrorBox(String message)
    Displays a pop-up with a short message (doesn't really have to be an error
     */
    public static void spawnErrorBox(String message) {
        TidierFXML errorBoxFXML = new TidierFXML("FXMLErrorBox.fxml", Modality.APPLICATION_MODAL);
        ((ErrorBoxController) errorBoxFXML.getController()).setMessage(message);
        centerStage(errorBoxFXML.getStage());
        errorBoxFXML.getStage().showAndWait();
    }
    /*
    closeWindow(Button b)
    Takes input of a button that resides on the target window,
    Gathers reference for the window, then closes it.
     */
    public static void closeWindow(Button b) {
        ((Stage) b.getScene().getWindow()).close();
    }
    /*
    spawnAddTaskBox(Task t, boolean isModify)
    Spawns the add task interface.
     */
    public static void spawnAddTaskBox(Task t, boolean isModify) {
        TidierFXML addTaskBoxFXML = new TidierFXML("FXMLAddTask.fxml", Modality.APPLICATION_MODAL);
        ((AddTaskController)addTaskBoxFXML.getController()).passTask(t, isModify);
        centerStage(addTaskBoxFXML.getStage());
        addTaskBoxFXML.getStage().showAndWait();
    }
    /*
    spawnMinimalAddTaskBox(Task t, boolean isAbove)
    Spawns the minimal add task interface used to quickly add pres and posts.
     */
    public static Task spawnMinimalAddTaskBox(Task t, boolean isAbove, boolean isPerm) {
        TidierFXML minimalAddTaskFXML = new TidierFXML("FXMLMinimalAddTask.fxml", Modality.APPLICATION_MODAL);
        MinimalAddTaskController minAdd = ((MinimalAddTaskController) minimalAddTaskFXML.getController());
        minAdd.receiveInfo(t, isAbove, isPerm);
        centerStage(minimalAddTaskFXML.getStage());
        minimalAddTaskFXML.getStage().showAndWait();
        return minAdd.getCreatedTask();
    }

    public static void centerStage(Stage s) {
         try {
             Rectangle2D b = SGUI3.getMainScreenBounds();
             s.setX(b.getMinX());
             s.setY(b.getMinY());
             s.centerOnScreen();
         } catch (NullPointerException e) {
             e.printStackTrace(System.err);
         }
    }
}
