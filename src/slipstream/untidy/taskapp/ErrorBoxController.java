package slipstream.untidy.taskapp;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import slipstream.tidy.utils.UI;

public class ErrorBoxController implements Initializable {
    @FXML private Label boxMessage;
    @FXML private Button okButton;
    public void setMessage(String message) {
        boxMessage.setText(message);
    }
    @Override public void initialize(URL url, ResourceBundle rb) { 
        boxMessage.setText("no message set");
    }
    @FXML public void handleOkButtonAction() {
        UI.closeWindow(okButton);
    }
}
