LuceneBench<br>
========
[Apache Lucene](http://lucene.apache.org/core/) is a high-performance text search engine library written in Java.<br>
The widely used search platforms  [Solr](http://lucene.apache.org/solr/) and [Elasticsearch](https://www.elastic.co/de/products/elasticsearch) are both based on Lucene.
<br><br>
[SeekStorm](https://seekstorm.com) is a high-performance search platform written in C#, powering the SeekStorm Search as a Service.
<br><br>
Performance for Indexing and Search is often important, but reliable numbers are hard to obtain. 
While there are many benchmark results published, they all vary depending on the number of documents, size of document, the hardware (Processor, RAM, SSD type), operating system, the number of parallel users. 
<br><br>
The only way is to run a benchmark according to your specific requirements on your hardware.
<br><br>
## Key Performance Indicators
Lucene Bench measures the following Key Performance Indicators (KPI):
<br>
* Indexing Throughput (million documents per day)
* Search Througput (queries per second - QPS)
* Query Latency (mean, median, maximum, percentiles)
* Concurrent Users
* Index Size

## Indexing Test data
[English Wikipedia dump](https://dumps.wikimedia.org/enwiki/latest/enwiki-latest-pages-articles.xml.bz2) (5,677,776 docs)<br>
The Wikipedia dump original XML format has been exported to a plain text file (UTF-8).
Five consecutive lines comprise a single document: title, content, domain, url, date.

## Query Test data
[TREC 2009 Million Query Track](https://trec.nist.gov/data/million.query09.html) (40,000 queries)<br>
The test queries are stored in a plain text file (UTF-8).
<br><br>
Test data and search index are stored on different disks in order to utilize the full disk speed for indexing and searching, uncompromized by reading the test data.

## Benchmark results

|                           | [Lucene](http://lucene.apache.org/core/) v7.5   | [SeekStorm](https://seekstorm.com/) v0.1   |
| ------------------------- | ------------- | ------------- |    
| Search Throughput (QPS)   | 65  | 588  |
| Search Latency (ms)   | 79  | 12  |
| Indexing Speed (million docs/day) | 1,042 | 473  |
| Indexing Speed (GB/hour)  | 135  | 61  |
| Index Size (GB)           | 16  | 18  |

### Hardware
Intel Core i7-8750H<br>
32 GB RAM<br>
Samsung 970 EVO SSD, 1TB<br>
