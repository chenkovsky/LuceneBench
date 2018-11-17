LuceneBench<br>
========
[Apache Lucene](http://lucene.apache.org/core/) is a high-performance text search engine library written in Java.<br>
The widely used search platforms  [Solr](http://lucene.apache.org/solr/) and [Elasticsearch](https://www.elastic.co/de/products/elasticsearch) are both based on Lucene.
<br><br>
Performance for Indexing and Search is often important, but reliable numbers are hard to obtain. 
While there are many benchmark results published, they all vary depending on the number of documents, size of document, the hardware (Processor, RAM, SSD type), operating system, the number of parallel users. 
<br><br>
The only way is to run a benchmark according to your specific requirements on your hardware.
<br><br>
# Key Performance Indicators
Lucene Bench measures following Key Performance Indicators (KPI):
<br>
* Indexing Throughput (million documents per day)
* Search Througput (queries per second - QPS)
* Query Latency (mean, median, maximum, percentiles)
* Concurrent Users
* Index Size
<br><br>
# Indexing Test data
[English Wikipedia dump](https://dumps.wikimedia.org/enwiki/latest/enwiki-latest-pages-articles.xml.bz2) (5,677,776 docs)<br>

# Query Test data
[TREC 2009 Million Query Track](https://trec.nist.gov/data/million.query09.html)<br>




