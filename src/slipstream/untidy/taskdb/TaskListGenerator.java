/*
 * This license header is intentionally left blank.
 */

package slipstream.untidy.taskdb;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/*TaskListGenerator
a class to generate a tasklist with a number of tasks,
including random rules and perms.
 */
public class TaskListGenerator {
    public static Task taskA, taskB;
    private static int Am, Bm, AAm, BBm, Ai, Bi, AAi, BBi, wsizei;
    private static int tsa, tsb;
    private static SecureRandom rnd;
    private static int wsize(TaskList taskList, int x) {
        int i;
        for(i = 0; i < taskList.getTaskList().size(); i++) {
            if(lsize(taskList, i) == 0) {
                break;
        }   }
        return Math.min(x, i);
    }
    public static int lsize(TaskList taskList, int level) { 
        return taskList.getLevel(level).size(); }

    public static boolean init_core(TaskList taskList) {
        wsizei = wsize(taskList, 4);
        if(wsizei == 0) { 
            System.err.println("list is empty.");
            return false;
        }
        if(wsizei <= 2 && lsize(taskList, 0) == 1) {
            System.err.println("list doesn't have enough in it to use this function.");
            return false;
        }
        Am = rnd.nextInt(wsizei);
        Bm = rnd.nextInt(wsizei);
        for(int a = 0; a < wsizei; a++) {
            for(int b = 0; b < wsizei; b++) {
                Ai = (a+Am)%wsizei;
                Bi = (b+Bm)%wsizei;
                AAm = rnd.nextInt(lsize(taskList, a));
                BBm = rnd.nextInt(lsize(taskList, b));
                for(int c = 0; c < lsize(taskList, a); c++) {
                    for(int d = 0; d < lsize(taskList, b); d++) {
                        tsa = lsize(taskList, Ai);
                        tsb = lsize(taskList, Bi);
                        AAi = (c+AAm)%tsa;
                        BBi = (d+BBm)%tsb;
                        if(Ai==Bi && AAi==BBi) continue;
                        taskA = taskList.getLevel(Ai).get(AAi);
                        taskB = taskList.getLevel(Bi).get(BBi);
                        if(taskA.findBelow(taskB) != null
                                || taskB.findBelow(taskA) != null)
                            continue;
                        return true;
        }   }   }   }
        return false;
    }
    public static TaskList withTasks(int Quantity) {
        TaskList a_generated_tasklist = new TaskList();
        for(int i = 1; i <= Quantity; i++) a_generated_tasklist.addTask("Task:" + i);
        try { 
            rnd = SecureRandom.getInstanceStrong();
        } 
        catch(NoSuchAlgorithmException e) { 
            e.printStackTrace(System.err); 
        }
        for(int i = 0; i < Quantity/2; i++) {
            if(init_core(a_generated_tasklist)) {
                taskA.addPost(taskB);
            }
            else break;
        }
        return a_generated_tasklist;
    }
}

