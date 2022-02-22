package slipstream.untidy.junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import slipstream.untidy.taskdb.Task;
import slipstream.untidy.taskdb.TaskList;

import static org.junit.Assert.*;

public class TaskTest {

    TaskList list = new TaskList();
    Task A;
    Task B;
    Task C;

    public void resetTasks() {
        A = new Task("Task A");
        B = new Task("Task B");
        C = new Task("Task C");
        list.getTaskList().forEach(x -> x.clear());
        list.addTask(A);
        list.addTask(B);
        list.addTask(C);
    }

    @Before
    public void setUp() {
        resetTasks();
    }

    @After
    public void tearDown() {
    }

    /**
     * This mirrors the ProgressTest.java method <br>
     * <ul>
     *     <li>TODO: check if this method is necessary with ProjectTest where it is.</li>
     *     <li>TODO: tighten timings to minimums.</li>
     * </ul>
     */
    @Test(timeout = 1000)
    public void areNoTODOs() {
        int i = 0;
        if(i != 0) fail("not all TODOs are completed for TaskTest.");
    }

    /**
     * expected configuration after A.addPost(B): <br>
     * A -> B
     */
    @Test(timeout = 1000)
    public void addsRuleBelow() {
        A.addPost(B);
        assertTrue("the second task should be below the first.", A.getPosts().contains(B));
    }

    /**
     * expected configuration after B.addPre(A): <br>
     * A -> B
     */
    @Test(timeout = 1000)
    public void addsRuleAbove() {
        resetTasks();
        B.addPre(A);
        assertTrue("the second task should be below the first.", B.getPres().contains(A));
    }

    /**
     * Logical steps are as follows:
     * <ol>
     *     <li>A -> B</li>
     *     <li>A -> C</li>
     *     <li>B -> C</li>
     *     <li>Full structure: A -> B -> C</li>
     *     <li>A -> C no longer exists because it is redundant.</li>
     * </ol>
     * TODO: Consider reducing strictness of redundancy rules (it may be better to keep redundant rules)
     */
    @Test(timeout = 1000)
    public void removesRedundantRuleBelow_BC() {
        resetTasks();
        assertTrue("rule should be fine", A.addPost(B));
        assertTrue("rule should be fine", A.addPost(C));
        assertTrue("B should have A as its pre", B.getPres().contains(A));
        assertTrue("C should have A as its pre", C.getPres().contains(A));
        assertTrue("A should have B as post", A.getPosts().contains(B));
        assertTrue("A should have C as post", A.getPosts().contains(C));
        assertTrue("rule should be fine", B.addPost(C));
        assertTrue("C should have B as its pre", C.getPres().contains(B));
        assertFalse("C should not have A as pre.", C.getPres().contains(A));
        assertTrue("A should have posts.", A.getPosts().size() > 0);
        assertTrue("A should have B as its post.", A.getPosts().contains(B));
        assertFalse("A -> C shouldn't still be there.", A.getPosts().contains(C));
    }

    /**
     * B <- A <br>
     * C <- A <br>
     * C <- B ? should be C <- B <- A
     */
    @Test(timeout = 1000)
    public void removesRedundantRuleAbove_BC() {
        resetTasks();
        B.addPre(A);
        C.addPre(A);
        C.addPre(B);
        assertFalse("C <- A shouldn't still be there.", C.getPres().contains(A));
    }

    /**
     * Logic:
     * <ol>
     *     <li>B -> C</li>
     *     <li>A -> C</li>
     *     <li>A -> B</li>
     *     <li>Full Structure: A -> B -> C</li>
     *     <li>A -> C shouldn't be there any more</li>
     * </ol>
     */
    @Test(timeout = 1000)
    public void removesRedundantRuleBelow_AB() {
        resetTasks();
        assertTrue("rule should be fine", B.addPost(C));
        assertTrue("rule should be fine", A.addPost(C));
        assertTrue("rule should be fine", A.addPost(B));
        assertFalse("A -> C shouldn't still be there.", A.getPosts().contains(C));
        assertFalse("C <- A shouldn't still be there.", C.getPres().contains(A));
    }

    /**
     * Logic:
     * <ol>
     *     <li>B -> C</li>
     *     <li>A -> C</li>
     *     <li>A -> B</li>
     *     <li>Full structure: A -> B -> C</li>
     *     <li>A -> C shouldn't be there any more.</li>
     * </ol>
     */
    @Test(timeout = 1000)
    public void removesRedundantRuleAbove_AB() {
        resetTasks();
        C.addPre(B);
        C.addPre(A);
        B.addPre(A);
        assertFalse("C <- A shouldn't still be there.", C.getPres().contains(A));
        assertFalse("A -> C shouldn't still be there.", A.getPosts().contains(C));
    }

    /**
     * Logic:
     * <ol>
     *     <li>A -> B</li>
     *     <li>A -> B a second time should fail because it is irrelevant</li>
     * </ol>
     */
    @Test(timeout = 1000)
    public void doesNotAddDuplicateRuleBelow() {
        resetTasks();
        assertTrue("A -> B should succeed because no rules are in the DAG yet.", A.addPost(B));
        assertFalse("A -> B should fail because it's already there.", A.addPost(B));
    }

    /**
     * Logic:
     * <ol>
     *     <li>A -> B</li>
     *     <li>A -> B a second time should fail because it is irrelevant</li>
     * </ol>
     */
    @Test(timeout = 1000)
    public void doesNotAddDuplicateRuleAbove() {
        resetTasks();
        assertTrue("B -> A should succeed because no rules are set yet.", A.addPre(B));
        assertFalse("B -> A should fail because it's already set.", A.addPre(B));
    }

    /**
     * Logic:
     * <ol>
     *     <li>A -> B -> C</li>
     *     <li>B -> C should fail because it already exists.</li>
     * </ol>
     */
    @Test(timeout = 1000)
    public void doesNotAddRedundantRuleBelow() {
        resetTasks();
        A.addPost(B);
        B.addPost(C);
        assertFalse("A -> C should fail because A -> B -> C already.", A.addPost(C));
    }

    /**
     * Logic:
     * <ol>
     *     <li>A -> B -> C</li>
     *     <li>B -> C should fail because it already exists.</li>
     * </ol>
     */
    @Test(timeout = 1000)
    public void doesNotAddRedundantRuleAbove() {
        resetTasks();
        A.addPre(B);
        assertTrue("A should have B as post.", A.getPres().contains(B));
        B.addPre(C); //C -> B -> A
        assertTrue("B should have C as post.", B.getPres().contains(C));
        assertFalse("A <- C should fail because C -> B -> A already.", A.addPre(C));
    }

    /**
     * Tests base functionality of getPriority, which should return a recursive sum of all nodes traversed when moving deeper down from a task. <br>
     * A -> B -> C <br>
     * A would have priority of 2 while B would have a priority of 1. Higher priority score means higher priority. <br>
     * Note that tasks may be traversed over more than once, and this is by design. <br>
     * A -> B -> C -> E <br>
     *      B -> D -> E <br>
     * A would have priority of 5 because E is traversed over twice in the DAG for this algorithm. <br>
     * "E depends on multiple prerequisites, and both of those prerequisites depend on B." <br>
     */
    @Test(timeout = 1000)
    public void getsPriority() {
        //A -> B
        //A -> C
        resetTasks();
        A.addPost(B);
        A.addPost(C);
        assertEquals(2, A.getPriority().intValue());
        resetTasks();
        //A -> B
        //B -> C
        A.addPost(B);
        B.addPost(C);
        assertEquals(2, A.getPriority().intValue());
        resetTasks();
        //A -> C
        //B -> C
        //A -> B
        A.addPost(C);
        B.addPost(C);
        A.addPost(B);
        assertEquals(2, A.getPriority().intValue());
        resetTasks();
        //A
        assertEquals(0, A.getPriority().intValue());
    }

    /**
     * Logic:
     * <ol>
     *     <li>A -> B</li>
     *     <li>A -> ([B] -> C -> B)</li>
     *     <li>A -> ([B] -> C -> D -> B)</li>
     *     <li>A -> (B -> [C] -> D -> B)</li>
     * </ol>
     * A should have a priority of 1 because C is flagged, meaning it's next to do in the list, i.e. B is done, C is next in the perms - meaning it will not be counted as a post for A.
     */
    @Test(timeout = 1000)
    public void getsPriorityWhenPermsInTheMix() {
        //A -> B
        //B +> C, C +> B, [B]
        resetTasks();
        A.addPost(B);
        B.setPermPost(C);
        assertEquals(2, A.getPriority().intValue());
        assertEquals(1, B.getPriority().intValue());
        assertTrue("B should have C as perm post", B.getPermPost().eq(C));
        assertTrue("B perm pre should not be null", B.getPermPre() != null);
        assertTrue("B should have C as perm pre", B.getPermPre().eq(C));
        //C +> D, D +> B, [B]
        Task D = new Task("Task D");
        list.addTask(D);
        C.setPermPost(D);
        assertTrue("B should be flagged", B.isFlaggedFromPerms());
        assertTrue("B should have D as perm pre", B.getPermPre().eq(D));
        assertTrue("B should have C as perm post", B.getPermPost().eq(C));
        assertTrue("C should have D as perm post", C.getPermPost().eq(D));
        assertTrue("D should not be flagged", !D.isFlaggedFromPerms());
        assertTrue("C should not be flagged", !C.isFlaggedFromPerms());
        assertTrue("C should have priority of 1 but instead has priority of " + C.getPriority(), C.getPriority().intValue() == 1);
        assertTrue("B should be flagged from Above.", B.isFlaggedFromAbove());
        assertTrue("B should have a priority of 2, instead priority is " + B.getPriority().intValue(), 2 == B.getPriority().intValue());
        assertEquals(3, A.getPriority().intValue());
        C.flagPerm();
        assertEquals(1, A.getPriority().intValue());
    }

    /**
     * Various tests of the depth method
     */
    @Test(timeout = 1000)
    public void getsDepth() {
        //A -> B
        //A -> C
        resetTasks();
        A.addPost(B);
        A.addPost(C);
        assertEquals(1, C.getDepth().intValue());
        resetTasks();
        //A -> B
        //B -> C
        A.addPost(B);
        B.addPost(C);
        assertEquals(2, C.getDepth().intValue());
        resetTasks();
        //A -> C
        //B -> C
        //A -> B
        A.addPost(C);
        B.addPost(C);
        A.addPost(B);
        assertEquals(2, C.getDepth().intValue());
        resetTasks();
        //A
        assertEquals(0, A.getDepth().intValue());
    }

    /**
     * Logic:
     * <ol>
     *     <li>A -> B</li>
     *     <li>A -> (B -> C -> B)</li>
     *     <li>C should have depth 2, B should have depth 1.</li>
     *     <li>A -> (B -> C -> D -> B)</li>
     *     <li>D should have depth 3.</li>
     * </ol>
     */
    @Test(timeout = 5000)
    public void getsDepthWhenPermsInTheMix() {
        //A -> B
        //B +> C, C +> B, [B]
        //A Priority should be 2
        //B Priority should be 1
        resetTasks();
        assertTrue("should be able to add a post", A.addPost(B));
        assertTrue("B should have A as pre", B.getPre(0).eq(A));
        assertTrue("A should have B as post", A.getPost(0).eq(B));
        B.setPermPost(C);
        assertEquals(2, C.getDepth().intValue());
        assertEquals(1, B.getDepth().intValue());
        Task D = new Task("Task D");
        list.addTask(D);
        C.setPermPost(D);
        assertTrue("D perm post must not be null.", D.getPermPost() != null);
        assertTrue("D needs to have perm post of B", D.getPermPost().eq(B));
        assertEquals(3, D.getDepth().intValue());
        assertEquals(1, B.getDepth().intValue());
        B.flagPerm();
        assertEquals(1, B.getDepth().intValue());
    }

    /**
     * Find a perm post directly below a task.
     */
    @Test(timeout = 1000)
    public void findsPermBelow() {
        resetTasks();
        assertTrue("A.setPermPost(B) didn't work", A.setPermPost(B));
        assertTrue("B perm post should not be null", B.getPermPost() != null);
        assertTrue("A perm post should not be null", A.getPermPost() != null);
        assertTrue("A should be flagged.", A.isFlaggedFromPerms());
        Task searchResult = A.findPermPost(B.getNAME());
        if(searchResult == null) fail("search result was null.");
        if(!searchResult.eq(B)) fail("search result isn't B as expected.");
        searchResult = B.findPermPost(A.getNAME());
        if(searchResult != null) fail("search result should be null for bottom perm");
    }

    /**
     * Find a perm pre directly above a task.
     */
    @Test(timeout = 1000)
    public void findsPermAbove() {
        resetTasks();
        A.setPermPre(B);
        Task searchResult = A.findPermPre(B.getNAME());
        if(searchResult == null) fail("search result was null.");
        if(!searchResult.eq(B)) fail("search result isn't B as expected.");
        searchResult = B.findPermPre(A.getNAME());
        if(searchResult != null) { fail("search result should be null for top perm"); }
    }

    /**
     * Finds perm pres above a task
     */
    @Test(timeout = 1000)
    public void findsPerm2Above() {
        resetTasks();
        A.setPermPre(B);
        B.setPermPre(C);
        //System.out.println(A.getNAME() + " <- " + A.getPermPre().getNAME() + " <- " + A.getPermPre().getPermPre().getNAME() + " <- " + A.getPermPre().getPermPre().getPermPre().getNAME());
        C.flagPerm();
        Task searchResult = A.findAbove(C.getNAME());
        if(searchResult == null) fail("search result was null.");
        if(!searchResult.eq(C)) fail("search result isn't C as expected.");
        searchResult = C.findPermPre(A.getNAME());
        if(searchResult != null) { fail("search result should be null for top perm"); }
    }

    /**
     * <ol>
     *     <li>A -> B -> A</li>
     *     <li>A -> B -> C -> A</li>
     *     <li>[A] -> B -> C -> [A]</li>
     *     <li>A should find C in its nested posts</li>
     *     <li>A is flagged. C should not find A below it because it's at the top of the perm loop.</li>
     * </ol>
     */
    @Test(timeout = 1000)
    public void findsPerm2Below() {
        resetTasks();
        A.setPermPost(B);
        B.setPermPost(C);
        //System.out.println(A.getNAME() + " -> " + A.getPermPost().getNAME() + " -> " + A.getPermPost().getPermPost().getNAME() + " -> " + A.getPermPost().getPermPost().getPermPost().getNAME());
        A.flagPerm();
        System.out.println("The flagged perm is: " + A.getFlaggedInLoop().getNAME());
        Task searchResult = A.findBelow(C.getNAME());
        if(searchResult == null) fail("search result was null.");
        if(!searchResult.eq(C)) fail("search result isn't C as expected.");
        searchResult = C.findBelow(A.getNAME());
        if(searchResult != null) { fail("search result should be null for bottom perm"); }
    }

    /**
     * Find a task in the pres list
     * TODO: also test not finding a task.
     */
    @Test(timeout = 1000)
    public void findsAbove() {
        resetTasks();
        A.addPre(B);
        B.addPre(C);
        if(A.findAbove(C.getNAME()) == null) fail("Task A should have found C.");
    }

    /**
     * Find a task in the posts list
     */
    @Test(timeout = 1000)
    public void findsBelow() {
        resetTasks();
        A.addPost(B);
        B.addPost(C);
        if(A.findBelow(C) == null) fail("Task A should have found C.");
    }

    /**
     * Push a task to the pres list
     */
    @Test(timeout = 1000)
    public void pushesAbove() {
        resetTasks();
        A.pushPre(B);
        assertTrue("B should be above A.", A.getPres().contains(B));
    }

    /**
     * Push a task to the posts list
     */
    @Test(timeout = 1000)
    public void pushesBelow() {
        resetTasks();
        A.pushPost(B);
        assertTrue("B should be below A.", A.getPosts().contains(B));
    }

    /**
     * Logic:
     * <ol>
     *     <li>A -> B -> A</li>
     *     <li>A -> B -> C -> A</li>
     *     <li>A -> B -> D -> C -> A</li>
     * </ol>
     */
    @Test(timeout = 1000)
    public void addsBelowPermLinkJustFine() {
        resetTasks();
        A.setPermPost(B);
        B.setPermPost(C);
        assertTrue("C perm post should not be null.", C.getPermPost() != null);
        assertTrue("C should have A as a perm post.", C.getPermPost().eq(A));
        assertTrue("A should be flagged.", A.isFlaggedFromPerms());
        assertTrue("C should not be flagged.", !C.isFlaggedFromPerms());
        assertTrue("B should not be flagged.", !B.isFlaggedFromPerms());
        assertTrue("C should not have A as a perm pre.", !C.getPermPre().eq(A));
        Task D = new Task("Task D");
        //B +> D; D +> C
        B.setPermPost(D);
        assertTrue("C should not have B as a perm pre.", !C.getPermPre().eq(B));
        assertTrue("C should have D as a perm pre.", C.getPermPre().eq(D));
    }

    /**
     * Logic:
     * <ol>
     *     <li>B -> A -> B</li>
     *     <li>B should be flagged, since it was added as a perm pre to start a loop.</li>
     *     <li>C -> B -> A -> C</li>
     *     <li>B will still be flagged, because C was added to an existing loop, ie one already in order.</li>
     * </ol>
     */
    @Test(timeout = 1000)
    public void addsAbovePermLinkJustFine() {
        resetTasks();
        assertTrue("A should be able to add B as perm pre", A.setPermPre(B));
        assertTrue("Task A perm pre should not be null", A.getPermPre() != null);
        assertTrue("Task A should have B on perm pre", A.getPermPre().eq(B));
        assertTrue("Task A perm Post should be B, not null", A.getPermPost() != null);
        assertTrue("Task A should have B on perm post", A.getPermPost().eq(B));
        assertTrue("Task B should have A as perm pre, not null", B.getPermPre() != null);
        assertTrue("Task B should have A on perm pre", B.getPermPre().eq(A));
        assertTrue("Task B should have A on perm post", B.getPermPost().eq(A));
        System.out.println("getting test results.");
        assertTrue("Task B should be flagged.", A.getFlaggedInLoop().eq(B));
        assertTrue("Task B should be flagged.", B.getFlaggedInLoop().eq(B));
        System.out.println("finished tests.");
        assertTrue("Task C shouldn't have perms or posts.", C.getPermPre() == null && C.getPermPost() == null);
        assertTrue("Task C shouldn't be flagged.", !C.isFlaggedFromPerms());
        assertTrue("B should be able to add C as perm pre", B.setPermPre(C));
        assertTrue("B doesn't have C as perm pre. Actual value: " + B.getPermPre().getNAME(), B.getPermPre().eq(C));
        System.out.println("getting test results.");
        assertTrue("Task A should be able to see what is flagged.", A.getFlaggedInLoop() != null);
        assertTrue("Task B should be flagged. instead: " + A.getFlaggedInLoop().getNAME(), A.getFlaggedInLoop().eq(B));
        assertTrue("Task B should be flagged. instead: " + B.getFlaggedInLoop().getNAME(), B.getFlaggedInLoop().eq(B));
        System.out.println("finished tests.");
        assertTrue("C perm pre should not be null", C.getPermPre() != null);
        assertTrue("B doesn't have the right perm dependencies.", C.getPermPost().eq(B));
        assertTrue("A should not be flagged.", !A.isFlaggedFromPerms());
        assertTrue("B should be flagged.", B.isFlaggedFromPerms());
        assertTrue("C should not be flagged.", !C.isFlaggedFromPerms());
        assertTrue("C should not have A as a perm post.", !C.getPermPost().eq(A));
        assertTrue("A doesn't have the right perm dependencies. instead it's " + A.getPermPost().getNAME(), A.getPermPost().eq(C));
        assertTrue("B doesn't have A as perm post. Actual value: " + B.getPermPost().getNAME(), B.getPermPost().eq(A));
        assertTrue("Task B should be flagged. instead: " + C.getFlaggedInLoop().getNAME(), C.getFlaggedInLoop().eq(B));
    }

    /**
     * This is explained in detail in the next test, which tests PermPre methods instead of PermPost, but similar logic.
     */
    @Test(timeout = 1000)
    public void removesPermBelowTasksJustFine() {
        resetTasks();
        assertTrue("A should be able to set B as perm post", A.setPermPost(B));
        assertTrue("B should be able to set C as perm post", B.setPermPost(C));
        A.removePermPost();
        assertTrue("C perm post should not be null", C.getPermPost() != null);
        assertTrue("C should have A as a perm post.", C.getPermPost().eq(A));
        assertTrue("C should have A as a perm pre.", C.getPermPre().eq(A));
        assertTrue("A perm post should not be null", A.getPermPost() != null);
        assertTrue("A perm pre should not be null", A.getPermPre() != null);
        assertTrue("A should have C as a perm post and pre.", A.getPermPost().eq(C) && A.getPermPre().eq(C));
        assertTrue("B should not have A as a perm pre.", B.getPermPre() == null);
        assertTrue("C should not have B as a perm post.", !C.getPermPost().eq(B));
        assertTrue("neither B nor C should be flagged.", A.isFlaggedFromPerms() && !B.isFlaggedFromPerms() && !C.isFlaggedFromPerms());
    }

    /**
     * When a perm pre or perm post is removed, the perm loop must be reformed.
     * <ol>
     *     <li>B -> A -> B</li>
     *     <li>C -> B -> A -> C</li>
     *     <li>A's perm pre, B, is removed from the loop</li>
     *     <li>C -> A -> C</li>
     * </ol>
     * It follows that B should no longer have perm pres or posts, A and C should point to each other.
     */
    @Test(timeout = 1000)
    public void removesPermAboveTasksJustFine() {
        resetTasks();
        A.setPermPre(B);
        B.setPermPre(C);
        A.removePermPre();
        assertTrue("C perm pre should not be null.", C.getPermPre() != null);
        assertTrue("C should have B as a perm post.", C.getPermPre().eq(A));
        assertTrue("C should have B as a perm pre.", C.getPermPost().eq(A));
        assertTrue("B should not have A as a perm pre.", B.getPermPre() == null);
        assertTrue("C should have A as a perm post.", C.getPermPost().eq(A));
        assertTrue("C should be flagged.", C.isFlaggedFromPerms());
    }

    /**
     * Of course, in order for the list to render on screen, there must be consistent rules about where exactly to render each task, as well as appropriate handling of perm loops - else the client will render infinitely.
     * Logic:
     * <ol>
     *     <li>D >> C</li>
     *     <li>result: D -> C -> D</li>
     *     <li>C >> A</li>
     *     <li>result: D -> C -> A -> D</li>
     *     <li>A -> B</li>
     *     <li>E -> D</li>
     *     <li>F -> E</li>
     *     <li>B -> G</li>
     * </ol>
     * The resulting data structure: <br>
     *     F -> E -> D -> C -> A -> D -> C <br>
     *     and A -> B -> G <br>
     * <ul>
     *     <li>F is at level 0.</li>
     *     <li>E is at level 1.</li>
     *     <li>D is at level 2.</li>
     *     <li>C is at level 3.</li>
     *     <li>A is at level 4.</li>
     *     <li>B is at level 5.</li>
     *     <li>G is at level 6.</li>
     * </ul>
     */
    @Test(timeout = 1000)
    public void realWorldTest001() {
        resetTasks();
        //A is fold laundry
        //B is keep laundry caught up
        //C is cycle laundry
        //D is start laundry
        //E is goes to bed
        //F is concurrent modification exception
        //G is stay over
        Task D = new Task("start laundry");
        Task E = new Task("goes to bed");
        Task F = new Task("concurrent");
        Task G = new Task("stay over");
        list.addTask(D);
        list.addTask(E);
        list.addTask(F);
        list.addTask(G);
        D.setPermPost(C);
        C.setPermPost(A);
        A.addPost(B);
        E.addPost(D);
        F.addPost(E);
        B.addPost(G);
        D.flagPerm();
        assertTrue("A depth should be ", A.getDepth() == 4);
        assertTrue("B depth should be ", B.getDepth() == 5);
        assertTrue("C depth should be ", C.getDepth() == 3);
        assertTrue("D depth should be ", D.getDepth() == 2);
        assertTrue("E depth should be ", E.getDepth() == 1);
        assertTrue("F depth should be ", F.getDepth() == 0);
        assertTrue("G depth should be ", G.getDepth() == 6);
        list.reSort();
        assertTrue("top layer should not have A", !list.getLevel(0).contains(A));
    }

    /**
     * After implementing linkedhashset for findabove and findbelow, perms are not handled smoothly in rule creation.
     */
    @Test(timeout=200)
    public void perms_as_pres_taken_care_of() {
        TaskList list = new TaskList();
        Task D = new Task("Perm 1");
        Task E = new Task("Perm 2");
        Task F = new Task("Task 3");
        Task G = new Task("Task 4");
        list.addTask(D);
        list.addTask(E);
        list.addTask(F);
        list.addTask(G);
        D.setPermPre(E);
        assertTrue(E.isFlaggedFromPerms());
        F.addPre(D);
        G.addPre(E);
        F.addPre(G);    //  [E] ->  G   ->  F
                        //  [E] ->  D   ->  F
        assertFalse(F.addPre(E));
        assertFalse(F.getPres().contains(E));
        assertTrue(F.getAllAbove().contains(E));
        assertTrue(F.getAllAbove().contains(G));
        assertTrue(F.getAllAbove().contains(D));
        G.removePre(E);     //          G   ->  F
                            //  [E] ->  D   ->  F
        assertTrue(E.isFlaggedFromPerms());
        assertTrue(F.getAllAbove().contains(E));
        assertTrue(F.getAllAbove().contains(G));
        assertTrue(F.getAllAbove().contains(D));
    }

    /**
     * real world example of perms breaking things.
     */
    @Test(timeout=1000)
    public void real_world_perms_test() {
        TaskList tl = new TaskList();
        Task AA = new Task("1");
        Task BB = new Task("2");
        Task CC = new Task("3");
        Task DD = new Task("4");
        Task EE = new Task("5");
        Task FF = new Task("6");
        Task GG = new Task("7");
        Task HH = new Task("8");
        Task II = new Task("9");
        Task JJ = new Task("10");
        Task KK = new Task("11");
        Task LL = new Task("12");
        Task MM = new Task("13");
        tl.addTask(AA);
        tl.addTask(BB);
        tl.addTask(CC);
        tl.addTask(DD);
        tl.addTask(EE);
        tl.addTask(FF);
        tl.addTask(GG);
        tl.addTask(HH);
        tl.addTask(II);
        tl.addTask(JJ);
        tl.addTask(KK);
        AA.addPost(BB);     //aa: eliminate string
        BB.addPost(CC);     //bb: monday
        CC.addPost(DD);     //cc: 30 min scan
        DD.addPost(EE);     //dd: 35 min scan
        EE.addPost(FF);     //ee: rust 90 min
        FF.addPost(GG);     //ff: implement dag in rust
                            //gg: acquire
        HH.setPermPre(BB);  //HH: tuesday
        II.setPermPre(HH);  //II: wednesday
        JJ.setPermPre(II);  //JJ: thursday
        KK.setPermPre(JJ);  //KK: friday
        LL.setPermPre(KK);  //LL: saturday
        MM.setPermPre(LL);  //MM: sunday
        FF.addPre(HH);
        assertTrue(KK.getFlaggedInLoop().eq(BB.getNAME()));
        assertTrue(BB.isFlaggedFromPerms());
        assertTrue(FF.findAbove(II) == null && FF.findBelow(II) == null);
        assertTrue(FF.addPre(II)); //II: wednesday
        assertFalse(FF.getPres().contains(HH));
    }
}