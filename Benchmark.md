## benchmarks

Benchmarks are run by the `travis-ci` docker integration-test.
- All benchmark codes could be seen in the [test code directory](https://github.com/housepower/ClickHouse-Native-JDBC/tree/master/src/test/java/com/github/housepower/jdbc/benchmark), you can do the bench on your machine too. 
- The result score means the avg_time to process on opertaion, lower is better.
- With string type batch insertion, `Native-jdbc` and `http-jdbc` are almost the similar performance.
- With other types batch insertion or selection, `Native-jdbc` may be 1~4 times better than `http-jdbc`. 
- Benchmark Params:
    - `batchSize`: insert batch
    - `columnNum`: insert columnNum, mostly used in WideColumn benchmark.
    - `number`: select size
    
```
Benchmark                                           (batchSize)  (columnNum)  (number)  Mode  Cnt     Score   Error  Units
InsertIBenchmark.benchInsertHttp                         200000           20     10000  avgt    2   322.586          ms/op
InsertIBenchmark.benchInsertHttp                         200000           50     10000  avgt    2   345.363          ms/op
InsertIBenchmark.benchInsertHttp                         200000          100     10000  avgt    2   323.464          ms/op
InsertIBenchmark.benchInsertHttp                         500000           20     10000  avgt    2   807.962          ms/op
InsertIBenchmark.benchInsertHttp                         500000           50     10000  avgt    2   834.858          ms/op
InsertIBenchmark.benchInsertHttp                         500000          100     10000  avgt    2   829.479          ms/op
InsertIBenchmark.benchInsertNative                       200000           20     10000  avgt    2    92.025          ms/op
InsertIBenchmark.benchInsertNative                       200000           50     10000  avgt    2    91.780          ms/op
InsertIBenchmark.benchInsertNative                       200000          100     10000  avgt    2    90.758          ms/op
InsertIBenchmark.benchInsertNative                       500000           20     10000  avgt    2   216.306          ms/op
InsertIBenchmark.benchInsertNative                       500000           50     10000  avgt    2   212.870          ms/op
InsertIBenchmark.benchInsertNative                       500000          100     10000  avgt    2   213.917          ms/op
SelectIBenchmark.benchSelectHTTP                         200000           20     10000  avgt    2    27.395          ms/op
SelectIBenchmark.benchSelectHTTP                         200000           50     10000  avgt    2    27.124          ms/op
SelectIBenchmark.benchSelectHTTP                         200000          100     10000  avgt    2    26.868          ms/op
SelectIBenchmark.benchSelectHTTP                         500000           20     10000  avgt    2    27.253          ms/op
SelectIBenchmark.benchSelectHTTP                         500000           50     10000  avgt    2    27.864          ms/op
SelectIBenchmark.benchSelectHTTP                         500000          100     10000  avgt    2    26.858          ms/op
SelectIBenchmark.benchSelectNative                       200000           20     10000  avgt    2     8.720          ms/op
SelectIBenchmark.benchSelectNative                       200000           50     10000  avgt    2     8.712          ms/op
SelectIBenchmark.benchSelectNative                       200000          100     10000  avgt    2     9.366          ms/op
SelectIBenchmark.benchSelectNative                       500000           20     10000  avgt    2     9.037          ms/op
SelectIBenchmark.benchSelectNative                       500000           50     10000  avgt    2     9.237          ms/op
SelectIBenchmark.benchSelectNative                       500000          100     10000  avgt    2     8.958          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         200000           20     10000  avgt    2   728.169          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         200000           50     10000  avgt    2  1823.334          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         200000          100     10000  avgt    2  3673.929          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         500000           20     10000  avgt    2  1847.852          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         500000           50     10000  avgt    2  4534.485          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         500000          100     10000  avgt    2  8966.698          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       200000           20     10000  avgt    2   383.218          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       200000           50     10000  avgt    2  1049.005          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       200000          100     10000  avgt    2  2322.530          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       500000           20     10000  avgt    2  1020.537          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       500000           50     10000  avgt    2  2850.579          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       500000          100     10000  avgt    2  6204.861          ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            200000           20     10000  avgt    2   360.050          ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            200000           50     10000  avgt    2   874.894          ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            200000          100     10000  avgt    2  1729.700          ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            500000           20     10000  avgt    2   935.002          ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            500000           50     10000  avgt    2  2117.880          ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            500000          100     10000  avgt    2  4221.023          ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          200000           20     10000  avgt    2   220.140          ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          200000           50     10000  avgt    2   650.552          ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          200000          100     10000  avgt    2  1375.568          ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          500000           20     10000  avgt    2   530.236          ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          500000           50     10000  avgt    2  1537.990          ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          500000          100     10000  avgt    2  3185.190          ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         200000           20     10000  avgt    2   448.670          ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         200000           50     10000  avgt    2  1068.982          ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         200000          100     10000  avgt    2  2160.340          ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         500000           20     10000  avgt    2  1161.117          ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         500000           50     10000  avgt    2  2690.070          ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         500000          100     10000  avgt    2  5391.127          ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       200000           20     10000  avgt    2   438.517          ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       200000           50     10000  avgt    2  1170.543          ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       200000          100     10000  avgt    2  2398.526          ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       500000           20     10000  avgt    2  1086.710          ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       500000           50     10000  avgt    2  2851.833          ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       500000          100     10000  avgt    2  5995.123          ms/op
```