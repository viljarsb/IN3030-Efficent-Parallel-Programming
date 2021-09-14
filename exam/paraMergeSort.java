
public class paraMergeSort
{
    public static void parallelMergeSort(int[] inputArray, int len)
    {
        int numOfThreads = Runtime.getRuntime().availableProcessors();
        mergeSort(inputArray, len, numOfThreads);
    }

    /*
        This method performs the upwards merging of two sub-arrays into the larger array one step up.
        Its called once the once the recursive division of the arrays reaches the bottom and starts to return upwards.

        @Params, the left, right and mother arrays and the left and right size.
    */
    private static void merge(int[] left_arr, int[] right_arr, int[] arr, int left_size, int right_size)
    {
        int i = 0;
        int l = 0;
        int r = 0;

        while(l < left_size && r < right_size)
        {
            if(left_arr[l] < right_arr[r])
            {
                arr[i++] = left_arr[l++];
            }

            else
            {
                arr[i++] = right_arr[r++];
            }
        }

        while(l < left_size)
        {
            arr[i++] = left_arr[l++];
        }

        while(r < right_size)
        {
            arr[i++] = right_arr[r++];
        }
    }


    /*
        This method divides the array supplied into two sub-arrays.
        If there is any free threads, it allows a new thread to continue to split
        the left sub-array, while the caller continues on the right sub-array.
        If no threads free, the caller does both the sides.

        Once the function returns from the recursion, it starts to merge
        the two sub-arrays upwards.

        @Params, The array to divide and its length.
     */
    private static void mergeSort(int[] inputArray, int len, int numOfThreads)
    {
        if(len < 2)
            return;

        int mid = len/2;
        int[] left_arr = new int[mid];
        int[] right_arr = new int[len-mid];

        int r = 0;
        int l = 0;

        for(int i = 0; i < len; i++)
        {
            if(i < mid)
            {
                left_arr[l] = inputArray[i];
                l++;
            }

            else
            {
                right_arr[r] = inputArray[i];
                r++;
            }
        }

        if(numOfThreads > 1)
        {
            Thread leftThread = new Thread(new worker(left_arr, mid, numOfThreads/2));
            leftThread.start();
            mergeSort(right_arr, len - mid, numOfThreads/2);

            try
            {
                leftThread.join();
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        else
        {
            mergeSort(left_arr, mid, 0);
            mergeSort(right_arr, len-mid, 0);
        }

        merge(left_arr, right_arr, inputArray, mid, len-mid);
    }

    /*
        This is the thread class that works in the merge-sort.
        It simply calls merge-sort on the the array it was supplied.
    */
    private static class worker implements Runnable
    {
        private int[] arr;
        private int len;
        private int numOfThreads;

        public worker(int[] arr, int len, int numOfThreads)
        {
            this.arr = arr;
            this.len = len;
            this.numOfThreads = numOfThreads;
        }

        public void run()
        {
            mergeSort(arr, len, numOfThreads);
        }
    }
}
