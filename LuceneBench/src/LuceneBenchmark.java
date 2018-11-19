﻿import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

//ImportWikipedia
public class LuceneBenchmark 
{
    public static Boolean newindex=false;
    public static int querySize = 2000;

    public static long linecount = 0;
    public static long indexedDocCount=0;
    public static long filesize = 0;

    public static List<String> queries = new ArrayList<String>();

    public static LuceneCore lucene = new LuceneCore();

    public static Semaphore indexSemaphore;
    public static Semaphore searchSemaphore;

    public static  AtomicLong resultCountSum =new AtomicLong();
    //public static  AtomicLong sumSearchTime =new AtomicLong();
    public static  ReentrantLock lock = new ReentrantLock();

    public static Sampling sampleSearchTime;// = new Sampling(10000);

    public static void LoadQueries(String filename)
    {
        //https://trec.nist.gov/data/million.query09.html
        //https://raw.githubusercontent.com/shaun-on-gh/CS5246Test/master/Other_Projects/test_files/09.mq.topics.20001-60000.txt

        Path path = Paths.get(filename);
        try (Stream<String> lines = Files.lines(path)) 
        {
            lines.forEach(query -> 
            {                
                if (query != "")
                {
                  
                    String[] parts = query.split(":");
                    queries.add(parts[2]);          
                }                     
            });
        } catch (IOException ex) {}

        System.out.println("Queries loaded: " + queries.size());       
    }

    public static void main(String[] args)  throws InterruptedException, RejectedExecutionException 
    {
        System.out.println("Indexing started ...");

        long start=0L;
        long millis=0L;
        
        // Open the directory 
        lucene.openIndex("D:/data/luceneindex", newindex);
        
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

                            // Add documents
                            indexedDocCount++;
                            try{
                                indexSemaphore.acquire();
                            }catch(Exception e){}
                            executor.submit(new AddDocThread(doc));
                            break;
                        default:
                            break;
                    }

                    linecount++;
                    if ((linecount % 500000) == 0)
                    {
                        System.out.println(String.format("%,d",linecount / 5)+"  permits: "+indexSemaphore.availablePermits());
                    }
                });
            } catch (IOException ex) {
                System.out.println("Exception at line "+String.format("%,d",linecount / 5)+"  "+ex.getMessage());
            }

            //###### end threading
  
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
              } catch (InterruptedException e) {  }

            // Close the index
            lucene.closeIndex();

            millis=System.currentTimeMillis()-start;

            System.out.println("Wikipedia indexing finished:   docs: " + String.format("%,d",indexedDocCount)+" ("+String.format("%,d",linecount / 5) +")" + " docs/day: " + 
            String.format("%,d",indexedDocCount * 1000 * 3600 * 24 / millis) + " (" + String.format("%,d",linecount / 5 * 1000 * 3600 * 24 / millis) + 
            ")" + " GB/hour: " + (filesize * 1000 * 3600 / millis / 1024 / 1024 / 1024)+"  minutes: "+ 
            (millis/(long)60000));

            System.out.println("availablePermits: "+indexSemaphore.availablePermits());
        }
        //--------------------------

        LoadQueries("C:/data/09.mq.topics.20001-60000.txt");

        

        // Search

        //warmup
        LuceneBenchmark.lucene.search("test");  

        for (int concurrentUsers=1;concurrentUsers<=10;concurrentUsers++) 
        {
            int maxSearchThreads=concurrentUsers;
            ExecutorService searchExecutor = Executors.newFixedThreadPool(maxSearchThreads);
            searchSemaphore = new Semaphore(maxSearchThreads*4);

            int queryCount=0;
            resultCountSum.set(0);
            sampleSearchTime = new Sampling(10000);
            start=System.currentTimeMillis();

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
            try {
                searchExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {  }


            millis=System.currentTimeMillis()-start;

            //results, time, qps
            System.out.println("QueryCount: " + queryCount+"  userNumber: "+concurrentUsers+"  Throughput: " + String.format("%,.2f",((double)queryCount * (double)1000 / (double)millis)) + " query/sec (QPS)"+"  resultCount: " + String.format("%,d",resultCountSum.get())+"  latency: "+String.format("%,d",sampleSearchTime.sum/queryCount));
            sampleSearchTime.Calc();
            System.out.println("");
            System.out.println("Search Latency");
            System.out.println("mean: " + String.format("%,d",sampleSearchTime.sum/ queryCount) + "ms");
            System.out.println("median: " + String.format("%,d",sampleSearchTime.median) + "ms");
            System.out.println("50th percentile: " + String.format("%,d",sampleSearchTime.percentile50));
            System.out.println("75th percentile: " + String.format("%,d",sampleSearchTime.percentile75));
            System.out.println("90th percentile: " + String.format("%,d",sampleSearchTime.percentile90));
            System.out.println("95th percentile: " + String.format("%,d",sampleSearchTime.percentile95));
            System.out.println("98th percentile: " + String.format("%,d",sampleSearchTime.percentile98));
            System.out.println("99th percentile: " + String.format("%,d",sampleSearchTime.percentile99));
            System.out.println("99.9th percentile: " + String.format("%,d",sampleSearchTime.percentile999));
            System.out.println("max: " + String.format("%,d",sampleSearchTime.maximum) + "ms");
            System.out.println("");
        }
    }

}