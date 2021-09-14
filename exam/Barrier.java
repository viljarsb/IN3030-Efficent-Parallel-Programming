
public class Barrier
{
    private static semCyclicBarrier testBarrier;

    public static void main(String[] args)
    {
        if(args.length != 1)
        {
            System.out.println("Run with <Num of threads>");
            System.exit(-1);
        }

        try
        {
            int numOfThreads = Integer.parseInt(args[0]);
            testBarrier = new semCyclicBarrier(numOfThreads + 1);
            runTest(numOfThreads);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void runTest(int numOfThreads)
    {
        System.out.println("Main thread will now start " + numOfThreads +  " threads.");

        for(int i = 0; i < numOfThreads; i++)
        {
            new Thread(new testWorker(i, testBarrier)).start();
        }


        System.out.println("Main thread waiting at barrier test ONE.");
        testBarrier.await();
        System.out.println("Main thread released from barrier test ONE.");

        System.out.println("Main thread waiting at barrier test TWO.");
        testBarrier.await();
        System.out.println("Main thread released from barrier test TWO.");
    }
}
