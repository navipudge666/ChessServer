package ThreadDispatcher;

public class ThreadWorker implements Runnable
{
    public void run()
    {
        ThreadDispatcher dispatcher = ThreadDispatcher.getInstance();
        long id = Thread.currentThread().getId();
        synchronized (dispatcher)
        {
            dispatcher.workerCount++;
        }
        while (dispatcher.workerCount <= dispatcher.maxPoolSize)
        {
            ThreadedTask task = null;
            synchronized (dispatcher.queue)
            {
                if (!dispatcher.queue.isEmpty())
                    task = dispatcher.queue.pop();
            }
            if (task != null)
            {
                task.run();
                dispatcher.allTasks.remove(task);
            }
        }
        synchronized (dispatcher)
        {
            dispatcher.workerCount--;
        }
    }
}
