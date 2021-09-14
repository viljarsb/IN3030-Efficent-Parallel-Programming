
import java.util.concurrent.Semaphore;

public class semCyclicBarrier
{
    private int numOfThreads;
    private int numOfWaiting;
    private Semaphore lock;
    private Semaphore barrierOne;
    private Semaphore barrierTwo;

    /*
        The constructor for the cyclic barrier.
        Initiates the semaphores, and sets the counter and num of threads.

        @Param  Int, the numbers of threads that will rendezvous at the barrier.
    */
    public semCyclicBarrier(int numOfThreads)
    {
        this.numOfThreads = numOfThreads;
        this.numOfWaiting = 0;
        this.lock = new Semaphore(1);
        this.barrierOne = new Semaphore(0);
        this.barrierTwo = new Semaphore(1);
    }

    public void await()
    {
        try
        {
            lock.acquire();
            numOfWaiting++;
            System.out.println("Waiting threads increased to: " + numOfWaiting);

            if(numOfWaiting == numOfThreads)
            {
                System.out.println("All threads have reached the barrier.");
                barrierTwo.acquire();
                barrierOne.release();
            }

            lock.release();
            barrierOne.acquire();
            barrierOne.release();

            lock.acquire();
            numOfWaiting--;

            if(numOfWaiting == 0)
            {
                barrierOne.acquire();
                barrierTwo.release();
            }

            lock.release();

            barrierTwo.acquire();
            barrierTwo.release();
        }

        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
