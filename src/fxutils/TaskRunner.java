package fxutils;

import java.util.ArrayList;
import java.util.List;

public class TaskRunner implements Runnable {

    private List<Thread> threads;
    private Runnable onTaskFinished;

    public TaskRunner(List<Runnable> tasks, Runnable onTaskFinished) {
        threads = new ArrayList<>();
        for (Runnable r : tasks) {
            threads.add(new Thread(r));
        }
        this.onTaskFinished = onTaskFinished;
    }

    public TaskRunner(Runnable task, Runnable onTaskFinished) {
        threads = new ArrayList<>();
        threads.add(new Thread(task));
        this.onTaskFinished = onTaskFinished;
    }

    @Override
    public void run() {
        new Thread(() -> {
            boolean done = false;
            for (Thread t : threads) {
                t.start();
            }

            while (!done) {
                done = checkAllThreadsDone();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }

            onTaskFinished.run();
        }).start();
    }

    private boolean checkAllThreadsDone() {
        return threads.stream().noneMatch(Thread::isAlive);
    }
}
