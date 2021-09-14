

/*
    Class holding a simple method for a Sequential Sorting of an int array.
*/
public class SequentialSort {
    public static void run(int arr[], int k){

        insertionSort(arr, k);
         findK(arr, k);
    }

    private static void insertionSort(int arr[], int k){
        int i;

        //from 0 to k.
        for(int j = 0; j < k; j++) {

        int currentValue = arr[j + 1];
        i = j;

        while(i >= 0  && arr[i] < currentValue){
            arr[i + 1] = arr[i];
            i = i - 1;
        }
        arr[i + 1] = currentValue;
     }
    }

    private static void singleInsertionSort(int arr[], int k){
        for(int i = k - 1; i > 0; i--)
        {
            if(arr[i] > arr[i-1]){
                int temp = arr[i - 1];
                arr[i - 1] = arr[i];
                arr[i] = temp;
            }
        }
    }

    private static void findK(int arr[], int k){
        for(int i = k; i < arr.length; i++){
            if(arr[i] > arr[k - 1]){
                int temp = arr[k - 1];
                arr[k - 1] = arr[i];
                arr[i] = temp;
                singleInsertionSort(arr, k);
            }
        }
    }
}
