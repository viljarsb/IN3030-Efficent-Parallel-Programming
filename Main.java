
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Main {

    public static void main(String[] args) {

        int n = Integer.parseInt(args[0]);
        int k = Integer.parseInt(args[1]);

        if(n < 16)
        {
            System.out.println("Supply N equal to or greater then 16.");
            System.exit(-1);
        }

        if(k == 0)
        {
            int cores = Runtime.getRuntime().availableProcessors();
            k = cores;
            System.out.println("Defaulting to: " + cores + " cores");
        }

        System.out.println("Starting program with N = " + n + " and CORE-COUNT = " + k);

        runParaTests(n, k);
        runSeqTest(n);
    }


    private static void runParaTests(int n, int k)
    {
        paraSieve paraSieve = new paraSieve(n, k);
        paraFactorizer paraFactorizer = new paraFactorizer(n);

        ArrayList<Long> timings = new ArrayList<>();
        long start;
        long end;
        long median;

        System.out.println("\nRunning test of parallel sieve");
        for(int i = 0; i < 7; i++)
        {
            start = System.nanoTime();
            int[] result = paraSieve.getPrimes();
            end = System.nanoTime();
            timings.add((end-start)/1000000);
            System.out.println("Parallel sieve used: " + (end-start)/1000000 + " ms on run: "+ (i+1));
        }

        median = calculateMedian(timings);
        System.out.println("Parallel sieve had a median run time of: " + median + " ms over 7 runs");

        timings = new ArrayList<>();
        long[][] resultMatrix = null;
        System.out.println("\nRunning test of parallel factoriser");
        for(int i = 0; i < 7; i++)
        {
            start = System.nanoTime();
            resultMatrix = paraFactorizer.factorize();
            end = System.nanoTime();
            timings.add((end-start)/1000000);
            System.out.println("Parallel factoriser used: " + (end-start)/1000000 + " ms on run: "+ (i+1));
        }

        median = calculateMedian(timings);
        System.out.println("Parallel factoriser had a median run time of: " + median + " ms over 7 runs");


        //just write out the output of one of the runs.
        precode precodeT = new precode(n);
        int ctr = 99;
        while(ctr >= 0)
        {
            for(long element : resultMatrix[ctr])
            {
                if(element != resultMatrix[ctr][0])
                    precodeT.addFactor(resultMatrix[ctr][0], element);
            }
            ctr--;
        }

        precodeT.writeFactors();
    }

    private static void runSeqTest(int n)
    {
        seqSieve seqSieve = new seqSieve(n);
        seqFactorizer seqFactorizer = new seqFactorizer(n);

        ArrayList<Long> timings = new ArrayList<>();
        long start;
        long end;
        long median;

        System.out.println("\nRunning test of serial sieve");
        for(int i = 0; i < 7; i++)
        {
            start = System.nanoTime();
            int[] result = seqSieve.getPrimes();
            end = System.nanoTime();
            timings.add((end-start)/1000000);
            System.out.println("Serial sieve used: " + (end-start)/1000000 + " ms on run: "+ (i+1));
        }

        median = calculateMedian(timings);
        System.out.println("Serial sieve had a median run time of: " + median + " ms over 7 runs");

        timings = new ArrayList<>();
        long[][] resultMatrix;
        System.out.println("\nRunning test of serial factoriser");
        for(int i = 0; i < 7; i++)
        {
            start = System.nanoTime();
            resultMatrix = seqFactorizer.factorize();
            end = System.nanoTime();
            timings.add((end-start)/1000000);
            System.out.println("Serial factoriser used: " + (end-start)/1000000 + " ms on run: "+ (i+1));
        }

        median = calculateMedian(timings);
        System.out.println("Serial factoriser had a median run time of: " + median + " ms over 7 runs");
    }

    private static long calculateMedian(ArrayList<Long> timing)
    {
        Collections.sort(timing);
        if(timing.size() % 2 == 1)
            return  timing.get((timing.size() + 1)/ 2 - 1);
        else
            return (timing.get(timing.size() / 2 - 1) + timing.get(timing.size() / 2)) / 2;
    }

}
