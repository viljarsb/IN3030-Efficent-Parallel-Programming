
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Sort {
    private static int[] masterArray;
    public static void main(String[] args)
    {
        if(args.length != 3)
        {
            System.out.println("Run with <Seed> <N> <Trials>");
            System.exit(-1);
        }

        try
        {
            int seed = Integer.parseInt(args[0]);
            int size = Integer.parseInt(args[1]);
            int trials = Integer.parseInt(args[2]);
            masterArray = new int[size];
            System.out.println("\nProgram starting testing of seq and para merge-sort\nWith seed: " + seed + ", size: " + size + ", trails: " + trials + "\n");
            runSortTest(seed, size, trials);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void runSortTest(int seed, int size, int trials)
    {
        masterArray = generateArray(seed, size);
        int[] array = new int[size];

        ArrayList<Long> timings = new ArrayList<>();
        long start, end, resultPara, resultSeq;

        System.out.println("Starting tests of parallel merge-sort, the program will run: " + trials + " tests.");
        for(int i = 0; i < trials; i++)
        {
            System.arraycopy(masterArray, 0, array, 0, masterArray.length);
            start = System.nanoTime();
            paraMergeSort.parallelMergeSort(array, array.length);
            end = System.nanoTime();
            System.out.println("Parallel merge-sort used: " + (end-start)/1000000 + " ms on run: " + (i + 1) + "/" + trials);
            timings.add((end-start)/1000000);

            //just a simple check to make sure its correct.
            if(i == trials-1)
                validateResults(array);
        }

        resultPara = calculateMedian(timings);
        System.out.println("\nParallel merge-sort used a median time of " + resultPara + " ms over " + trials + " runs\n");
        timings.clear();

        System.out.println("Starting tests of sequential merge-sort, the program will run: " + trials + " tests.");
        for(int i = 0; i < trials; i++)
        {
            System.arraycopy(masterArray, 0, array, 0, masterArray.length);
            start = System.nanoTime();
            sequentialMergeSort.mergeSort(array, array.length);
            end = System.nanoTime();
            System.out.println("Sequential merge-sort used: " + (end-start)/1000000 + " ms on run: " + (i + 1) + "/" + trials);
            timings.add((end-start)/1000000);

            //just a simple check to make sure its correct.
            if(i == trials-1)
                validateResults(array);
        }

        resultSeq = calculateMedian(timings);
        System.out.println("\nSequential merge-sort used a median time of " + resultSeq + " ms over " + trials + " runs\n");

        double speedup = (double)resultSeq/resultPara;
        System.out.format("\nSPEEDUP: %.2f", speedup);
        timings.clear();
    }

    /* Helper functions */

    /* Generates a random array from the size and seed provided */
    private static int[] generateArray(int seed, int size)
    {
        Random rd = new Random(seed);
        int[] arr = new int[size];

        for(int i = 0; i < size; i++)
        {
            arr[i] = Math.abs(rd.nextInt());
        }

        return arr;
    }

    /* Calculates the median runtime from a set of timings */
    private static long calculateMedian(ArrayList<Long> timing)
    {
        Collections.sort(timing);
        if(timing.size() % 2 == 1)
            return  timing.get((timing.size() + 1)/ 2 - 1);
        else
            return (timing.get(timing.size() / 2 - 1) + timing.get(timing.size() / 2)) / 2;
    }

    /* Just makes sure that the result of the merge-sort is correct */
    private static void validateResults(int[] a)
    {
       int[] a_copy = new int[a.length];

       System.arraycopy(a, 0, a_copy, 0, a.length);
       Arrays.sort(a_copy);


       for(int i = 0; i < a.length; i++)
       {
           if(a[i] != a_copy[i])
           {
               System.out.println("Error in merge-sort");
               System.exit(-1);
           }
       }

       System.out.println("The array is correctly sorted.");
    }
}
