
import java.util.Random;

public class testWorker implements Runnable
{
    private int id;
    private semCyclicBarrier testBarrier;
    private Random rand;

    public testWorker(int id, semCyclicBarrier testBarrier)
    {
        this.id = id;
        this.testBarrier = testBarrier;
        this.rand = new Random();
    }

    public void run()
    {
        int randomInt = Math.abs(rand.nextInt(10000));

        try
        {
            System.out.println("Thread: " + id + " will sleep for: " + randomInt + " ms");
            Thread.sleep(randomInt);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("Thread: " + id + " waiting at barrier test ONE.");
        testBarrier.await();
        System.out.println("Thread: " + id + " released from barrier test ONE.");

        randomInt = Math.abs(rand.nextInt(10000));

        try
        {
            System.out.println("Thread: " + id + " will sleep for: " + randomInt + " ms");
            Thread.sleep(randomInt);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("Thread: " + id + " waiting at barrier test TWO.");
        testBarrier.await();
        System.out.println("Thread: " + id + " released from barrier test TWO.");
    }
}
