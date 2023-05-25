package ThreadDispatcher;

public class ThreadMonitor
{
    public static void Update()
    {
        ThreadDispatcher dispatcher = ThreadDispatcher.getInstance();
        synchronized (dispatcher.queue)
        {
            cls();
            //System.out.println(ThreadDispatcher.workerCount + " " + ThreadDispatcher.maxPoolSize);
            for(var task : dispatcher.allTasks)
            {
                System.out.printf("%s : %d : %s%n", task.name, task.id, task.status);
            }
        }
    }

    private static void cls()
    {
        //System.out.println("-------------------------------------------");
        for (int i = 0; i < 20; i++)
            System.out.println();
    }
}
