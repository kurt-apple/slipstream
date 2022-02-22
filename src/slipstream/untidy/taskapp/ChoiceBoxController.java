package slipstream.untidy.taskapp;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import slipstream.tidy.utils.UI;

public class ChoiceBoxController implements Initializable {
    @FXML private Label boxMessage;
    @FXML private Button yesButton, noButton;
    private boolean choice;
    @Override public void initialize(URL url, ResourceBundle rb) {
        choice = false;
        boxMessage.setText("no message set");
    }
    public void setMessage(String message) {
        boxMessage.setText(message);
    }
    @FXML public void handleNoButton() {
        choice = false;
        UI.closeWindow(noButton);
    }
    @FXML public void handleYesButton() {
        choice = true;
        UI.closeWindow(yesButton);
    }
    public boolean getChoice() {
        return choice;
    }
}
