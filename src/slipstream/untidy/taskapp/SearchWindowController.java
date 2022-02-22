package slipstream.untidy.taskapp;

import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import slipstream.tidy.utils.Pips;
import slipstream.tidy.utils.UI;
import slipstream.untidy.taskdb.Task;

public class SearchWindowController implements Initializable {
    @FXML Button searchButton, modifyButton, srchback, markComplete;
    @FXML TextField searchField;
    @FXML TableView<Task> searchResults;
    ObservableList<Task> searchResultsOL;
    TableColumn<Task, String> nameColumn;
    TableColumn<Task, String> pathColumn;

    @Override public void initialize(URL url, ResourceBundle rb) {
        nameColumn = new TableColumn<>("Task Name");
        pathColumn = new TableColumn<>("Path to Task");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("NAME"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("PATH"));
        searchResults.getColumns().setAll(nameColumn, pathColumn);
    }

    @FXML public void modifyTask() { 
        Task x = searchResults.getSelectionModel().getSelectedItem();
        UI.spawnAddTaskBox(x, true);
    }

    @FXML public void markTaskComplete() {
        Task x = searchResults.getSelectionModel().getSelectedItem();
        if(x == null) return;
        for(int i = 0; i < x.getPosts().size(); i++)
            if(!x.getPost(i).getNAME().startsWith("PERM: ")) x.removePost(x.getPost(i--));
        if(x.getPermPost() != null) x.getPermPost().flagPerm();
        else SGUI3.getList().deleteTask(x);
        searchResults.getSelectionModel().clearSelection();
        searchTaskList();
    }

    @FXML public void srchbackhandle() { UI.closeWindow(srchback); }
    @FXML public void searchTaskList() {
        String query = searchField.getText();
        searchResults.getItems().clear();
        searchResults.setItems(FXCollections.observableList(SGUI3.getList().query(query)));
}   }
