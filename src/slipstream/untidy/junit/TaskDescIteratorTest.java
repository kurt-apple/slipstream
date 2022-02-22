package slipstream.untidy.junit;

import org.junit.Test;
import slipstream.untidy.taskdb.TaskDescIterator;
import slipstream.untidy.taskdb.TaskList;

import static org.junit.Assert.*;

public class TaskDescIteratorTest {
    @Test
    public void iterates() {
        TaskList t = new TaskList();
        for(int i = 0; i < 10; i++) {
            t.addTask("Task " + i);
        }
        TaskDescIterator tdi = t.getDescIterator();
        int i = 0;
        do {   
            assertTrue("these tasks should be equal; " + t.getLevel(0).get(i).getNAME() + ", " + tdi.getTask().getNAME(), t.getLevel(0).get(i).eq(tdi.getTask().getNAME()));
            i++;
        } while (tdi.hasNext());
    }
}
