package slipstream.untidy.taskapp;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/*TidierFXML
a class designed to shorten the process to open new windows.
 */
//TODO find a way to make tests for FXML features like this one.
public class TidierFXML {
    private FXMLLoader a;
    private Stage      b;
    public TidierFXML(FXMLLoader xLoader, Stage xStage) {
        a = xLoader;
        b = xStage;
    }
    public TidierFXML(String url, Modality mode) {
        a = new FXMLLoader(TidierFXML.class.getResource(url));
        b = new Stage();
        b.initModality(mode);
        try {
            b.setScene(new Scene(a.load()));
            b.getScene().getStylesheets().add("stylesheet.css");
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(699);
        }
    }
    public TidierFXML(String url) {
        a = new FXMLLoader(TidierFXML.class.getResource(url));
        b = new Stage();
        b.initModality(Modality.APPLICATION_MODAL);
        try {
            b.setScene(new Scene(a.load()));
            b.getScene().getStylesheets().add("stylesheet.css");
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(698);
        }
    }
    public FXMLLoader getLoader() { return a; }
    public Stage       getStage() { return b; }
    public <T>T getController() { return a.getController(); }
}
