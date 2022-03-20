package io.github.ceragon.rwsemaphore;

public class RWSemWaiter {
    private volatile Thread task;
    private int flags;

    public Thread getTask() {
        return task;
    }

    public void setTask(Thread task) {
        this.task = task;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }
}
