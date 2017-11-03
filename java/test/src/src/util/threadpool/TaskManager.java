package util.threadpool;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class TaskManager
{
    private Thread[] workers = null;
    private LinkedList<Job> jobs = null;
    private TaskManager() {}

    public static TaskManager create(int length)
    {
        int i;
        final TaskManager taskPool = new TaskManager();

        length = constrainLength(length);
        System.out.println("worker count = " + length);
        taskPool.jobs = new LinkedList<Job>();
        taskPool.workers = new Thread[length];
        for (i = length - 1; i >= 0; --i) {
            taskPool.workers[i] = new Thread(new Worker(taskPool.jobs));
            taskPool.workers[i].start();
        }

        return taskPool;
    }

    private static int constrainLength(int length)
    {
        if (0 >= length || 20 < length) {
            length = 4;
        }

        return length;
    }

    public void performTask(ITask task)
    {
        Job job = new Job(task, false);

        synchronized (jobs) {
            jobs.addLast(job);
            jobs.notifyAll();
        }
    }

    public void destroy()
    {
        int i;
        Job job;

        synchronized (jobs) {
            while (0 < jobs.size()) {
                try {
                    jobs.wait(500);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (i = workers.length - 1; i >= 0; --i) {
                job = new Job(null, true);
                jobs.addLast(job);
                jobs.notifyAll();
            }
        }
    }
}

class Worker implements Runnable
{
    private LinkedList<Job> _jobs = null;

    public Worker(LinkedList<Job> jobs)
    {
        _jobs = jobs;
    }

    public void run()
    {
        Job job = null;
        Thread curThread = Thread.currentThread();

        do {
            try {
                if (curThread.isInterrupted()) {
                    break;
                }

                synchronized (_jobs) {
                    while (0 == _jobs.size()) {
                        _jobs.wait();
                    }
                    try {
                        job = _jobs.removeFirst();
                    }
                    catch (NoSuchElementException nse) {
                        System.out.println("internal error");
                    }

                }
            }
            catch (InterruptedException ie) {
                break;
            }

            if (null != job) {
                job.run();
                job = null;
            }
        } while (true);
    }
}

class Job implements ITask
{
    private ITask _entity = null;
    private boolean _bSuicide = false;
    private Job() {};

    public Job(ITask task, boolean bSuicide)
    {
        this();
        _entity = task;
        _bSuicide = bSuicide;
    }

    public boolean equals(Object obj)
    {
        if (null == obj) {
            return false;
        }

        if (false == obj instanceof ITask) {
            return false;
        }

        return _entity.hashCode() == obj.hashCode();
    }

    public void run()
    {
        if (_bSuicide) {
            System.out.println("end :----" + Thread.currentThread());
            Thread.currentThread().interrupt();
        }
        else {
            _entity.run();
        }
    }
}
