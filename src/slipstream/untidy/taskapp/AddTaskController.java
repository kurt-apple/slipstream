package slipstream.untidy.taskapp;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import slipstream.tidy.utils.Pips;
import slipstream.tidy.utils.UI;
import slipstream.untidy.taskdb.Task;
import slipstream.untidy.taskdb.TaskList;

//TODO: change all lists to Task instead of String. Display Task.getNAME.
//TODO: use searches by ID instead of by string name.

public class AddTaskController implements Initializable {
    @FXML ListView<Task>
            NAMESEARCH, 
            PRESEARCH, 
            POSTSEARCH, 
            PERMPRESEARCH, 
            PERMPOSTSEARCH, 
            PRELIST, 
            POSTLIST, 
            PERMPRELIST, 
            PERMPOSTLIST;
    
    @FXML TextField 
            TASKNAME, 
            PRESEARCHFIELD, 
            POSTSEARCHFIELD, 
            PERMPRESEARCHFIELD, 
            PERMPOSTSEARCHFIELD;
    
    @FXML Button 
            savetask, 
            canceltask, 
            validate, 
            searchpre, 
            searchpost, 
            PERMsearchpre, 
            PERMsearchpost, 
            addselectedpre, 
            addselectedpost, 
            PERMaddselectedpre,
            PERMaddselectedpost, 
            addnewpre, 
            addnewpost, 
            PERMaddnewpre,
            PERMaddnewpost,
            removepre, 
            removepost,
            PERMremovepre, 
            PERMremovepost;
            
    @FXML Label addTasklabel;
    Task t;
    
    private ObservableList<Task>
            namequeryol, 
            prequeryol, 
            postqueryol, 
            permprequeryol, 
            permpostqueryol, 
            prelistol, 
            postlistol, 
            permprelistol, 
            permpostlistol;
    
    TaskList mainref;
    boolean isModify = false;
    
    public static ObservableList<Task> newOL() {
        return FXCollections.observableList(new ArrayList<>());
    }
    public void OL(ObservableList<Task> ol) { ol = newOL(); }

    @Override public void initialize(URL url, ResourceBundle rb) {
        mainref = SGUI3.getList();

        namequeryol = newOL();
        prequeryol = newOL();
        postqueryol = newOL();
        prelistol = newOL();
        postlistol = newOL();
        permprequeryol = newOL();
        permpostqueryol = newOL();
        permprelistol = newOL();
        permpostlistol = newOL();

        NAMESEARCH.setItems(namequeryol);
        PRESEARCH.setItems(prequeryol);
        POSTSEARCH.setItems(postqueryol);
        PRELIST.setItems(prelistol);
        POSTLIST.setItems(postlistol);
        PERMPRESEARCH.setItems(permprequeryol);
        PERMPOSTSEARCH.setItems(permpostqueryol);
        PERMPRELIST.setItems(permprelistol);
        PERMPOSTLIST.setItems(permpostlistol);

        NAMESEARCH.setCellFactory(Pips.std_factory);
        PRESEARCH.setCellFactory(Pips.std_factory);
        POSTSEARCH.setCellFactory(Pips.std_factory);
        PRELIST.setCellFactory(Pips.std_factory);
        POSTLIST.setCellFactory(Pips.std_factory);
        PERMPRESEARCH.setCellFactory(Pips.std_factory);
        PERMPOSTSEARCH.setCellFactory(Pips.std_factory);
        PERMPRELIST.setCellFactory(Pips.std_factory);
        PERMPOSTLIST.setCellFactory(Pips.std_factory);

        addTasklabel.setText("ADD TASK");
    }

    public ArrayList<Task> MLQ(TextField tf) {
        return mainref.query(tf.getText());
    }
    
    @FXML public void handlevalidate() {
        namequeryol.clear();
        namequeryol.addAll(MLQ(TASKNAME));
    }

    @FXML public void handlepresearch() {
        prequeryol.clear();
        ArrayList<Task> pres = MLQ(PRESEARCHFIELD);
        pres.remove(t.getNAME());
        boolean isPERM = t.getNAME().startsWith("PERM: ");
        //TODO: come up with perm, and come up with bedrock task nomenclature
        //TODO: Tuesday and Wednesday can both be added as pres for the same task
//        pres.removeIf(x -> t.findAbove(x) != null
//                || t.findBelow(x) != null
//                || (!isPERM && x.getNAME().startsWith("PERM: ")));
        pres.removeAll(t.getAllBoth());
        pres.remove(t);
        prequeryol.addAll(pres);
    }
    
    @FXML public void handlepermpresearch() {
        permprequeryol.clear();
        ArrayList<Task> permpres = MLQ(PERMPRESEARCHFIELD);
        permpres.remove(t.getNAME());
        boolean isPERM = t.getNAME().startsWith("PERM: ");
        permpres.removeIf(x -> t.isLoopedWith(x) || (!isPERM && x.getNAME().startsWith("PERM: ")));
        permpres.remove(t);
        permprequeryol.addAll(permpres);
    }
    
    @FXML public void handlepostsearch() {
        postqueryol.clear();
        ArrayList<Task> posts = MLQ(POSTSEARCHFIELD);
        posts.remove(t.getNAME());
        posts.removeAll(t.getAllBoth());
        posts.remove(t);
        postqueryol.addAll(posts);
    }
    
    @FXML public void handlepermpostsearch() {
        permpostqueryol.clear();
        ArrayList<Task> permposts = MLQ(PERMPOSTSEARCHFIELD);
        permposts.remove(t.getNAME());
        permposts.removeIf(x -> t.isLoopedWith(x));
        permposts.remove(t);
        permpostqueryol.addAll(permposts);
    }
    
    public static Task getSelected(ListView<Task> lv) {
        return lv.getSelectionModel().getSelectedItem();
    }
    
    @FXML public void handleaddselectedpre() {
        if(t.addPre(getSelected(PRESEARCH))) {
            prelistol.clear();
            prelistol.addAll(t.getPres());
            handlepresearch();
        }
        else UI.spawnErrorBox("the selected pre couldn't be added");
    }
    
    @FXML public void handleaddselectedpost() {
        if(t.addPost(getSelected(POSTSEARCH))) {
            postlistol.clear();
            postlistol.addAll(t.getPosts());
            handlepostsearch();
        }
        else UI.spawnErrorBox("the selected post couldn't be added");
    }
    
    @FXML public void handleaddselectedpermpre() {
        if(t.setPermPre(getSelected(PERMPRESEARCH))) {
            permprelistol.clear();
            permprelistol.add(t.getPermPre());
            prelistol.clear();
            prelistol.addAll(t.getPres());
            permpostlistol.clear();
            permpostlistol.add(t.getPermPost());
            handlepermpresearch();
        }
    }
    
    @FXML public void handleaddselectedpermpost() {
        //System.out.println(" AddTaskController.handleaddselectedpermpost(): Adding Perm Post.");
        if(t.setPermPost(getSelected(PERMPOSTSEARCH))) {
            permpostlistol.clear();
            permpostlistol.add(t.getPermPost());
            postlistol.clear();
            postlistol.addAll(t.getPosts());
            permprelistol.clear();
            permprelistol.add(t.getPermPre());
            handlepermpostsearch();
        }
    }
    
    @FXML public void handleremovepre() {
        t.removePre(getSelected(PRELIST));
        prelistol.remove(getSelected(PRELIST));
    }
    
    @FXML public void handleremovepost() {
        t.removePost(getSelected(POSTLIST));
        postlistol.remove(getSelected(POSTLIST));
    }
    
    @FXML public void handleremovepermpre() {
        t.removePermPre();
        permprelistol.remove(getSelected(PERMPRELIST));
    }
    
    @FXML public void handleremovepermpost() {
        t.removePermPost();
        permpostlistol.remove(getSelected(PERMPOSTLIST));
    }
    
    @FXML public void handleaddnewpre() {
        Task newpre = UI.spawnMinimalAddTaskBox(t, true, false);
        if(newpre != null) {
            mainref.addTask(newpre);
            if(t.addPre(newpre)) {
                prelistol.clear();
                for(Task iTask : t.getPres()) {
                    prelistol.add(iTask);
                }
            }
        }
    }
    
    @FXML public void handleaddnewpost() {
        Task newpost = UI.spawnMinimalAddTaskBox(t, false, false);
        if(newpost != null) {
            mainref.addTask(newpost);
            if(t.addPost(newpost)) {
                postlistol.clear();
                for(Task iTask : t.getPosts()) {
                    postlistol.add(iTask);
                }
            }
        }
    }
    
    @FXML public void handleaddnewpermpre() {
        Task newpermpre = UI.spawnMinimalAddTaskBox(t, true, true);
        if(newpermpre != null) {
            mainref.addTask(newpermpre);
            if(t.setPermPre(newpermpre)) {
                permprelistol.clear();
                permprelistol.add(t.getPermPre());
                permpostlistol.clear();
                permpostlistol.add(t.getPermPost());
                prelistol.clear();
                for(Task iTask : t.getPres()) {
                    prelistol.add(iTask);
                }
                handlepermpresearch();
            }
        }
    }
    
    @FXML public void handleaddnewpermpost() {
        Task newpermpost = UI.spawnMinimalAddTaskBox(t, false, true);
        if(newpermpost != null) {
            mainref.addTask(newpermpost);
            if(t.setPermPost(newpermpost)) {
                permpostlistol.clear();
                permpostlistol.add(t.getPermPost());
                permprelistol.clear();
                permprelistol.add(t.getPermPre());
                postlistol.clear();
                for(Task iTask : t.getPosts()) {
                    postlistol.add(iTask);
                }
                handlepermpostsearch();
            }
        }
    }

    /*handleSaveTask()
    either close the window after resorting the entire list or add the new task then close.
     */
    @FXML public void handlesavetask() {
        t.setNAME(TASKNAME.getText());
        if(!isModify) {
            if(!mainref.addTask(t)) {
                UI.spawnErrorBox("Task already exists. Choose a different name.");
                return;
            }
        }
        mainref.reSort();
        UI.closeWindow(savetask);
    }

    /*handleCancelTask()
    by clicking cancel, right now the app closes the window,
    but changes are actually kept because there is no copy/sandbox the pending changes are stored in before goind live.
     */
    //TODO: actually have a sandboxed editor environment until changes are confirmed with user, else revert properly to prior state
    @FXML public void handlecanceltask() {
        if(isModify) {
            if(UI.spawnChoiceBox("are you sure you want to cancel modifying task?")) {
                UI.closeWindow(canceltask);
        }   }
        else if(UI.spawnChoiceBox("are you sure you want to cancel adding task?")) {
            UI.closeWindow(canceltask);
    }   }
    
    public void passTask(Task tomodify, boolean isModify) {
        t = tomodify;
        if(t.isFlaggedFromPerms()) System.out.println("flagged from perms.");
        this.isModify = isModify;
        TASKNAME.setText(t.getNAME());
        for(Task iTask : t.getPres()) 
            PRELIST.getItems().add(iTask);
        for(Task iTask : t.getPosts()) 
            POSTLIST.getItems().add(iTask);
        if(t.getPermPre() != null) PERMPRELIST.getItems().add(t.getPermPre());
        if(t.getPermPost() != null) PERMPOSTLIST.getItems().add(t.getPermPost());
        if(isModify) addTasklabel.setText(t.isFlaggedFromPerms() ? "flagged from perms" : "not flagged from perms");
    }
}
