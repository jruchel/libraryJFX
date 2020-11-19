package utils;

import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;

/**
 * Klasa pozwalajaca na wykonywanie asynchronicznych zadan na osobnych watkach
 */
public class TaskRunner implements Runnable {

    private List<Runnable> tasks;
    private Runnable onTaskFinished;
    private boolean inOrder;

    /**
     * @param tasks          zadania w postaci interfejsu {@link Runnable} do wykonania
     * @param onTaskFinished zadanie ktore zostanie wykonane jako powiadomienie o zakonczeniu
     * @param inOrder        zmienna odpowiadajaca za to, czy zadania maja zostac wykonane pokolei na jednym watku, czy w tym samym czasie na wielu
     */
    public TaskRunner(List<Runnable> tasks, Runnable onTaskFinished, boolean inOrder) {
        this.tasks = new ArrayList<>();
        this.tasks.addAll(tasks);
        this.onTaskFinished = onTaskFinished;
        this.inOrder = inOrder;
    }

    public TaskRunner(Runnable task) {
        this(task, null, false);
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

    /**
     * Wykonywanie zadan na jednym watku
     */
    private void executeInOrder() {
        new Thread(() -> {
            for (Runnable r : tasks) {
                r.run();
            }
            if (onTaskFinished != null)
                onTaskFinished.run();
        }).start();
    }

    /**
     * Wykonywanie zadan na wielu watkach jednoczesnie
     */
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

    /**
     * Mozliwe jest tez uzywanie {@link TaskRunner} jako {@link #onTaskFinished} jezeli chcemy wykonac czesc zadan po kolei, a pozniej czesc w tym samym czasie
     * <br>
     * Przyklad:
     * <code> <br><br>
     * Runnable ordered1, ordered2; <br>
     * Runnable unOrdered1, unOrdered2, onFinished; <br>
     * Task runner1 = new TaskRunner(Arrays.asList(unOrdered1, unOrdered2), onFinished); <br>
     * Task runner2 = new TaskRunner(Arrays.AsList(ordered1, ordered2), runner1, true); <br>
     * runner2.run(); <br><br>
     * </code>
     * W powyzszym przykladzie najpierw zostana wykonanie zadania ordered1 i ordered2 jedno po drugim, <br>
     * a dopiero po ich zakonczeniu wywolane zostana unOrdered1, unOrdered2 jednoczenise <br>
     * oraz onFinished po ich zakonczeniu
     */
    @Override
    public void run() {
        if (inOrder) {
            executeInOrder();
        } else {
            executeOutOfOrder();
        }
    }

    /**
     * Sprawdzanie czy wszystkie watki sa juz zakonczone
     *
     * @param threads watki do sprawdzenia
     * @return false jesli pozostal chociaz jeden watek w trakcie wykonywania, w przeciwnym wypadku true
     */
    private boolean checkAllThreadsDone(List<Thread> threads) {
        return threads.stream().noneMatch(Thread::isAlive);
    }
}
