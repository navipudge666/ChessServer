package ThreadDispatcher;

import java.util.ArrayDeque;

public class ThreadDispatcher
{
    protected static ThreadDispatcher instance;

    protected int maxPoolSize = 10;
    protected final ArrayDeque<ThreadedTask> allTasks = new ArrayDeque<>();
    protected final ArrayDeque<ThreadedTask> queue = new ArrayDeque<>();
    protected volatile int workerCount = 0;

    public ThreadDispatcher()
    {
        for (int i = 0; i < maxPoolSize; i++)
        {
            (new Thread(new ThreadWorker())).start();
        }
    }

    public static ThreadDispatcher getInstance()
    {
        if (instance == null)
        {
            synchronized (ThreadDispatcher.class)
            {
                if (instance == null)
                    instance = new ThreadDispatcher();
            }
        }
        return instance;
    }

    public void Add(ThreadedTask task)
    {
        synchronized (queue)
        {
            this.queue.addFirst(task);
            this.allTasks.addFirst(task);
        }
        //new Thread(task).start();
        ThreadMonitor.Update();
    }

    public void AddInQueue(ThreadedTask task)
    {
        synchronized (queue)
        {
            this.queue.add(task);
            this.allTasks.add(task);
        }
        ThreadMonitor.Update();
    }

    public void setMaxPoolSize(int _maxPoolSize) throws IllegalArgumentException
    {
        if (this.maxPoolSize < 0)
            throw new IllegalArgumentException();
        for (int i = 0; i < _maxPoolSize - this.maxPoolSize; i++)
            (new Thread(new ThreadWorker())).start();
        this.maxPoolSize = _maxPoolSize;
    }
}
