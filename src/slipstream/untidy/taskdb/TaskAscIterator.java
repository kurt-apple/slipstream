/*
 * This license header is intentionally left blank.
 */

package slipstream.untidy.taskdb;

import java.util.Iterator;

public class TaskAscIterator implements Iterator<Task> {
    public TaskList AllTasks;
    private int x;
    private int y;
    private Task tref;
    
    public TaskAscIterator(TaskList t) {
        AllTasks = t;
        x = AllTasks.getTaskList().size();
        y = AllTasks.getLevel(x).size();
    }

    public static TaskAscIterator from(Task t) {
        TaskAscIterator tdi = new TaskAscIterator(t.getList());
        do {            
            if(tdi.getTask().eq(t.getNAME())) return tdi;
        } while (tdi.hasNext());
        return null;
    }
    
    public Task getTask() {
        return AllTasks.getLevel(x).get(y);
    }

    //TODO: review, clean, test
    @Override
    public boolean hasNext() {
        
        if(!getTask().eq(tref.getNAME())) {
            TaskAscIterator fffff = new TaskAscIterator(AllTasks);
            do {
                if(fffff.getTask().eq(tref.getNAME())) {
                    x = fffff.x;
                    y = fffff.y;
                    break;
                }
            }
            while(fffff.hasNext());
        }
        
        if(--y < 0) {
            if(--x < 0) return false;
            y = AllTasks.getLevel(x).size();
        }
        if(AllTasks.getLevel(x).get(y) != null) {
            tref = getTask();
            return true;
        }
        return false;
    }

    @Override
    public Task next() {
        if(hasNext()) return tref;
        else return null;
    }
}
