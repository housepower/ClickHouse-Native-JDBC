## benchmarks

Benchmarks are run by the `travis-ci` docker integration-test.

- All benchmark codes could be seen in the [test code directory](https://github.com/housepower/ClickHouse-Native-JDBC/tree/master/src/test/java/com/github/housepower/jdbc/benchmark), you can do bench on your machine. 
- The score means the avg_time to process on opertaion, lower is better.
- With string type batch insertion, `Native-jdbc` and `http-jdbc` are with similar performance.
- With other types batch insertion, `Native-jdbc` may be 2~3 times better than `http-jdbc`. 
- Benchmark Params:
    - `batchSize`: insert batch
    - `columnNum`: insert columnNum, mostly used in WideColumn benchmark.
    - `number`: select size
    
```
Benchmark                                           (batchSize)  (columnNum)  (number)  Mode  Cnt     Score   Units
InsertIBenchmark.benchInsertHttp                          10000          N/A       N/A  avgt    3    23.964   ms/op
InsertIBenchmark.benchInsertHttp                         100000          N/A       N/A  avgt    3   149.191   ms/op
InsertIBenchmark.benchInsertHttp                        1000000          N/A       N/A  avgt    3  1513.667   ms/op
InsertIBenchmark.benchInsertNative                        10000          N/A       N/A  avgt    3    10.946   ms/op
InsertIBenchmark.benchInsertNative                       100000          N/A       N/A  avgt    3    49.009   ms/op
InsertIBenchmark.benchInsertNative                      1000000          N/A       N/A  avgt    3   408.214   ms/op
SelectIBenchmark.benchSelectHTTP                            N/A          N/A     10000  avgt    3    24.248   ms/op
SelectIBenchmark.benchSelectNative                          N/A          N/A     10000  avgt    3     8.694   ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         200000           20       N/A  avgt    3   695.163   ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         200000           50       N/A  avgt    3  1760.321   ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         200000          100       N/A  avgt    3  3547.326   ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         400000           20       N/A  avgt    3  1414.008   ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         400000           50       N/A  avgt    3  3490.360   ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         400000          100       N/A  avgt    3  7059.748   ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       200000           20       N/A  avgt    3   378.654   ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       200000           50       N/A  avgt    3  1017.302   ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       200000          100       N/A  avgt    3  2353.287   ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       400000           20       N/A  avgt    3   738.225   ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       400000           50       N/A  avgt    3  2130.605   ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       400000          100       N/A  avgt    3  4581.799   ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            200000           20       N/A  avgt    3   357.707   ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            200000           50       N/A  avgt    3   843.998   ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            200000          100       N/A  avgt    3  1674.101   ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            400000           20       N/A  avgt    3   726.875   ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            400000           50       N/A  avgt    3  1665.800   ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            400000          100       N/A  avgt    3  3343.526   ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          200000           20       N/A  avgt    3   216.731   ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          200000           50       N/A  avgt    3   653.968   ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          200000          100       N/A  avgt    3  1379.721   ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          400000           20       N/A  avgt    3   408.512   ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          400000           50       N/A  avgt    3  1246.928   ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          400000          100       N/A  avgt    3  2629.012   ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         200000           20       N/A  avgt    3   457.183   ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         200000           50       N/A  avgt    3  1063.601   ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         200000          100       N/A  avgt    3  2103.658   ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         400000           20       N/A  avgt    3   896.546   ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         400000           50       N/A  avgt    3  2145.316   ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         400000          100       N/A  avgt    3  4297.294   ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       200000           20       N/A  avgt    3   434.940   ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       200000           50       N/A  avgt    3  1145.448   ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       200000          100       N/A  avgt    3  2339.114   ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       400000           20       N/A  avgt    3   855.085   ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       400000           50       N/A  avgt    3  2287.780   ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       400000          100       N/A  avgt    3  4964.959   ms/op
```