package bgu.spl.a2;

import sun.awt.windows.ThemeReader;

import java.util.Deque;
import java.util.Queue;
import java.util.Stack;

/**
 * this class represents a single work stealing processor, it is
 * {@link Runnable} so it is suitable to be executed by threads.
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class Processor implements Runnable {

    private final WorkStealingThreadPool pool;
    private final int id;

    /**
     * constructor for this class
     * <p>
     * IMPORTANT:
     * 1) this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     * <p>
     * 2) you may not add other constructors to this class
     * nor you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param id   - the processor id (every processor need to have its own unique
     *             id inside its thread pool)
     * @param pool - the thread pool which owns this processor
     */
    /*package*/ Processor(int id, WorkStealingThreadPool pool) {
        this.id = id;
        this.pool = pool;
    }

    @Override
    public void run() {
        // TODO: 19.12.2016 if not steal from all todo wait by monitor
        while (!Thread.currentThread().isInterrupted()) {
            if (pool.haveTasks(id)) {
                Task t = pool.getNextTask(id);
                // Check if stolen.
                if (t != null) {
                    t.handle(this);
                }
                //todo null pointer from ger next teask
            } else {
                boolean successSteal = pool.stealTasks(id);
                if (!successSteal) {
                    try {
                        pool.getVersionMonitor().await(pool.getVersionMonitor().getVersion());
                    } catch (InterruptedException ix) {
                        System.out.println("in InterruptedException");
                        Thread.currentThread().interrupt();
                        //continue loop.
                    }
                }
            }
        }

    }

    public void addTasks(Task<?>... task) {
        pool.addTasksToProcessor(id, task);
    }

    public void addOneTask(Task<?> task) {
        pool.addOneTaskToProcessor(id, task);
    }



}
