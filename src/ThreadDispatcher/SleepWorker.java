package ThreadDispatcher;

public class SleepWorker extends ThreadedTask
{
    public SleepWorker(String name)
    {
        super(name);
    }

    public void doWork()
    {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}