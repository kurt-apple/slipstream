package slipstream.untidy.taskapp;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import slipstream.tidy.utils.UI;
import slipstream.untidy.taskdb.Task;
import slipstream.untidy.taskdb.TaskList;

public class TDLMomentController implements Initializable {
    @FXML Button leftChoiceButton, rightChoiceButton, skipButton;
    @FXML Label TaskLabel;
    private String slabel;
    public static Task taskA, taskB;
    private static int Am, Bm, AAm, BBm, Ai, Bi, AAi, BBi, wsizei;
    private static int tsa, tsb;
    //get "girth" of list, return min(x, girth)
    private static int wsize(int x) {
        int i;
        int j = 40;
        int k = 0;
        if(taskList == null) {
            for(i = 0; i < SGUI3.getList().getTaskList().size() && j > 0; i++) {
                k = lsize(i);
                if(k == 0) {
                    break;
                }
                j -= k;
            }
            return Math.min(x, i);
        }
        for(i = 0; i < taskList.getTaskList().size() && j > 0; i++) {
            k = lsize(i);
            if(k == 0) {
                break;
            }
            j -= k;
        }
        return Math.min(x, i);
    }
    public static int lsize(int level) { 
        if(taskList == null) return SGUI3.getList().getLevel(level).size();
        else return taskList.getLevel(level).size(); }

    //TODO: it is initiating invalid rules.
    public static boolean init_core() {
        
        taskA = null;
        taskB = null;
        
        //first, if there are tasks on top with no posts, select them one at a time.
        if(taskList == null) {
            for(Task t : SGUI3.getList().getLevel(0)) {
                if(t.isBottom() && !t.getNAME().startsWith("PERM: ")) {
                    taskA = t;
                    break;
                }
            }
        }
        else {
            for(Task t : taskList.getLevel(0)) {
                if(t.isBottom() && !t.getNAME().startsWith("PERM: ")) {
                    taskA = t;
                    break;
                }
            }
        }
        
        SecureRandom rnd;
        try { rnd = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Trouble making algorithms.");
            return false;
        }
        
        //second, if the top layers are too broad, select tasks from fewer layers.
        wsizei = wsize(5);
        if(wsizei == 0) { 
            System.err.println("list is empty.");
            return false;
        }
        if(wsizei <= 2 && lsize(0) == 1) {
            System.err.println("list doesn't have enough in it to use this function.");
            return false;
        }
        
        //third, pseudorandomly select other tasks.
        
        Am = rnd.nextInt(wsizei);
        Bm = rnd.nextInt(wsizei);
        for(int a = 0; a < wsizei; a++) {
            for(int b = 0; b < wsizei; b++) {
                Ai = (a+Am)%wsizei;
                Bi = (b+Bm)%wsizei;
                AAm = rnd.nextInt(lsize(a));
                BBm = rnd.nextInt(lsize(b));
                for(int c = 0; c < lsize(a); c++) {
                    for(int d = 0; d < lsize(b); d++) {
                        tsa = lsize(Ai);
                        tsb = lsize(Bi);
                        AAi = (c+AAm)%tsa;
                        BBi = (d+BBm)%tsb;
                        if(Ai==Bi && AAi==BBi) continue;
                        if(taskList == null) {
                            if(taskA == null) taskA = SGUI3.getList().getLevel(Ai).get(AAi);
                            taskB = SGUI3.getList().getLevel(Bi).get(BBi);
                        }
                        else {
                            if(taskA == null) taskA = taskList.getLevel(Ai).get(AAi);
                            taskB = taskList.getLevel(Bi).get(BBi);
                        }
                        if(taskA.getNAME().startsWith("PERM: ") 
                                || taskB.getNAME().startsWith("PERM: ")) 
                            continue;
                        if(taskA.findAbove(taskB) != null
                                || taskB.findAbove(taskA) != null)
                            continue;
                        if(taskA.eq(taskB)) continue;
                        return true;
        }   }   }   }
        return false;
    }
    
    public static boolean init() {
        if(!init_core()) {
            UI.spawnErrorBox("this function aborted. Either your list is organized or there was an error.");
            return false;
        }
        return true;
    }
    @Override public void initialize(URL url, ResourceBundle rb) {
        slabel = taskA.getNAME() + "            " + taskB.getNAME();
        TaskLabel.setText(slabel); 
    }
    public static TaskList taskList;
    public static void recieveTaskListReference(TaskList tList) {
        taskList = tList;
    }
    
    
    @FXML public void handleLeftPress() {
        if(!taskA.addPost(taskB)) {
            UI.spawnErrorBox("Error adding rule here.");
        }
        if(taskList == null) {
            SGUI3.getList().reSort();
            TaskViewController.populate_list();
        } else taskList.reSort();
        if(!init()) UI.closeWindow(skipButton);
        else {
            slabel = taskA.getNAME() + "            " + taskB.getNAME();
            TaskLabel.setText(slabel); 
        }
    }
    
    
    @FXML public void handleRightPress() {
        if(!taskB.addPost(taskA)) {
            UI.spawnErrorBox("Error adding rule here.");
        }
        if(taskList == null) {
            SGUI3.getList().reSort();
            TaskViewController.populate_list();
        } else taskList.reSort();
        if(!init()) UI.closeWindow(skipButton);
        else {
            slabel = taskA.getNAME() + "            " + taskB.getNAME();
            TaskLabel.setText(slabel); 
        }
    }
    
    
    @FXML public void handleSkipPress() {
        if(!init()) UI.closeWindow(skipButton);
        else {
            slabel = taskA.getNAME() + "            " + taskB.getNAME();
            TaskLabel.setText(slabel); 
}   }   }
