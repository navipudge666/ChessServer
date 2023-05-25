package ThreadDispatcher;

import java.util.Random;

public class RandomTask extends ThreadedTask
{
    public RandomTask()
    {
        super("RandomTask");
    }

    public RandomTask(String name)
    {
        super(name);
    }

    @Override
    protected void doWork()
    {
        Random random = new Random();
        int range = 1000000;
        int target = random.nextInt(range);
        int cur = random.nextInt(range);
        int i = 0;
        while (cur != target)
            target = random.nextInt();
    }
}
