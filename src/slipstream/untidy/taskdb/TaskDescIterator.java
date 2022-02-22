/*
 * This license header is intentionally left blank.
 */

package slipstream.untidy.taskdb;

import java.util.Iterator;

public class TaskDescIterator implements Iterator<Task> {
    private int x;
    private int y;
    private Task tref;
    
    TaskList AllTasks;
    
    public TaskDescIterator(TaskList t) {
        AllTasks = t;
        x = 0;
        y = 0;
        if(t.getTaskList().size() >= 1) {
            if(t.getLevel(0).size() >= 1) {
                tref = t.getLevel(0).get(0);
                return;
            }
        }
        tref = null;
    }
    
    public static TaskDescIterator from(Task t) {
        TaskDescIterator tdi = new TaskDescIterator(t.getList());
        do {            
            if(tdi.getTask().eq(t.getNAME())) return tdi;
        } while (tdi.hasNext());
        return null;
    }

    //TODO: review, clean, test
    @Override
    public boolean hasNext() {
        if(!getTask().eq(tref.getNAME())) {
            TaskDescIterator fffff = new TaskDescIterator(AllTasks);
            do {
                if(fffff.getTask().eq(tref.getNAME())) {
                    x = fffff.x;
                    y = fffff.y;
                    break;
                }
            }
            while(fffff.hasNext());
        }
        
        if(AllTasks.getLevel(x).size() <= ++y) {
            if(AllTasks.getTaskList().size() <= ++x) return false;
            y=0;
        }
        if(AllTasks.getLevel(x).get(y) != null) {
            tref = getTask();
            return true;
        }
        return false;
    }

    public Task getTask() {
        return AllTasks.getLevel(x).get(y);
    }

    @Override
    public Task next() {
        if(hasNext()) return tref;
        else return null;
    }
}
