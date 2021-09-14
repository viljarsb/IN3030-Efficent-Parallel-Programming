

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.CyclicBarrier;

public class parallelSort {

    //get the number of true physical cores.
    static int numberOfProcessors;

    //Create and initilize some mutexes and a barrier used for syncronization.
    static ReentrantLock reentrantLock;
    static ReentrantLock reentrantLock_two;
    static CyclicBarrier barrier;

    static int[] arr;
    static int maxK;

    public static void run(int k, int[] arr) {
        //Let a thread work to sort K in the backround, while the mainthread initilizes threads that will search trough N.
        //If k is very large, this is going to be slow, maybe use more threads in such a case(?)
        Thread pre_worker = new Thread(new pre_worker(0, arr));
        pre_worker.start();
        numberOfProcessors = Runtime.getRuntime().availableProcessors();
        barrier = new CyclicBarrier(numberOfProcessors + 1);
        reentrantLock = new ReentrantLock(true);
        reentrantLock_two = new ReentrantLock(true);
        maxK = k;

        int created = 0;

        int modResult = (arr.length - maxK)%numberOfProcessors;
        int min =(int) (arr.length - maxK)/numberOfProcessors;
        int current = maxK;

        ArrayList<Thread> workers = new ArrayList<>();
        while(true)
        {
            if(created < numberOfProcessors)
            {
                workers.add(new Thread(new worker(created, current, current + min, arr)));
                current = current + min;
                created = created + 1;

                if(created == (numberOfProcessors - modResult) && modResult != 0)
                    min = min + 1;
            }


            else
            {
                try{
                    pre_worker.join();
                    for(Thread worker : workers)
                        worker.start();
                }

                catch (Exception e)
                {
                    System.out.println(e);
                }
                break;
            }
        }

        try {
            barrier.await();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static class pre_worker implements  Runnable{
        int id;
        int[] arr;

        public pre_worker(int id, int[] arr) {
            this.id = id;
            this.arr = arr;
        }

        public void run() {
            insertionSort(arr, maxK);
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
    }

    private static class worker implements Runnable {

        int id;
        int startIndex;
        int endIndex;
        static int[] arr;
        static int largestInQ = 0;

        public worker(int id, int startIndex, int endIndex, int[] arr)
        {
            this.id = id;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.arr = arr;
        }

        public void run()
        {
            findK(this.startIndex, this.endIndex, arr);

            try {
                barrier.await();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        public static void findK(int startIndex, int endIndex, int[] arr)
        {
            for(int i = startIndex; i < endIndex; i++)
            {
                if(arr[i] > arr[maxK-1])
                {
                    singleInsertionSort(i);
                }
            }
        }

        public static void singleInsertionSort(int indexFound)
        {

            //Since we run a fair reentrantlock, we can check if its worth to wait in Q.
            //or if a larger int is already waiting.
            reentrantLock.lock();
            try{
                if(largestInQ < arr[indexFound])
                    largestInQ = arr[indexFound];
            }

            catch (Exception e) {
                e.printStackTrace();
            }

            finally {
                reentrantLock.unlock();
            }
            reentrantLock_two.lock();
            try
            {
                if(arr[indexFound] > arr[maxK - 1])
                {
                    int temp = arr[maxK - 1];
                    arr[maxK - 1] = arr[indexFound];
                    arr[indexFound] = temp;

                    for(int i = maxK - 1; i > 0; i--)
                    {
                        if(arr[i] > arr[i-1]){
                            int temp2 = arr[i - 1];
                            arr[i - 1] = arr[i];
                            arr[i] = temp2;
                        }
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                reentrantLock_two.unlock();
            }
        }

    }
}
