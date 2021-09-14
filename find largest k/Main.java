
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Main {

    static double speedup;
    static long A2;
    static long A3;
    public static void main(String[] args) {
        System.out.println("Program starting execution.");
        System.out.println("You have choosen N = " + args[0] + " and K = " + args[1]);
        System.out.println("The program will do 7 runs and calculate the median value\nin terms of speed of execution of the three algorithms.");
        int n = Integer.parseInt(args[0]);
        int k = Integer.parseInt(args[1]);

       	testA1(n, k, 7);
        testA2(n, k, 7);
        testA3(n, k, 7);

        long precentChng = Math.round(Math.abs(100 * ((A2 - A3) / (double)A2)));

        if(A3 < A2)
            System.out.println("A3 finished the task: " + (precentChng) + "% faster than A2.");
        else
            System.out.println("A3 finished the task: " + (precentChng) + "% slower than A2.");
    }

    public static int[] createArray(int n)
    {
        int[] arr = new int[n];
        Random random_generator = new Random(7634);
        for(int i = 0; i < n; i++)
        {
            arr[i] = random_generator.nextInt(Integer.MAX_VALUE);
        }
        return arr;
    }

    public static void testA1(int n, int k, int trials)
    {
        ArrayList<Long> timing = new ArrayList<Long>();
        int[] arr;
        for(int i = 0; i < trials; i++)
        {
            arr = createArray(n);
            long start = System.nanoTime();
            Arrays.sort(arr);
            int[] largestK = new int[k];
            int counter = 0;

            for(int j = arr.length - 20; j < arr.length; j++)
            {
                largestK[counter] = arr[j];
                counter++;
            }
            long end = System.nanoTime();
            System.out.println("A1 run: " + (i + 1) + " used: " + (end-start)/100000 + " milliseconds");
            timing.add((end-start)/100000);
        }

        System.out.println("A1 had a median run time of: " + calculateMedian(timing) + " milliseconds over: " + trials + " executions");
    }

    public static void testA2(int n, int k, int trials)
    {
        ArrayList<Long> timing = new ArrayList<Long>();

        for(int i = 0; i < trials; i++)
        {
            int[] arr = createArray(n);
            long start = System.nanoTime();
            SequentialSort.run(arr, k);
            long end = System.nanoTime();
            System.out.println("A2 run: " + (i + 1) + " used: " + (end-start)/100000 + " milliseconds");
            timing.add((end-start)/100000);
        }
        System.out.println("A2 had a median run time of: " + calculateMedian(timing) + " milliseconds over: " + trials + " executions");
        A2 = calculateMedian(timing);
    }

    public static void testA3(int n, int k, int trials)
    {
        ArrayList<Long> timing = new ArrayList<Long>();
        for(int i = 0; i < trials; i++)
        {
            int[] arr = createArray(n);
            long start = System.nanoTime();
            parallelSort.run(k, arr);
            long end = System.nanoTime();
            System.out.println("A3 run: " + (i + 1) + " used: " + (end-start)/100000 + " milliseconds");
            timing.add((end-start)/100000);
        }
        System.out.println("A3 had a median run time of: " + calculateMedian(timing) + " milliseconds over: " + trials + " executions");
        A3 = calculateMedian(timing);
    }

    public static long calculateMedian(ArrayList<Long> timing)
    {
        Collections.sort(timing);
        if(timing.size() % 2 == 1)
            return  timing.get((timing.size() + 1)/ 2 - 1);
        else
            return (timing.get(timing.size() / 2 - 1) + timing.get(timing.size() / 2)) / 2;
    }
}
