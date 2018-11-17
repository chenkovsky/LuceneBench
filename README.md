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
[TREC 2009 Million Query Track](https://trec.nist.gov/data/million.query09.html)<br>
The test queries are stored in a plain text file (UTF-8).
<br><br>
The test data and the search index are stored on different disks in order to utilize the full disk speed for indexing and searching, uncompromized by reading the test data.

## Benchmark results

|                           | [Lucene](http://lucene.apache.org/core/) v7.5   | [SeekStorm](https://seekstorm.com/) v0.1   |
| ------------------------- | ------------- |
| Search Throughput (QPS)   | Content Cell  |Content Cell  |
| Indexing Speed (docs/day) | Content Cell  |Content Cell  |
| Indexing Speed (GB/hour)  | Content Cell  |Content Cell  |
| Index Size (GB)           | Content Cell  |Content Cell  |

### Hardware
Intel Core i7-8750H<br>
32 GB RAM<br>
Samsung 970 EVO SSD, 1TB<br>
