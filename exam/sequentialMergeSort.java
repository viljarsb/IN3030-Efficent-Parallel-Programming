
//This is just a copy of the sequential code from the exam paper.
public class sequentialMergeSort
{
    public static void merge(int[] left_arr, int[] right_arr, int[] arr, int left_size, int right_size)
    {
        int i = 0;
        int l = 0;
        int r = 0;

        while(l < left_size && r < right_size)
        {

            if(left_arr[l] < right_arr[r])
                arr[i++] = left_arr[l++];

            else
                arr[i++] = right_arr[r++];
        }

        while(l < left_size)
            arr[i++] = left_arr[l++];

        while(r < right_size)
            arr[i++] = right_arr[r++];
    }

    public static void mergeSort(int[] arr, int len)
    {
        if(len < 2)
            return;

        int mid = len/2;
        int[] left_arr = new int[mid];
        int[] right_arr = new int[len - mid];

        int k = 0;

        for(int i = 0; i < len; i++)
        {
            if(i < mid)
                left_arr[i] = arr[i];
            else
            {
                right_arr[k] = arr[i];
                k++;
            }
        }

        mergeSort(left_arr, mid);
        mergeSort(right_arr, len-mid);
        merge(left_arr, right_arr, arr, mid, len-mid);
    }


}
