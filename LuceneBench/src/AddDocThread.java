public class AddDocThread implements Runnable 
{
    DocObject doc;

    public AddDocThread(DocObject doc) 
    {
        //this.doc = doc;
        this.doc=new DocObject();
        this.doc.content=doc.content;
        this.doc.title=doc.title;
        this.doc.domain=doc.domain;
        this.doc.url=doc.url;
        this.doc.date=doc.date;
    }

    @Override
    public void run() 
    {
        //Measure the end to end latency of my client code
        try
        {
            LuceneBenchmark.lucene.addDoc(doc);
        }
        finally
        {
            LuceneBenchmark.indexSemaphore.release();
        }
    }   
}