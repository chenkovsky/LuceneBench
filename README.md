LuceneBench<br>
[![MIT License](https://img.shields.io/github/license/wolfgarbe/lucenebench.svg)](https://github.com/wolfgarbe/LuceneBench/blob/master/LICENSE)
========
[Lucene](http://lucene.apache.org/core/) is a high-performance search engine library written in Java, powering the search platforms  [Solr](http://lucene.apache.org/solr/) and [Elasticsearch](https://www.elastic.co/de/products/elasticsearch).
<br><br>
[SeekStorm](https://seekstorm.com) is a high-performance search platform written in C#, powering the SeekStorm Search as a Service.
<br><br>
Performance for Indexing and Search is of paramount importance, but reliable numbers are hard to obtain. 
While there are many benchmark results published, they all vary depending on the number of documents, size of document, the hardware (Processor, RAM, SSD type), operating system, the number of parallel users. 
<br><br>
The only way to objectively compare technologies is to run a benchmark according to your requirements on your hardware.
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
Five consecutive lines consitute a single document: title, content, domain, url, date.

## Query Test data
[TREC 2009 Million Query Track](https://trec.nist.gov/data/million.query09.html) (40,000 queries)<br>
The test queries are stored in a plain text file (UTF-8).
<br><br>
Test data and search index are stored on different disks in order to utilize the full disk speed for indexing and searching, uncompromized by reading the test data.

## Benchmark results

|                           | [Lucene](http://lucene.apache.org/core/) v7.5   | [SeekStorm](https://seekstorm.com/) v0.1   |
| :--- | ---: | ---: |    
| **Search Throughput** (QPS)   | 62  | 553  |
| **Search Latency** (ms)   | 80  | 12  |
| mean |  80 | 13  |
| median |  70 | 13  |
| 50th percentile | 71  | 13  |
| 75th percentile | 91  | 19  |
| 90th percentile | 120  | 24  |
| 95th percentile | 142  | 27  |
| 98th percentile | 169  | 32  |
| 99th percentile | 195  | 35  |
| 99.9th percentile| 333  | 49  |
| max| 363  | 52  |
| **Indexing Speed** (million docs/day) | 1,042 | 473  |
| **Indexing Speed** (GB/hour)  | 135  | 61  |
| **Index Size** (GB)           | 16  | 18  |

### Benchmark conditions
All fields are stored.<br>
Full text search in all fields.<br>
Multithreaded queries: 5 Threads<br>
Multithreaded indexing: 16 Threads as [recommended](https://home.apache.org/~mikemccand/lucenebench/indexing.html)<br>
Lucene RAM buffer size: 2048 MB as [recommended](https://home.apache.org/~mikemccand/lucenebench/indexing.html)<br>
JRE parameters: -Xmx8g -Xms8g -server as [recommended](https://home.apache.org/~mikemccand/lucenebench/indexing.html)

### Hardware
Intel Core i7-8750H<br>
32 GB RAM<br>
Samsung 970 EVO SSD, 1TB<br>

### Software
Java JRE 10.0.2<br>
Lucene 7.5.2<br>
Microsoft Windows 10 Professional<br>
