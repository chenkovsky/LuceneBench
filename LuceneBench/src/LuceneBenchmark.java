import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

//ImportWikipedia
public class LuceneBenchmark 
{
    public static Boolean newindex=true;
    public static int querySize = 2000;
    public static int userNumber = 5;

    public static long linecount = 0;
    public static long indexedDocCount=0;
    public static long filesize = 0;

    public static List<String> queries = new ArrayList<String>();

    public static LuceneCore lucene = new LuceneCore();

    public static Semaphore indexSemaphore;
    public static Semaphore searchSemaphore;

    public static  AtomicLong resultCountSum =new AtomicLong();
    public static  AtomicLong sumSearchTime =new AtomicLong();

    public static void LoadQueries(String filename)
    {
        //https://trec.nist.gov/data/million.query09.html
        //https://raw.githubusercontent.com/shaun-on-gh/CS5246Test/master/Other_Projects/test_files/09.mq.topics.20001-60000.txt

        Path path = Paths.get(filename);
        try (Stream<String> lines = Files.lines(path)) 
        {
            lines.forEach(query -> 
            {                
                //System.out.println(queries.size()+" string: "+query);
                if (query != "")
                {
                  
                    String[] parts = query.split(":");
                    queries.add(parts[2]);
                    //System.out.println("query: "+parts[2]);              
                }                     
            });
        } catch (IOException ex) {
          // do something or re-throw...
        }

        System.out.println("Queries loaded: " + queries.size());       
    }

    public static void main(String[] args)  throws InterruptedException, RejectedExecutionException 
    {
        System.out.println("Indexing started ...");

        long start=0L;
        long millis=0L;
        
        // Open the directory 
        lucene.openIndex("D:/data/luceneindex", newindex);
        //-------
        
        if (newindex)
        {
            String pathString="C:/data/wikipedia/enwiki-latest-pages-articles.txt";
            Path path = Paths.get(pathString);
            File f = new File(pathString);
            filesize=f.length();

            start=System.currentTimeMillis();

            var doc = new DocObject();

            //########### start threading
            //see https://home.apache.org/~mikemccand/lucenebench/indexing.html
            Integer maxThreads=16;//Runtime.getRuntime().availableProcessors(); 
            ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
            indexSemaphore = new Semaphore(maxThreads*4);
            
            try (Stream<String> lines = Files.lines(path)) 
            {
                lines.forEach(line -> 
                {              
                    switch ((int)(linecount % 5))
                    {
                        case 0:
                            doc.url = line;
                            break;
                        case 1:
                            doc.domain = line;
                            break;
                        case 2:
                            doc.date = Long.parseLong(line);
                            break;
                        case 3:
                            doc.title = line;
                            break;
                        case 4:
                            doc.content = line;
                            //Program.index.IndexDocQueue(doc, true); 
                            //-----------
                            // Add documents
                            indexedDocCount++;
                            //indexedDocCount.incrementAndGet(); AtomicLong
                            //lucene.addDoc(doc);
                            try{
                                indexSemaphore.acquire();
                            }catch(Exception e){}
                            executor.submit(new AddDocThread(doc));
                            //-------------
                            break;
                        default:
                            break;
                    }

                    linecount++;
                    if ((linecount % 500000) == 0)
                    {
                        System.out.println(String.format("%,d",linecount / 5)+"  permits: "+indexSemaphore.availablePermits());
                        //if ((linecount / 5) >= 100000) break;
                    }
                });
            } catch (IOException ex) {
                System.out.println("Exception at line "+String.format("%,d",linecount / 5)+"  "+ex.getMessage());
            }

            //###### end threading
  
            executor.shutdown();
            //executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
              } catch (InterruptedException e) {  }

            //---------
            // Close the index
            lucene.closeIndex();
            //---------

            millis=System.currentTimeMillis()-start;

            System.out.println("Wikipedia indexing finished:   docs: " + String.format("%,d",indexedDocCount)+" ("+String.format("%,d",linecount / 5) +")" + " docs/day: " + 
            String.format("%,d",indexedDocCount * 1000 * 3600 * 24 / millis) + " (" + String.format("%,d",linecount / 5 * 1000 * 3600 * 24 / millis) + 
            ")" + " GB/hour: " + (filesize * 1000 * 3600 / millis / 1024 / 1024 / 1024)+"  minutes: "+ 
            (millis/(long)60000));

            System.out.println("availablePermits: "+indexSemaphore.availablePermits());
        }
        //--------------------------

        LoadQueries("C:/data/09.mq.topics.20001-60000.txt");

        start=System.currentTimeMillis();

        // Search
        int maxSearchThreads=userNumber;
        ExecutorService searchExecutor = Executors.newFixedThreadPool(maxSearchThreads);
        searchSemaphore = new Semaphore(maxSearchThreads*4);

        int queryCount=0;
        for (String s : queries) 
        {
            //lucene.search(s);  
            try{
                searchSemaphore.acquire();
            }catch(Exception e){}
            searchExecutor.submit(new SearchThread(s));

            queryCount++;
            if (queryCount>=querySize) break;
        }

        searchExecutor.shutdown();
        //executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        try {
            searchExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
          } catch (InterruptedException e) {  }


        millis=System.currentTimeMillis()-start;

        //results, time, qps
        System.out.println("QueryCount: " + queryCount+"  userNumber: "+userNumber+"  Throughput: " + String.format("%,.2f",((double)queryCount * (double)1000 / (double)millis)) + " query/sec (QPS)"+"  resultCount: " + String.format("%,d",resultCountSum.get())+"  latency: "+String.format("%,d",LuceneBenchmark.sumSearchTime.get()/queryCount));
    }

}