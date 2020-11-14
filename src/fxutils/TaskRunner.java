package fxutils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskRunner implements Runnable {

    private List<Runnable> tasks;
    private Runnable onTaskFinished;
    private boolean inOrder;

    public TaskRunner(List<Runnable> tasks, Runnable onTaskFinished, boolean inOrder) {
        this.tasks = new ArrayList<>();
        this.tasks.addAll(tasks);
        this.onTaskFinished = onTaskFinished;
        this.inOrder = inOrder;
    }

    public TaskRunner(List<Runnable> tasks) {
        this(tasks, null, false);
    }

    public TaskRunner(List<Runnable> tasks, boolean inOrder) {
        this(tasks, null, inOrder);
    }

    public TaskRunner(List<Runnable> tasks, Runnable onTaskFinished) {
        this(tasks, onTaskFinished, false);
    }

    public TaskRunner(Runnable task, Runnable onTaskFinished) {
        this(task, onTaskFinished, false);
    }

    public TaskRunner(Runnable task, Runnable onTaskFinished, boolean inOrder) {
        tasks = new ArrayList<>();
        tasks.add(task);
        this.onTaskFinished = onTaskFinished;
        this.inOrder = inOrder;
    }

    private void executeInOrder() {
        new Thread(() -> {
            for (Runnable r : tasks) {
                r.run();
            }
            if (onTaskFinished != null)
                onTaskFinished.run();
        }).start();

    }

    private void executeOutOfOrder() {
        List<Thread> threads = tasks.stream().map(Thread::new).collect(Collectors.toList());
        new Thread(() -> {
            boolean done = false;
            for (Thread t : threads) {
                t.start();
            }
            while (!done) {
                done = checkAllThreadsDone(threads);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
            if (onTaskFinished != null)
                onTaskFinished.run();
        }).start();
    }

    @Override
    public void run() {
        if (inOrder) {
            executeInOrder();
        } else {
            executeOutOfOrder();
        }
    }

    private boolean checkAllThreadsDone(List<Thread> threads) {
        return threads.stream().noneMatch(Thread::isAlive);
    }
}
