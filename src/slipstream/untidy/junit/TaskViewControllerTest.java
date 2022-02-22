/*
 * This license header is intentionally left blank.
 */
package slipstream.untidy.junit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import slipstream.tidy.utils.TestKit;
import slipstream.tidy.utils.UI;
import slipstream.untidy.taskdb.Task;
import slipstream.untidy.taskdb.TaskList;

/**
 *
 * @author xvoidxxx
 */
public class TaskViewControllerTest {
    
    public TaskViewControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    TaskList testList = new TaskList();
    String taskName = "new task";
    String taskName2 = "other task";
    
    @Before
    public void setUp() {
        testList.addTask(taskName);
        testList.addTask(taskName2);
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void deletesTask() {
        Task deletedTask = testList.findTask(taskName);
        assertFalse("deletedTask should not be null.", deletedTask == null);
        if(!TestKit.dummyChoiceBox("press yes to delete.", true)) {
            fail("This choicebox should be yes, not no");
        }
        testList.deleteTask(deletedTask);
        assertTrue("Task should be deleted.", testList.findTask(taskName) == null);
    }
    
    @Test
    public void declinesDeleteTask() {
        Task notDeletedTask = testList.findTask(taskName2);
        assertFalse("notDeletedTask should not be null. (1)", notDeletedTask == null);
        if(TestKit.dummyChoiceBox("Press no to decline delete.", false)) {
            fail("This choicebox should be no, not yes");
        }
        assertFalse("notDeletedTask should not be null. (2)", notDeletedTask == null);
        assertTrue("Task should not be deleted.", testList.findTask(taskName2).equals(notDeletedTask));
    }
}
