
import java.util.ArrayList;
import java.util.Collections;

public class Main
{

    public static void main(String[] args) {
    	if(args.length != 2)
		{
			System.out.println("Run with <N> <Seed>");
			System.exit(-1);
		}

    	int n, seed;

    	try
		{
			n = Integer.parseInt(args[0]);
			seed = Integer.parseInt(args[1]);
			System.out.println("Running tests with N = " + n + " SEED = " + seed);
			runTests(n, seed);
		}

    	catch (Exception e)
		{
			e.printStackTrace();
		}

    }


    public static void runTests(int n, int seed)
	{
		long start, end;
		long seq, para;
		ArrayList<Long> timings = new ArrayList<>();
		ArrayList<int[]> results = new ArrayList<>();

		System.out.println("Starting testing of seq radix.");
		for(int i = 0; i < 7; i++)
		{
			int [] arr = Oblig4Precode.generateArray(n, seed);
			start = System.nanoTime();
			Radix.radixsort(arr, arr.length);
			end = System.nanoTime();
			long value = (end-start)/1000000;
			timings.add(value);
			controlVadility(arr);
			results.add(arr);
			System.out.println("Run: " + (i + 1) + " of seq radix used: " + value + " ms");
		}

		System.out.println("\nSeq radix had a median run time of: " + calculateMedian(timings) + " ms\n");
		seq = calculateMedian(timings);
		timings.clear();

		System.out.println("Starting testing of para radix.");
		paraRadix paraRadixObj = new paraRadix();
		for(int i = 0; i < 7; i++)
		{
			int [] arr = Oblig4Precode.generateArray(n, seed);
			start = System.nanoTime();
			int [] b = paraRadixObj.sort(arr);
			end = System.nanoTime();
			long value = (end-start)/1000000;
			timings.add(value);
			controlVadility(b);
			results.add(b);
			System.out.println("Run: " + (i + 1) + " of para radix used: " + value + " ms");
		}


		System.out.println("\nPara radix had a median run time of: " + calculateMedian(timings) + " ms\n");
		para = calculateMedian(timings);

		float res = percent(para, seq);

		if(res < 0)
			System.out.println("Para radix ran: " + res + "% slower then seq radix.");
		else
			System.out.println("Para radix ran: " + res + "% faster then seq radix.");

		System.out.println("\nTesting to make sure seq and para results are equal");

		for(int i = 0; i < 7; i++)
		{
			if(results.get(i).equals(results.get(i+7)))
				System.out.println("Results from run: " + (i+1)  + " are not equal.");

			else
				System.out.println("Results from run: " + (i+1) + " are equal.");
		}

		int[] a, b;

		a = results.get(0);
		b = results.get(7);
		Oblig4Precode.saveResults(Oblig4Precode.Algorithm.SEQ, seed, a);
		Oblig4Precode.saveResults(Oblig4Precode.Algorithm.PAR, seed, b);
	}

	static float percent(long a, long b)
	{
		float result = 0;
		result = ((b - a) * 100) / a;
		return  result;
	}

	private static long calculateMedian(ArrayList<Long> timing)
	{
		Collections.sort(timing);
		if(timing.size() % 2 == 1)
			return  timing.get((timing.size() + 1)/ 2 - 1);
		else
			return (timing.get(timing.size() / 2 - 1) + timing.get(timing.size() / 2)) / 2;
	}

	private static void controlVadility(int[] arr)
	{
		for(int i = 0; i < arr.length - 1; i++)
		{
			if(arr[i] > arr[i+1])
			{
				System.out.println("Sort wrong, at index: " + i);
				return;
			}
		}
		System.out.println("Results are valid.");
	}
}
