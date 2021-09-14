

public class SeqConvex
{
    private static ConvexHull convexHull;

    public static IntList start(ConvexHull conv)
    {
        convexHull = conv;

        for(int i = 0; i < convexHull.points.size(); i++)
        {
            if(convexHull.x[i] > convexHull.x[convexHull.MAX_X])
                convexHull.MAX_X = i;
            else if(convexHull.x[i] < convexHull.x[convexHull.MIN_X])
                convexHull.MIN_X = i;

            if(convexHull.y[i] >  convexHull.y[convexHull.MAX_Y])
                convexHull.MAX_Y = i;
        }


        IntList convexEnvelope = new IntList();
        convexEnvelope.add(convexHull.MAX_X);
        findLeftPoints(convexHull.MIN_X, convexHull.MAX_X, convexHull.points, convexEnvelope);
        convexEnvelope.add(convexHull.MIN_X);
        findLeftPoints(convexHull.MAX_X, convexHull.MIN_X, convexHull.points, convexEnvelope);

        return convexEnvelope;
    }

    private static void findLeftPoints(int p1, int p2, IntList points, IntList convexEnvelope)
    {
        int a = convexHull.y[p1] - convexHull.y[p2];
        int b = convexHull.x[p2] - convexHull.x[p1];
        int c = ((convexHull.y[p2] * convexHull.x[p1]) - (convexHull.y[p1] * convexHull.x[p2]));

        IntList pointsToTheLeft = new IntList();
        int maxDistance = 0;
        int maxPoint = -1;

        for(int i = 0; i < points.size(); i++)
        {
            int point = points.get(i);
            int d = a * convexHull.x[point] + b * convexHull.y[point] + c;

            if(d > 0)
            {
                pointsToTheLeft.add(point);

                if(d > maxDistance)
                {
                    maxDistance = d;
                    maxPoint = point;
                }
            }
        }

        if (maxPoint >= 0) {
            findLeftPoints(maxPoint, p2, pointsToTheLeft, convexEnvelope);
            convexEnvelope.add(maxPoint);
            findLeftPoints(p1, maxPoint, pointsToTheLeft, convexEnvelope);
        }
    }
}
