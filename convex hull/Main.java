

import java.util.ArrayList;
import java.util.Collections;

public class Main {

    static int n;
    static int seed;
    static int trials = 7;
    static boolean debug;

    public static void main(String[] args)
    {
        if(args.length == 3)
        {
            n = Integer.parseInt(args[0]);
            seed = Integer.parseInt(args[1]);
            int temp = Integer.parseInt(args[2]);
            if(temp == 1)
                debug = true;
            else if(temp == 0)
                debug = false;
            else
            {
                System.out.println("Run the program with <N> <SEED> and <0> (no output) or <1> (output to file).");
                System.exit(-1);
            }

        }

        else
        {
            System.out.println("Run the program with <N> <SEED> and <0> (no output) or <1> (output to file).");
            System.exit(-1);
        }

        System.out.println("Starting program with N = " + n + " seed = " + seed);
        if(debug)
        {
            System.out.println("Debug = TRUE, results will be recorded.");

            if(n > 10000)
                System.out.println("Since N is larger then 10.000, the graph will not be drawn on screen.");
        }


        runTests();

	    System.out.println("\n\nProgram terminating.\n");

	    if(debug)
        {
            System.out.println("The output of the sequential solution is written to the output file: CONVEX-HULL-POINTS_SEQ_" + n + ".txt");
            System.out.println("The output of the parallel solution is written to the output file: CONVEX-HULL-POINTS_PARA_" + n + ".txt");
        }

    }

    public static void runTests()
    {
        System.out.println("\n\nStarting tests, be aware that the n-points are generated between each test." +
                "\nTherefore it may look like the tests take longer then they do in reality." +
                "\nThe generation of these points are not included in the timings.");
        ConvexHull convexHull;
        IntList convexEnvelope;
        ArrayList<Long> timings = new ArrayList<>();
        long start;
        long end;
        long seqSpeed;
        long paraSpeed;

        System.out.println("\nRunning: " + trials + " iterations of sequential convex");
        for(int i = 0; i < trials; i++)
        {
            convexHull = new ConvexHull(n, seed);
            start = System.nanoTime();
            convexEnvelope = SeqConvex.start(convexHull);
            end = System.nanoTime();
            timings.add((end-start)/1000000);
            System.out.println("Run: " + (i+1) + " of sequential convex used: " + (end-start)/1000000 + " ms");

            if(i == trials-1 && debug)
            {
                Oblig5Precode precode = new Oblig5Precode(convexHull, convexEnvelope);
                precode.writeHullPoints("SEQ");
                if(n <= 10000)
                    precode.drawGraph("SEQ");
            }
        }

        seqSpeed = calculateMedian(timings);
        System.out.println("\nSequential convex had a median run time of: " + seqSpeed + " ms over " + trials + " runs");
        timings.clear();


        System.out.println("\nRunning: " + trials + " iterations of parallel convex");
        for(int i = 0; i < trials; i++)
        {
            convexHull = new ConvexHull(n, seed);
            start = System.nanoTime();
            convexEnvelope = ParaConvex.start(convexHull);
            end = System.nanoTime();
            timings.add((end-start)/1000000);
            System.out.println("Run: " + (i+1) + " of parallel convex used: " + (end-start)/1000000 + " ms");


            if(i == trials-1 && debug)
            {
                Oblig5Precode precode = new Oblig5Precode(convexHull, convexEnvelope);
                precode.writeHullPoints("PARA");
                if(n <= 10000)
                    precode.drawGraph("PARA");
            }
        }

        paraSpeed = calculateMedian(timings);
        System.out.println("\nParallel convex had a median run time of: " + paraSpeed + " ms over " + trials + " runs");

        //If its too fast, we cant calculate speedup, cuz division by zero.
        if(seqSpeed > 0 && paraSpeed > 0)
        {
            double speedup;
            speedup = (double)seqSpeed/(double)paraSpeed;
            String speedupString = String.format("%.2f", speedup);
            speedupString = speedupString.replace(",", ".");
            System.out.println("\nThe parallel solution had a speedup ratio of: " + speedupString + ", against the sequential solution when N = " + n + ".");
        }
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
