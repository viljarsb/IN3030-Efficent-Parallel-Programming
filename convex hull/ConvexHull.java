

public class ConvexHull {
    int n;
    int[] x;
    int[] y;
    int MAX_X;
    int MIN_X;
    int MAX_Y;
    IntList points;

    public ConvexHull(int n, int seed)
    {
       this.n = n;
       this.x = new int[n];
       this.y = new int[n];

       NPunkter17 pointGenerator = new NPunkter17(n, seed);
       pointGenerator.fyllArrayer(x, y);
       points = pointGenerator.lagIntList();
    }
}

