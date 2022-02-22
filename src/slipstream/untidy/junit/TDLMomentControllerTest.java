package slipstream.untidy.junit;

import org.junit.Before;
import org.junit.Test;
import slipstream.untidy.taskapp.TDLMomentController;
import slipstream.untidy.taskdb.Task;
import slipstream.untidy.taskdb.TaskList;
import slipstream.untidy.taskdb.TaskListGenerator;

import static org.junit.Assert.*;

public class TDLMomentControllerTest {

    TaskList testList;
    int rngsize;

    @Before
    public void setUp() {
        testList = TaskListGenerator.withTasks(20);
    }

    @Test
    public void initDoesNotSabotageList() {
        //task list generator tasks must pass before this is valid.
        TDLMomentController xController = new TDLMomentController();
        TDLMomentController.recieveTaskListReference(testList);
        Task A;
        Task B;
        while(xController.init_core()) {
            A = xController.taskA;
            B = xController.taskB;
            if(A.eq(B)) fail("Whoa there. A is equal to B. A: " + A.getNAME() + "; B: " + B.getNAME());
            assertTrue("Whoa there. B is above A.", xController.taskA.findAbove(xController.taskB.getNAME()) == null);
            assertTrue("Whoa there. B is below A.", xController.taskB.findAbove(xController.taskA.getNAME()) == null);
            assertTrue("Boggle should submit valid rules just fine.", A.addPost(B));
        }
    }
}