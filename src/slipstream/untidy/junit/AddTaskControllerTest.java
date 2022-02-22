package slipstream.untidy.junit;

import org.junit.Test;
import slipstream.untidy.taskdb.Task;
import slipstream.untidy.taskdb.TaskList;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class AddTaskControllerTest {
    TaskList testList;
    Task rootTask;
    ArrayList<Task> prequeryol = new ArrayList<>();
    ArrayList<Task> postqueryol = new ArrayList<>();
    ArrayList<Task> alreadyAdded = new ArrayList<>();
    private static SecureRandom rnd;

    @Test
    public void searchPreWorksConsistently() {
        final int numberOfPasses = 10;
        for(int i = 0; i < numberOfPasses; i++) {
            searchPreWorks();
            System.out.println("\n\n\n");
        }
    }

    public void searchPreWorks() {
        LinkedList<Task> addedpres = new LinkedList<>();
        try {
            rnd = SecureRandom.getInstanceStrong();
        }
        catch(NoSuchAlgorithmException e) {
            e.printStackTrace(System.err);
        }
        rootTask = new Task("root Task");
        testList = new TaskList();
        testList.addTask(rootTask);
        addedpres.add(rootTask);
        int breadth = 5;
        int depth = 5;
        LinkedList<LinkedList<Task>> newTasks = new LinkedList<>();
        for(int a = 0; a < breadth; a++) {
            newTasks.add(a, new LinkedList<>());
            newTasks.get(a).add(new Task(Integer.toString(rnd.nextInt())));
            testList.addTask(newTasks.get(a).get(0));
            addedpres.add(newTasks.get(a).get(0));
            testList.addRule_core(newTasks.get(a).get(0), rootTask);
            for(int b = 0; b < depth; b++) {
                newTasks.get(a).add(b+1, new Task(Integer.toString(rnd.nextInt())));
                testList.addTask(newTasks.get(a).get(b+1));
                testList.addRule_core(newTasks.get(a).get(b), newTasks.get(a).get(b+1));
            }
        }
        LinkedList<Integer> prelistsizes = new LinkedList<>();
        for(int c = 0; c < depth-1; c++) {
            final ArrayList<Task> prelist = handlepresearch();
            prelistsizes.add(c, prelist.size());
            for(int d = 0; d < prelistsizes.size()-1; d++) {
                assertFalse("pre list size shouldn't increase or stay the same amount",
                        prelistsizes.getLast() >= prelistsizes.get(d));
            }
            if(addedpres.stream().anyMatch(x -> prelist.contains(x))) {
                fail("an already-added pre has been found in the query list.");
            }
            //System.out.println("pre list size = " + prelist.size());
            //System.out.println("added pre size = " + addedpres.size());
            assertTrue(prelist.size() >= 0);
            Task foundTask = testList.findTask(prelist.get(rnd.nextInt(prelist.size())));
            rootTask.addPre(foundTask);
            addedpres.add(foundTask);
        }
    }

    @Test
    public void searchPostWorks() {
        return;
    }

    public ArrayList<Task> MLQ(String queryString) {
        return testList.query(queryString);
    }

    public ArrayList<Task> handlepresearch() {
        prequeryol = MLQ("");
        prequeryol.remove(rootTask.getNAME());
        boolean isPERM = rootTask.getNAME().startsWith("PERM: ");
        prequeryol.removeIf(x -> {
            if(rootTask.findAbove(x) != null || (!isPERM && x.getNAME().startsWith("PERM: "))) {
                return true;
            }
            return false;
        });
        prequeryol.removeIf(x -> {
            if(rootTask.findBelow(x) != null || (!isPERM && x.getNAME().startsWith("PERM: "))) {
                return true;
            }
            return false;
        });
        //System.out.println("prequeryol size = " + prequeryol.size());
        return prequeryol;
    }

    @Test
    public void noDuplicateTasks() {
        testList = new TaskList();
        testList.addTask("new Task");
        assertFalse("level 0 should have size greater than 0", testList.getLevel(0).size() == 0);
        assertTrue("The existing task is not detected", testList.taskExists("new Task"));
        assertTrue("The existing task is not detected", testList.taskExists(new Task("new Task")));
        assertFalse("A duplicate task has been allowed", testList.addTask("new Task"));
    }
}
