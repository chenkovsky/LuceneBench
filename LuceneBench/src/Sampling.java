import java.util.Random;
import java.util.List;
import java.util.ArrayList;
//import java.util.Arrays;

public class Sampling
        {
            int count = 0;
            public int size = 10000;
            public Random rnd = new Random(400);
            public List<Long> reservoir = new ArrayList<Long>();

            public long median = 0;
            public long percentile50 = 0;
            public long percentile75 = 0;
            public long percentile90 = 0;
            public long percentile95 = 0;
            public long percentile98 = 0;
            public long percentile99 = 0;
            public long percentile999 = 0;

            public Sampling(int size)
            {
                this.size = size;
            }

            public void Add(long x)
            {
                if (count == Integer.MAX_VALUE) return;

                if (count < size)
                {
                    reservoir.add(x);
                }
                else
                {
                    int p = rnd.nextInt(count); // Choose a random number 0 >= p < n
                    if (p < size)
                    {
                        reservoir.set(p, x);
                    }
                }
                count++;

            }

            public void Calc()
            {
                int n = reservoir.size();
                if (n < 1) return;
                reservoir.sort(null);
                median = reservoir.get(n / 2);
                if (n % 2 == 0) median = (median + reservoir.get(n / 2 - 1)) / 2;
                percentile50 = reservoir.get((n * 50) / 100);
                percentile75 = reservoir.get((n * 75) / 100);
                percentile90 = reservoir.get((n * 90) / 100);
                percentile95 = reservoir.get((n * 95) / 100);
                percentile98 = reservoir.get((n * 98) / 100);
                percentile99 = reservoir.get((n * 99) / 100);
                percentile999 = reservoir.get((n * 999) / 1000);
            }

        }