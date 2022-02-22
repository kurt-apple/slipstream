/*
 * This license header is intentionally left blank.
 */

package slipstream.archive;

import slipstream.untidy.taskdb.Task;

/**
 * <h1>Dependency</h1>
 * <p>thought about making dependencies their own type. Decided against it.</p>
 * <p>This approach may become useful, though, toward certain optimizations.</p>
 */
public class Dependency {
    private boolean isPerm;
    private Task DependencyTask;
    public Dependency(boolean above, boolean perm, Task t) {
        this.isPerm = perm;
        this.DependencyTask = t;
    }
    public Task getTask() {
        return this.DependencyTask;
    }
    /**
     * <h2>isPerm()</h2>
     * The main idea to the Dependency class is to handle perms vs regular rules/orderings.
     */
    public boolean isPerm() {
        return isPerm;
    }
}
