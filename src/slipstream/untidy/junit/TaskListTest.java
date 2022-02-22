/*
 * This license header is intentionally left blank.
 *//*

package slipstream.untidy.junit;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import slipstream.untidy.taskdb.Task;
import slipstream.untidy.taskdb.TaskList;

import static org.junit.Assert.*;

*/
/**
 *
 * @author xvoidxxx
 *//*

public class TaskListTest {
    //TODO: update all of these to account for new rules re: uniqueID and addRule and addTask methods in TaskList
    TaskList testTaskList = new TaskList();
    Task taskA = new Task("First Task");
    Task taskB = new Task("Second Task");
    Task taskC = new Task("Third Task");
    
    TaskList newListTest = new TaskList();
    ArrayList<Task> tmpQueryResults;
    SecureRandom rng;
    int rngsize;

    */
/**
     * Generate task list of random size; no perms or posts.
     *//*

    @Before
    public void setUp() {
        try {
            rng = SecureRandom.getInstanceStrong();
            rngsize = rng.nextInt(100)+50;
            for(int i = 0; i < rngsize; i++) {
                newListTest.addTask(Long.toString(rng.nextLong()));
            }
        } catch (NoSuchAlgorithmException e) {
            fail("this environment does not have suitable RNG algorithms.");
        }
        testTaskList.addTask(taskA);
        testTaskList.addTask(taskB); //to go below First Task
    }
    
    @After
    public void tearDown() {
    }

    public String printArrayList(ArrayList<Task> list) {
        String newString = new String();
        if(!list.isEmpty()) {
            for(Task t : list) {
                newString += t.getNAME() + "\n";
            }
        }
        return newString;
    }
    
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void emptyWhenNoResults() {
        tmpQueryResults = newListTest.query("this shouldn't be here");
        assertFalse("the query results list should not be null.", tmpQueryResults == null);
        assertTrue("the results list should not contain anything, but alas:\n" + printArrayList(tmpQueryResults), tmpQueryResults.isEmpty());
    }
    
    @Test
    public void hasAMatch() {
        String shouldGetResults = newListTest.getTaskList().get(0).get(rng.nextInt(rngsize)).getNAME().substring(0, 5);
        tmpQueryResults = newListTest.query(shouldGetResults);
        assertTrue("the results should contain at least one match", newListTest.query(shouldGetResults).size() > 0);
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void addsTaskBelow() {
       testTaskList.addRule_core(taskA, taskB);
       Task firstTask = testTaskList.findTask(taskA);
       assertTrue("firstTask must not be null.", firstTask != null);
       Task secondTask = firstTask.findBelow(taskB);
       assertTrue("secondTask must not be null.", secondTask != null);
       assertTrue("the second task should have been found", firstTask.findBelow(taskB).equals(testTaskList.findTask(taskB)));
    }
    
    //A -> B
    //A -> C
    //B -> C? (should be A -> B -> C afterwards)
    @Test
    public void removesRedundantRuleBC() {
        testTaskList.addTask(taskC);
        testTaskList.addRule_core(taskA, taskC);
        testTaskList.addRule_core(taskB, taskC);
        assertFalse("A -> C shouldn't still be there.", testTaskList.findTask(taskA).getPosts().size() > 1);
    }
    
    //B -> C
    //A -> C
    //A -> B? (should be A -> B -> C afterwards)
    @Test
    public void removesRedundantRuleAB() {
        testTaskList = new TaskList();
        testTaskList.addTask(taskA);
        testTaskList.addTask(taskB);
        testTaskList.addTask(taskC);
        testTaskList.addRule_core(taskB, taskC);
        testTaskList.addRule_core(taskA, taskC);
        testTaskList.addRule_core(taskA, taskB);
        assertFalse("A still has C as post.", testTaskList.findTask(taskA).getPosts().contains(testTaskList.findTask(taskC)));
        assertFalse("A -> C shouldn't still be there.", testTaskList.findTask(taskA).getPosts().size() > 1);
        assertFalse("C <- A shouldn't still be there.", testTaskList.findTask(taskC).getPres().size() > 1);
    }
    
    @Test //needs to pass before savesAndLoadsConsistently is valid
    public void equalsEqualsEquals() {
        TaskList A = new TaskList();
        TaskList B = new TaskList();
        A.addTask("ASDF");
        B.addTask("ASDF");
        A.reSort();
        B.reSort();
        assertTrue("These are equal.", A.equals(B));
    }
    
    String filename = "test.tdl";
    
    @Test
    public void savesAndLoadsConsistently() {
        TaskList A = new TaskList(filename);
        TaskList B = new TaskList(A);
        A.saveFile();
        A = new TaskList(filename);
        assertTrue("A should be the same as what's saved and loaded.", A.equals(B));
    }
    
    @Test
    public void depthIsWorking() {
        TaskList A = new TaskList();
        Task aa = new Task("Task A");
        Task bb = new Task("Task B");
        A.addTask(aa);
        A.addTask(bb);
        A.addRule_core(aa, bb);
        A.reSort();
        assertFalse("dude nah", bb.getDepth() == 0);
        assertTrue("dude nah", aa.getDepth() == 0);
        assertFalse("dude nah", bb.getDepth() == 0);
        assertTrue("dude nah", aa.getDepth() == 0);
    }
    
    @Test
    public void deletesATask() {
        TaskList A = new TaskList();
        Task aa = new Task("Task A");
        Task bb = new Task("Task B");
        A.addTask(aa);
        A.addTask(bb);
        A.addRule_core(aa, bb);
        A.reSort();
        A.deleteTask(bb);
        assertTrue("task is still here", A.findTask(bb.getNAME()) == null);
    }
}
*/
