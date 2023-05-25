package ThreadDispatcher;

public abstract class ThreadedTask implements Runnable
{
    protected String name;
    protected long id;
    protected String status = "waiting";

    public ThreadedTask(String name)
    {
        this.name = name;
    }

    public void run()
    {
        status = "running";
        id = Thread.currentThread().getId();
        ThreadMonitor.Update();
        doWork();
        status = "finished";
        synchronized (ThreadDispatcher.getInstance())
        {
            ThreadDispatcher.getInstance().allTasks.remove(this);
        }
        ThreadMonitor.Update();
    }

    protected abstract void doWork();
}
