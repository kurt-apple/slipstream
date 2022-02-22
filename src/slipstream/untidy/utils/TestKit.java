/*
package slipstream.untidy.utils;

import slipstream.untidy.taskdb.Task;
import slipstream.untidy.taskdb.TaskList;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class TestKit extends slipstream.tidy.utils.TestKit {
    */
/*
    generateTaskList(int startTasks)
    Randomly generates a tasklist using startTasks as size of the list
    WIP because cyclic tasks aren't implemented yet.
     *//*

    //TODO: review this method, then create tests
    public static TaskList generateTaskList(int startTasks) {
        TaskList gen = new TaskList();
        for(int i = 1; i <= startTasks; i++) {
            gen.addTask("Task " + i);
        }
        SecureRandom rng;
        try { rng = SecureRandom.getInstanceStrong(); }
        catch(NoSuchAlgorithmException e) { e.printStackTrace(System.err); return gen; }
        Task A, B;
        int levels, breadth;
        for(int i = 0; i < startTasks/2; i++) {
            levels = rng.nextInt(gen.getTaskList().size());
            breadth= rng.nextInt(gen.getTaskList().get(levels).size());
            A = gen.getTaskList().get(levels).get(breadth);
            levels = rng.nextInt(gen.getTaskList().size());
            breadth= rng.nextInt(gen.getTaskList().get(levels).size());
            B = gen.getTaskList().get(levels).get(breadth);
            while(!gen.addRule_core(A, B)) {
                A = new Task(Long.toString(rng.nextLong()));
                gen.addTask(A);
            }

            //perms less than 4%
            //made this number up
            if(rng.nextFloat() < 0.04) {
                levels = rng.nextInt(gen.getTaskList().size());
                breadth= rng.nextInt(gen.getTaskList().get(levels).size());
                A = gen.getTaskList().get(levels).get(breadth);
                levels = rng.nextInt(gen.getTaskList().size());
                breadth= rng.nextInt(gen.getTaskList().get(levels).size());
                B = gen.getTaskList().get(levels).get(breadth);
                while(!gen.addPerm_core(A.getNAME(), B.getNAME())) {
                    A = new Task(Long.toString(rng.nextLong()));
                    gen.addTask(A);
                }
            }
        }
        gen.reSort();
        return gen;
    }
}
*/
