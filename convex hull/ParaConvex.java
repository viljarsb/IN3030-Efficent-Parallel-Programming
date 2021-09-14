

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ParaConvex
{
    private static ConvexHull convexHull;
    private static ReentrantLock lock;
    private static CyclicBarrier mainBarrier;
    private static CyclicBarrier threadBarrier;
    private static CyclicBarrier treeWorkerBarrier;
    private static IntList convexEnvelope;
    private static IntList rightList;
    private static IntList leftList;
    private static int leftGlobal;
    private static int leftGlobalDistance;
    private static int rightGlobal;
    private static int rightGlobalDistance;
    private static AtomicInteger threadsAlive;
    private static AtomicInteger threadIdCounter;
    private static int numThreads;
    private static recTree recursionTreeLeft;
    private static recTree getRecursionTreeRight;
    private static CyclicBarrier recBarrier;
    private static IntList[][] holder;
    private static IntList globalLeftEnvelope;
    private static IntList getGlobalRightEnvelope;

    public static IntList start(ConvexHull conv)
    {
        threadIdCounter = new AtomicInteger(0);
        numThreads = Runtime.getRuntime().availableProcessors();
        convexHull = conv;
        lock = new ReentrantLock(true);
        treeWorkerBarrier = new CyclicBarrier(3);
        mainBarrier = new CyclicBarrier( numThreads + 1);
        recBarrier = new CyclicBarrier(2);
        threadBarrier = new CyclicBarrier(numThreads);
        convexEnvelope = new IntList();
        holder = new IntList[numThreads][2];

        rightList = new IntList();
        leftList = new IntList();

        rightGlobal = -1;
        leftGlobal = -1;
        rightGlobalDistance = 0;
        leftGlobalDistance = 0;

        int n = convexHull.x.length;
        int workAmount = n/numThreads;
        int startPos = 0;

        for(int i = 0; i < numThreads; i++)
        {
            new Thread(new InitialWorker(i, startPos, startPos + workAmount)).start();
            startPos = startPos + workAmount;
        }

        try
        {
            mainBarrier.await();
        }

        catch(Exception e)
        {
            e.printStackTrace();
        }


        threadsAlive = new AtomicInteger(4);
        recursionTreeLeft = new recTree();
        recursionTreeLeft.getRoot().point = leftGlobal;
        getRecursionTreeRight = new recTree();
        getRecursionTreeRight.getRoot().point = rightGlobal;
        recursionTreeLeft.getRoot().left = new Node();
        recursionTreeLeft.getRoot().right = new Node();
        getRecursionTreeRight.getRoot().left = new Node();
        getRecursionTreeRight.getRoot().right = new Node();

        new Thread(new RecWorker(leftGlobal, convexHull.MAX_X, leftList, recursionTreeLeft.getRoot().right)).start();
        new Thread(new RecWorker(convexHull.MIN_X, leftGlobal, leftList, recursionTreeLeft.getRoot().left)).start();
        new Thread(new RecWorker(rightGlobal, convexHull.MIN_X, rightList, getRecursionTreeRight.getRoot().right)).start();
        new Thread(new RecWorker(convexHull.MAX_X, rightGlobal, rightList, getRecursionTreeRight.getRoot().left)).start();

        try
        {
            recBarrier.await();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        new Thread(new TreeEnvelopeWorker(0)).start();
        new Thread(new TreeEnvelopeWorker(1)).start();

        try
        {
            treeWorkerBarrier.await();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
        convexEnvelope.add(convexHull.MAX_X);
        convexEnvelope.append(globalLeftEnvelope);
        convexEnvelope.add(convexHull.MIN_X);
        convexEnvelope.append(getGlobalRightEnvelope);
        return convexEnvelope;
    }


    private static class InitialWorker implements Runnable
    {
        private int id;
        private int start;
        private int end;

        public InitialWorker(int id, int start, int end)
        {
            this.id = id;
            this.start = start;
            this.end = end;
        }

        public void run()
        {
            setMaxMinValues();

            try
            {
                threadBarrier.await();
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            findPoints(convexHull.MIN_X, convexHull.MAX_X);

            try
            {
                mainBarrier.await();
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        private void setMaxMinValues()
        {
            int localMaxX = convexHull.MAX_X;
            int localMinX = convexHull.MIN_X;
            int localMaxY = convexHull.MAX_Y;


            for(int i = start; i < end; i++)
            {
                if(convexHull.x[i] > convexHull.x[localMaxX])
                    localMaxX = i;

                else if(convexHull.x[i] < convexHull.x[localMinX])
                    localMinX = i;

                if(convexHull.y[i] > convexHull.y[localMaxY])
                    localMaxY = i;
            }


            lock.lock();

            try
            {
                if(convexHull.x[convexHull.MAX_X] < convexHull.x[localMaxX])
                    convexHull.MAX_X = localMaxX;

                if(convexHull.x[convexHull.MIN_X] > convexHull.x[localMinX])
                    convexHull.MIN_X = localMinX;

                if(convexHull.y[convexHull.MAX_Y] < convexHull.y[localMaxY])
                    convexHull.MAX_Y = localMaxY;
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            finally
            {
                lock.unlock();
            }
        }

        private void findPoints(int p1, int p2)
        {
            IntList left = new IntList();
            IntList right = new IntList();

            int a = convexHull.y[p1] - convexHull.y[p2];
            int b = convexHull.x[p2] - convexHull.x[p1];
            int c = ((convexHull.y[p2] * convexHull.x[p1]) - (convexHull.y[p1] * convexHull.x[p2]));

            int topLeftIndex = -1;
            int topLeftDistance = 0;

            int botRightIndex = -1;
            int botRightDistance = 0;

            int point;
            int d;

            for(int i = start; i < end; i++)
            {
                point = convexHull.points.get(i);
                d = a * convexHull.x[point] + b * convexHull.y[point] + c;


                //left
                if(d > 0)
                {
                    left.add(convexHull.points.get(i));

                    if(d > topLeftDistance)
                    {
                        topLeftDistance = d;
                        topLeftIndex = i;
                    }
                }

                //right
                else if(d < 0)
                {
                    right.add(convexHull.points.get(i));

                    if(d < botRightDistance)
                    {
                        botRightDistance = d;
                        botRightIndex = i;
                    }
                }
            }

            holder[id][0] = left;
            holder[id][1] = right;

            lock.lock();

            try
            {
                if(topLeftDistance > leftGlobalDistance)
                {
                    leftGlobal = topLeftIndex;
                    leftGlobalDistance = topLeftDistance;
                }

                if(botRightDistance < rightGlobalDistance)
                {
                    rightGlobalDistance = botRightDistance;
                    rightGlobal = botRightIndex;
                }
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            finally
            {
                lock.unlock();
            }

            try
            {
                threadBarrier.await();
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            if(id == 0)
            {
                for(int i = 0; i < numThreads; i++)
                {
                    leftList.append(holder[i][0]);
                    rightList.append(holder[i][1]);
                }
            }
        }
    }

    private static class RecWorker implements Runnable
    {
        int id;
        int p1;
        int p2;
        IntList points;
        Node nodeInput;

        public RecWorker(int p1, int p2, IntList points, Node node)
        {
            this.id = threadIdCounter.getAndIncrement();
            this.p1 = p1;
            this.p2 = p2;
            this.points = points;
            this.nodeInput = node;
        }

        public void run()
        {
            findLeftPoints(p1, p2, points, nodeInput);

            threadsAlive.getAndDecrement();
            if(threadsAlive.get() == 0)
            {
                try
                {
                    recBarrier.await();
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        private void findLeftPoints(int p1, int p2, IntList points, Node node)
        {
            IntList left = new IntList();
            int a = convexHull.y[p1] - convexHull.y[p2];
            int b = convexHull.x[p2] - convexHull.x[p1];
            int c = ((convexHull.y[p2] * convexHull.x[p1]) - (convexHull.y[p1] * convexHull.x[p2]));

            int maxDistance = 0;
            int maxPoint = -1;
            int d;
            int point;

            for(int i = 0; i < points.size(); i++)
            {
                point = points.get(i);
                d = a * convexHull.x[point] + b * convexHull.y[point] + c;

                if(d > 0)
                {
                    left.add(point);
                    if(d > maxDistance)
                    {
                        maxDistance = d;
                        maxPoint = point;
                    }
                }
            }


            if(maxPoint >= 0)
            {
                node.point = maxPoint;
                node.left = new Node();
                node.right = new Node();



                if(numThreads > threadsAlive.getAndIncrement())
                {
                    findLeftPoints(p1, maxPoint, left, node.left);
                    new Thread(new RecWorker(p1, maxPoint, left, node.left)).start();
                }

                else
                {
                    threadsAlive.getAndDecrement();
                    findLeftPoints(p1, maxPoint, left, node.left);
                }

                findLeftPoints(maxPoint, p2, left, node.right);
            }
        }
    }

    private static class TreeEnvelopeWorker implements Runnable
    {
        int id;

        public TreeEnvelopeWorker(int id)
        {
            this.id = id;
        }

        public void run()
        {
            if(id == 0)
                globalLeftEnvelope = recursionTreeLeft.makeEnvelope();
            else if(id == 1)
                getGlobalRightEnvelope = getRecursionTreeRight.makeEnvelope();

            try
            {
                treeWorkerBarrier.await();
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}


