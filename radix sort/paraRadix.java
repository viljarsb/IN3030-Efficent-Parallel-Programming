

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class paraRadix
{
    private int[] inputArray;
    private int[] outputArray;
    private int[] sumCount;

    private int numOfDigits;
    private int placeVal;

    private int maxNum;
    private int numOfThreads;


    private ReentrantLock lock;
    private CyclicBarrier threadBarrier;
    private CyclicBarrier barrier;

    public paraRadix()
    {
        this.numOfThreads = Runtime.getRuntime().availableProcessors();
        this.lock = new ReentrantLock(true);
        this.threadBarrier = new CyclicBarrier(numOfThreads);
        this.barrier = new CyclicBarrier(numOfThreads + 1);
    }

    public int[] sort(int[] arr)
    {
        this.inputArray = arr;
        int workAmount = inputArray.length/numOfThreads;
        int rest = inputArray.length%numOfThreads;
        int start = 0;
        int counter=0;

        for(int i = 0; i < numOfThreads-rest; i++)
        {
            new Thread(new Worker(counter, start, start + workAmount)).start();
            start += workAmount;
            counter++;
        }

        for(int i = 0; i < rest; i++)
        {
            new Thread(new Worker(counter, start, start + workAmount+1)).start();
            start += workAmount+1;
            counter++;
        }

        try
        {
            barrier.await();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return inputArray;
    }


    private class Worker implements Runnable
    {
        private int id;
        private int start;
        private int end;

        public Worker(int id, int start, int end)
        {
            this.id = id;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run()
        {
            setMax();

            try
            {
                threadBarrier.await();
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            if(id == 0)
            {
                sumCount = new int[10];
                Arrays.fill(sumCount, 0);
                numOfDigits = String.valueOf(maxNum).length();
                placeVal = 1;
            }

            try
            {
                threadBarrier.await();
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            while(numOfDigits > 0)
            {
                countSort();

                try
                {
                    threadBarrier.await();
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }

                if(id == 0)
                {
                    numOfDigits--;
                    placeVal *= 10;
                    sumCount = new int[10];
                    Arrays.fill(sumCount, 0);
                }

                try
                {
                    threadBarrier.await();
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }


            try
            {
                barrier.await();
            }

            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        private void countSort()
        {
            int[] localCount = new int[10];
            Arrays.fill(localCount, 0);

            for(int i = start; i < end; i++)
            {
                int digit = (inputArray[i] / placeVal) % 10;
                localCount[digit]++;
            }

            for(int i = 1; i < 10; i++)
                localCount[i] += localCount[i - 1];

            updateGlobalCount(localCount);

            try
            {
                if(id == 0)
                    outputArray = new int[inputArray.length];
                threadBarrier.await();
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            if(id == 0)
            {
                for (int i = inputArray.length - 1; i >= 0; i--)
                {
                    int digit = (inputArray[i] / placeVal) % 10;
                    outputArray[sumCount[digit] - 1] = inputArray[i];
                    sumCount[digit]--;
                }
            }

            try
            {
                threadBarrier.await();
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            for(int i = start; i < end; i++)
                inputArray[i] = outputArray[i];
        }

        private void setMax()
        {
            int localMax = 0;
            for(int i = start; i < end; i++)
                if(inputArray[i] > localMax)
                    localMax = inputArray[i];

            lock.lock();

            try
            {
                if(maxNum < localMax)
                    maxNum = localMax;
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            finally
            {
                lock.unlock();
            }

        }

        private void updateGlobalCount(int[] localCount)
        {
            lock.lock();

            try
            {
                for(int i = 0; i < 10; i++)
                {
                    sumCount[i] += localCount[i];
                }
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            finally
            {
                lock.unlock();
            }
        }
    }
}
