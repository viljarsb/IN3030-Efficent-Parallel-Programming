
import java.util.ArrayList;
import java.util.List;

public class seqFactorizer {

    private long nSquare;
    private long result[][];

    public seqFactorizer(int n)
    {
        this.nSquare = (long)n*n;
        this.result = new long[100][];
    }

    public long[][] factorize()
    {
        int ctr = 1;

        while(ctr < 101)
        {
            long currentFactor = nSquare - ctr;
            long staticCurrent = nSquare - ctr;
            List<Long> factors  = new ArrayList<>();

            long i = 0, j;
            while (currentFactor % 2 == 0)
            {
                factors.add(new Long(2));
                currentFactor /= 2;
            }

            j = 3;
            while (j <= Math.sqrt(currentFactor) + 1)
            {
                if (currentFactor % j == 0)
                {
                    factors.add(j);
                    currentFactor /= j;
                }
                else { j += 2;}
            }
            if (currentFactor > 1) { factors.add(currentFactor);}

            result[ctr-1] = new long[factors.size()+1];
            result[ctr-1][0] = staticCurrent;

            for(int x = 1; x < result[ctr-1].length; x++)
            {
                long localFactor = factors.remove(0);
                result[ctr-1][x] = localFactor;
            }
            ctr++;

        }

        return result;
    }
}