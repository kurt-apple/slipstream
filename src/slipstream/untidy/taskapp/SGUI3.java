package slipstream.untidy.taskapp;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import slipstream.untidy.taskdb.TaskList;

public class SGUI3 extends Application {
    private static TaskList mainList;
    private static Stage mainStage;
    public static TaskList getList() { return mainList; }
    public static Rectangle2D getMainScreenBounds() {
        if(mainStage == null) return null;
        return Screen.getScreensForRectangle(mainStage.getX(), mainStage.getY(), mainStage.getWidth(), mainStage.getHeight()).get(0).getVisualBounds();
    }

    public static void newList(String FileName) { mainList = new TaskList(FileName); }

    public static void main(String[] args) { launch(args); }
    @Override public void start(Stage stage) throws Exception {
        mainList = new TaskList();
        TidierFXML loadlistT = new TidierFXML("FXMLTaskView.fxml", Modality.NONE);
        mainStage = loadlistT.getStage();
        mainStage.setTitle("TDL BETA");
        mainStage.setMaximized(true);
        mainStage.show();
}   }
