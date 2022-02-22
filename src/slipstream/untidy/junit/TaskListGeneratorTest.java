package slipstream.untidy.junit;

import org.junit.Test;
import slipstream.untidy.taskdb.TaskList;
import slipstream.untidy.taskdb.TaskListGenerator;

import static org.junit.Assert.*;

public class TaskListGeneratorTest {
    @Test
    public void generatedTaskListContainsEnough() {
        int listSize = 1;
        TaskList toGenerate = TaskListGenerator.withTasks(listSize);
        assertTrue("Generated Task List too small. listSize = " + listSize + "; actual size = " + toGenerate.getTotalSize(), listSize >= toGenerate.getTotalSize());
        listSize *= 10;
        toGenerate = TaskListGenerator.withTasks(listSize);
        assertTrue("Generated Task List too small. listSize = " + listSize + "; actual size = " + toGenerate.getTotalSize(), listSize >= toGenerate.getTotalSize());
        listSize *= 10;
        toGenerate = TaskListGenerator.withTasks(listSize);
        assertTrue("Generated Task List too small. listSize = " + listSize + "; actual size = " + toGenerate.getTotalSize(), listSize >= toGenerate.getTotalSize());
        listSize *= 10;
        toGenerate = TaskListGenerator.withTasks(listSize);
        assertTrue("Generated Task List too small. listSize = " + listSize + "; actual size = " + toGenerate.getTotalSize(), listSize >= toGenerate.getTotalSize());
    }

    @Test
    public void generatedTaskListNotNull() {
        assertFalse("genTaskList returned null, ouch.", TaskListGenerator.withTasks(10) == null);
    }

    @Test(timeout = 100)
    public void generateTaskListFastEnough() {
        try {
            TaskList speedTest = TaskListGenerator.withTasks(20);
        }
        catch (Exception e) {
            System.err.println("Exception on generateTaskListFastEnough.");
            e.printStackTrace(System.err);
            fail("End Stack Trace for generateTaskListFastEnough.");
        }
    }

    @Test(timeout = 5000)
    public void generateTaskListFastEnough2() {
        try {
            TaskList speedTest = TaskListGenerator.withTasks(1000);
        }
        catch (Exception e) {
            System.err.println("Exception on generateTaskListFastEnough.");
            e.printStackTrace(System.err);
            fail("End Stack Trace for generateTaskListFastEnough.");
        }
    }

    final int tries = 4;
    @Test(timeout = tries*5000)
    public void generatesTaskListFastEnough3() {
        for(int i = 0; i < tries; i++) {
            generateTaskListFastEnough2();
        }
    }
}
