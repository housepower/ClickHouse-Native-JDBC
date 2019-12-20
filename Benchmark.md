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
InsertIBenchmark.benchInsertHttp                         200000           20     10000  avgt    2   329.161          ms/op
InsertIBenchmark.benchInsertHttp                         200000           50     10000  avgt    2   357.735          ms/op
InsertIBenchmark.benchInsertHttp                         200000          100     10000  avgt    2   337.018          ms/op
InsertIBenchmark.benchInsertHttp                         500000           20     10000  avgt    2   870.603          ms/op
InsertIBenchmark.benchInsertHttp                         500000           50     10000  avgt    2   871.808          ms/op
InsertIBenchmark.benchInsertHttp                         500000          100     10000  avgt    2   874.484          ms/op

InsertIBenchmark.benchInsertNative                       200000           20     10000  avgt    2    85.176          ms/op
InsertIBenchmark.benchInsertNative                       200000           50     10000  avgt    2    84.685          ms/op
InsertIBenchmark.benchInsertNative                       200000          100     10000  avgt    2    81.595          ms/op
InsertIBenchmark.benchInsertNative                       500000           20     10000  avgt    2   189.354          ms/op
InsertIBenchmark.benchInsertNative                       500000           50     10000  avgt    2   188.860          ms/op
InsertIBenchmark.benchInsertNative                       500000          100     10000  avgt    2   187.894          ms/op

SelectIBenchmark.benchSelectHTTP                         200000           20     10000  avgt    2    29.101          ms/op
SelectIBenchmark.benchSelectHTTP                         200000           50     10000  avgt    2    28.962          ms/op
SelectIBenchmark.benchSelectHTTP                         200000          100     10000  avgt    2    29.342          ms/op
SelectIBenchmark.benchSelectHTTP                         500000           20     10000  avgt    2    29.137          ms/op
SelectIBenchmark.benchSelectHTTP                         500000           50     10000  avgt    2    29.201          ms/op
SelectIBenchmark.benchSelectHTTP                         500000          100     10000  avgt    2    29.663          ms/op
SelectIBenchmark.benchSelectNative                       200000           20     10000  avgt    2    10.298          ms/op
SelectIBenchmark.benchSelectNative                       200000           50     10000  avgt    2    10.618          ms/op
SelectIBenchmark.benchSelectNative                       200000          100     10000  avgt    2    10.275          ms/op
SelectIBenchmark.benchSelectNative                       500000           20     10000  avgt    2    10.451          ms/op
SelectIBenchmark.benchSelectNative                       500000           50     10000  avgt    2    10.525          ms/op
SelectIBenchmark.benchSelectNative                       500000          100     10000  avgt    2    10.388          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         200000           20     10000  avgt    2   740.573          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         200000           50     10000  avgt    2  1877.884          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         200000          100     10000  avgt    2  3796.538          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         500000           20     10000  avgt    2  1915.588          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         500000           50     10000  avgt    2  4616.353          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertHttp         500000          100     10000  avgt    2  9271.866          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       200000           20     10000  avgt    2   333.039          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       200000           50     10000  avgt    2   970.073          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       200000          100     10000  avgt    2  1994.276          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       500000           20     10000  avgt    2   820.008          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       500000           50     10000  avgt    2  2304.791          ms/op
WideColumnDoubleInsertIBenchmark.benchInsertNative       500000          100     10000  avgt    2  4963.705          ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            200000           20     10000  avgt    2   379.542          ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            200000           50     10000  avgt    2   927.107          ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            200000          100     10000  avgt    2  1811.646          ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            500000           20     10000  avgt    2   977.498          ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            500000           50     10000  avgt    2  2231.106          ms/op
WideColumnIntInsertIBenchmark.benchInsertHttp            500000          100     10000  avgt    2  4447.637          ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          200000           20     10000  avgt    2   209.462          ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          200000           50     10000  avgt    2   542.700          ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          200000          100     10000  avgt    2  1056.893          ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          500000           20     10000  avgt    2   542.191          ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          500000           50     10000  avgt    2  1335.009          ms/op
WideColumnIntInsertIBenchmark.benchInsertNative          500000          100     10000  avgt    2  2859.364          ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         200000           20     10000  avgt    2   474.204          ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         200000           50     10000  avgt    2  1144.567          ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         200000          100     10000  avgt    2  2262.609          ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         500000           20     10000  avgt    2  1200.224          ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         500000           50     10000  avgt    2  2838.900          ms/op
WideColumnStringInsertIBenchmark.benchInsertHttp         500000          100     10000  avgt    2  5713.537          ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       200000           20     10000  avgt    2   415.578          ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       200000           50     10000  avgt    2  1121.937          ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       200000          100     10000  avgt    2  2377.680          ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       500000           20     10000  avgt    2  1025.925          ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       500000           50     10000  avgt    2  2846.580          ms/op
WideColumnStringInsertIBenchmark.benchInsertNative       500000          100     10000  avgt    2  5861.501          ms/op

RowBinaryDoubleIBenchmark.benchInsertHttpRowBinary       200000           20     10000  avgt    2   215.373          ms/op
RowBinaryDoubleIBenchmark.benchInsertHttpRowBinary       200000           50     10000  avgt    2   542.209          ms/op
RowBinaryDoubleIBenchmark.benchInsertHttpRowBinary       200000          100     10000  avgt    2  1089.292          ms/op
RowBinaryDoubleIBenchmark.benchInsertHttpRowBinary       500000           20     10000  avgt    2   510.823          ms/op
RowBinaryDoubleIBenchmark.benchInsertHttpRowBinary       500000           50     10000  avgt    2  1300.616          ms/op
RowBinaryDoubleIBenchmark.benchInsertHttpRowBinary       500000          100     10000  avgt    2  2799.970          ms/op
RowBinaryDoubleIBenchmark.benchInsertNative              200000           20     10000  avgt    2   337.765          ms/op
RowBinaryDoubleIBenchmark.benchInsertNative              200000           50     10000  avgt    2   942.797          ms/op
RowBinaryDoubleIBenchmark.benchInsertNative              200000          100     10000  avgt    2  2049.243          ms/op
RowBinaryDoubleIBenchmark.benchInsertNative              500000           20     10000  avgt    2   808.243          ms/op
RowBinaryDoubleIBenchmark.benchInsertNative              500000           50     10000  avgt    2  2268.530          ms/op
RowBinaryDoubleIBenchmark.benchInsertNative              500000          100     10000  avgt    2  4878.871          ms/op
RowBinaryIntIBenchmark.benchInsertHttpRowBinary          200000           20     10000  avgt    2   214.645          ms/op
RowBinaryIntIBenchmark.benchInsertHttpRowBinary          200000           50     10000  avgt    2   529.331          ms/op
RowBinaryIntIBenchmark.benchInsertHttpRowBinary          200000          100     10000  avgt    2  1080.930          ms/op
RowBinaryIntIBenchmark.benchInsertHttpRowBinary          500000           20     10000  avgt    2   506.736          ms/op
RowBinaryIntIBenchmark.benchInsertHttpRowBinary          500000           50     10000  avgt    2  1344.688          ms/op
RowBinaryIntIBenchmark.benchInsertHttpRowBinary          500000          100     10000  avgt    2  2682.044          ms/op
RowBinaryIntIBenchmark.benchInsertNative                 200000           20     10000  avgt    2   212.411          ms/op
RowBinaryIntIBenchmark.benchInsertNative                 200000           50     10000  avgt    2   542.801          ms/op
RowBinaryIntIBenchmark.benchInsertNative                 200000          100     10000  avgt    2  1036.934          ms/op
RowBinaryIntIBenchmark.benchInsertNative                 500000           20     10000  avgt    2   523.403          ms/op
RowBinaryIntIBenchmark.benchInsertNative                 500000           50     10000  avgt    2  1407.279          ms/op
RowBinaryIntIBenchmark.benchInsertNative                 500000          100     10000  avgt    2  2758.049          ms/op
RowBinaryStringIBenchmark.benchInsertHttpRowBinary       200000           20     10000  avgt    2   462.631          ms/op
RowBinaryStringIBenchmark.benchInsertHttpRowBinary       200000           50     10000  avgt    2  1171.256          ms/op
RowBinaryStringIBenchmark.benchInsertHttpRowBinary       200000          100     10000  avgt    2  2334.050          ms/op
RowBinaryStringIBenchmark.benchInsertHttpRowBinary       500000           20     10000  avgt    2  1135.562          ms/op
RowBinaryStringIBenchmark.benchInsertHttpRowBinary       500000           50     10000  avgt    2  2950.124          ms/op
RowBinaryStringIBenchmark.benchInsertHttpRowBinary       500000          100     10000  avgt    2  5844.061          ms/op
RowBinaryStringIBenchmark.benchInsertNative              200000           20     10000  avgt    2   422.110          ms/op
RowBinaryStringIBenchmark.benchInsertNative              200000           50     10000  avgt    2  1121.684          ms/op
RowBinaryStringIBenchmark.benchInsertNative              200000          100     10000  avgt    2  2318.440          ms/op
RowBinaryStringIBenchmark.benchInsertNative              500000           20     10000  avgt    2  1020.892          ms/op
RowBinaryStringIBenchmark.benchInsertNative              500000           50     10000  avgt    2  2789.095          ms/op
RowBinaryStringIBenchmark.benchInsertNative              500000          100     10000  avgt    2  6399.122          ms/op
```