
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

class paraSieve {
    private int n, root, numOfPrimes;
    private byte[] oddNumbers;
    private int numOfThreads;
    private CyclicBarrier cyclicBarrier;
    private CyclicBarrier cyclicBarrierTwo;
    private CyclicBarrier cyclicBarrierPreWorker;
    private int[] initialPrimes;
    private int totalPrimesFound;
    private int[] primes;
    private ReentrantLock reentrantLock;

    paraSieve(int n, int k) {
        this.n = n;
        this.root = (int) Math.sqrt(n);
        this.oddNumbers = new byte[(n / 16) + 1];
        this.numOfThreads = k;
        this.cyclicBarrierPreWorker = new CyclicBarrier(2);
        this.cyclicBarrier = new CyclicBarrier(numOfThreads + 1);
        this.cyclicBarrierTwo = new CyclicBarrier(numOfThreads + 1);
        this.reentrantLock = new ReentrantLock();
        this.totalPrimesFound = 0;
    }

    int[] getPrimes() {
        if (n <= 1) return new int[0];
        new Thread(new preWorker()).start();
        sieve();

        return collectPrimes();
    }

    private int[] collectPrimes() {
        int j = 1;

        for (int i = 3; i <= n; i += 2)
            if (isPrime(i))
                primes[j++] = i;

        return primes;
    }

    private void sieve() {
        mark(1);
        numOfPrimes = 1;

        int partitionSize = (n-3)/numOfThreads;
        int rest = (n-3)%numOfThreads;
        int current = 3;
        int id = 0;

        ArrayList<Thread> waitingThreads = new ArrayList<>();

        for(int i = 0; i < numOfThreads - rest; i++)
        {
            waitingThreads.add(new Thread(new Worker(current, current + partitionSize, id++)));
            current = current + partitionSize;
        }

        for(int i = 0; i < rest; i++)
        {
            waitingThreads.add(new Thread(new Worker(current, current + partitionSize + 1, id++)));
            current = current + partitionSize + 1;
        }

        try
        {
            cyclicBarrierPreWorker.await();
            for(Thread worker : waitingThreads)
                worker.start();
            cyclicBarrier.await();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.primes = new int[totalPrimesFound];
        this.primes[0] = 2;
    }

    private boolean isPrime(int num) {
        int bitIndex = (num % 16) / 2;
        int byteIndex = num / 16;

        return (oddNumbers[byteIndex] & (1 << bitIndex)) == 0;
    }

    private void mark(int num) {
        int bitIndex = (num % 16) / 2;
        int byteIndex = num / 16;
        oddNumbers[byteIndex] |= (1 << bitIndex);
    }

    public class preWorker implements Runnable
    {
        public void run()
        {
            seqSieve helper = new seqSieve(root);
            initialPrimes = helper.getPrimes();

            try
            {
                cyclicBarrierPreWorker.await();
            }

            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public class Worker implements Runnable
    {
        int start;
        int end;
        int id;

        public Worker(int start, int end, int id)
        {
            this.start = start;
            this.end = end;
            this.id = id;
        }

        public void run()
        {
            int ctr = 1;
            int currentPrime = initialPrimes[ctr];
            int multiple = -1;

            for(int i : initialPrimes)
            {
                if(i != initialPrimes[0])
                {
                    currentPrime = i;
                    multiple = findFirstMultiple(currentPrime);

                    if(multiple != -1)
                        traversePartly(multiple, end, currentPrime);
                }
            }

            gatherPrimes();

            try
            {
                cyclicBarrier.await();
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        void gatherPrimes()
        {
            int ctr = 0;

            for(int i = start; i <= end; i++)
                if(isPrime(i) && i%2 != 0)
                    ctr++;


            reentrantLock.lock();

            try
            {
                if(this.id == 0)
                    totalPrimesFound = totalPrimesFound + ctr + 1;
                else
                    totalPrimesFound = totalPrimesFound + ctr;
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            finally
            {
                reentrantLock.unlock();
            }
        }

        int findFirstMultiple(int prime)
        {
            if(prime*prime > end)
                return -1;

            int current = prime * prime;

            while(true)
            {
                if(current <= start)
                    current += prime * 2;

                if(current >= start && current <= end)
                    break;

                if(current >= end)
                    return -1;
            }

            if(current%prime == 0 && current != prime && current >= prime * prime)
                return current;

            return -1;
        }

        void traversePartly(int start, int end, int prime)
        {
            for(int i = start; i <= end; i += prime * 2)
            {
                mark(i);
            }
        }
    }
}