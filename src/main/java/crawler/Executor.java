package crawler;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

class Executor<T extends Task> {

    private ArrayList<T> taskArray;
    private ArrayList<T> taskExecute;
    private ArrayList<RunThread> threadArray;
    private final Integer threadNumber;

    Executor(Integer threadNumber) {

        if (threadNumber < 1) {
            threadNumber = 1;
        }

        this.threadNumber = threadNumber;
        taskArray = new ArrayList<T>();
        taskExecute = new  ArrayList<T>();

        threadArray = new ArrayList<RunThread>();
        for (int i = 0; i < threadNumber; ++i) {
            threadArray.add(new RunThread(this));
        }
    }

    void start() {
        for (RunThread runThread: threadArray) {
            runThread.start();
        }
    }

    synchronized void interrupt() {

        for (RunThread runThread: threadArray) {
            runThread.interrupt();
        }
    }

    synchronized void interruptSoft() {

        try {
            wait(1000);
            while (taskExecute.size() != 0) {
                wait(1000);
            }
            interrupt();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    synchronized boolean execute(T task) {

        if (taskArray.contains(task) || taskExecute.contains(task)) {
            return false;
        }

        taskArray.add(task);
        this.notifyAll();

        return true;
    }

    private synchronized Task getTask() {
        Task currentTask = null;

        if (!taskArray.isEmpty()) {
            currentTask = taskArray.get(0);
            taskArray.remove(0);
            taskExecute.add((T) currentTask);
        }

        return currentTask;
    }

    private synchronized void deleteExecutedTask(Task task) {
        taskExecute.remove(task);
    }

    class RunThread extends Thread {
        Executor<T> executor;

        public RunThread(Executor<T> executor) {
            this.executor = executor;
        }

        @Override
        public void run() {

            synchronized (executor) {

                while (true) {

                    try {
                        while (taskArray.size() == 0) {
                            executor.wait();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }

                    if (!Thread.interrupted()) {
                        Task currentTask = getTask();
                        if (currentTask != null) {
                            currentTask.doWork();
                            deleteExecutedTask(currentTask);
                        }
                    } else {
                        return;
                    }

                }

            }
        }
    }
}