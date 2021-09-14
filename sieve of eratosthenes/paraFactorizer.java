
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

public class paraFactorizer {
    private long nSquare;
    private final int numOfThreads;
    private int activeThreads;
    private long resultMatrix[][];
    private ArrayList<ArrayList<Long>> masterFactor;
    private CyclicBarrier barrier;

    public paraFactorizer(int n)
    {
        this.nSquare = (long)n*n;
        this.numOfThreads = 16;
        this.activeThreads = 0;
        this.barrier = new CyclicBarrier(100+1);
        this.masterFactor = new ArrayList<>();
        this.resultMatrix = new long[100][];
    }

    long[][] factorize()
    {
        int ctr = 1;

        while(ctr < 101)
        {
            Long currentNumber = nSquare - ctr;
            new Thread(new Worker(currentNumber, ctr)).start();
            ctr++;
        }

        try
        {
            barrier.await();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return resultMatrix;
    }

    private class Worker implements Runnable
    {
        private long number;
        private ArrayList<Long> localFactors;
        private int ctr;

        public Worker(long number, int ctr)
        {
            this.ctr = ctr;
            this.number = number;
            this.localFactors = new ArrayList<>();
        }

        public void run()
        {
            long currentFactor = number;

            long i = 0, j;
            while (currentFactor % 2 == 0)
            {
                localFactors.add(new Long(2));
                currentFactor /= 2;
            }

            j = 3;
            while (j <= Math.sqrt(currentFactor) + 1)
            {
                if (currentFactor % j == 0)
                {
                    localFactors.add(j);
                    currentFactor /= j;
                }
                else { j += 2;}
            }

            if (currentFactor > 1) { localFactors.add(currentFactor);}


            resultMatrix[ctr-1] = new long[localFactors.size()+1];
            resultMatrix[ctr-1][0] = number;

            for(int x = 1; x < resultMatrix[ctr-1].length; x++)
            {
                long localFactor = localFactors.remove(0);
                resultMatrix[ctr-1][x] = localFactor;
            }



            try
            {
                barrier.await();
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }
}
