public class SearchThread implements Runnable 
{
    String queryString;

    public SearchThread(String queryString) 
    {
        this.queryString=queryString;
    }

    @Override
    public void run() 
    {
        //Measure the end to end latency of my client code
        try
        {
            long start=System.currentTimeMillis();
            long count=LuceneBenchmark.lucene.search(queryString);  
            long millis=System.currentTimeMillis()-start;
            LuceneBenchmark.resultCountSum.addAndGet(count);
            LuceneBenchmark.sumSearchTime.addAndGet(millis);
        }
        finally
        {
            LuceneBenchmark.searchSemaphore.release();
        }
    }   
}