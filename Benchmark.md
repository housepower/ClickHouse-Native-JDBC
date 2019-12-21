## benchmarks

Benchmarks are run by the `travis-ci` docker integration-test, eg [here](https://travis-ci.com/housepower/ClickHouse-Native-JDBC/builds/141995408#L5280).
- All benchmark codes could be seen in the [test code directory](https://github.com/housepower/Clickhouse-Native-JDBC/tree/master/src/test/java/com/github/housepower/jdbc/benchmark), you can do the bench on your machine too.
- Both  `Clickhouse-Native-JDBC` and `clickhouse-jdbc` are good jdbc clients. And `clickhouse-jdbc` are maintained better, 
    this benchmarks only show your some difference, users could choose freely according to their needs.
- The result's score means the avg_time to process one operation, lower is better.
- With string type of JDBC batch insertion, `Clickhouse-Native-JDBC` and `clickhouse-jdbc` are almost the similar performance.
- With other types of JDBC batch insertion or selection, `Clickhouse-Native-JDBC` may be 1~4 times better than `clickhouse-jdbc`.
- `clickhouse-jdbc` RowBinary insert performs similar performance vs `Clickhouse-Native-JDBC`, 
    it's not jdbc standared yet it's pretty efficient for the client because it sends data by row stream rather than one big block,
    but it's not columnar based and may cause the clickhouse-server CPU higher load (On my machine, this's maybe 3 times overload by loop benchmark).
- Here are the benchmark Params:
    - `batchSize`: insert batch size.
    - `columnNum`: insert column number, used in the WideColumn/RowBinary benchmarks.
    - `selectNumber`: select row size.
    
```
 InsertIBenchmark.benchInsertHttp                         200000           20             N/A  avgt    2    322.867          ms/op
 InsertIBenchmark.benchInsertHttp                         200000           50             N/A  avgt    2    323.162          ms/op
 InsertIBenchmark.benchInsertHttp                         500000           20             N/A  avgt    2    827.496          ms/op
 InsertIBenchmark.benchInsertHttp                         500000           50             N/A  avgt    2    836.865          ms/op
 InsertIBenchmark.benchInsertNative                       200000           20             N/A  avgt    2     72.309          ms/op
 InsertIBenchmark.benchInsertNative                       200000           50             N/A  avgt    2     72.948          ms/op
 InsertIBenchmark.benchInsertNative                       500000           20             N/A  avgt    2    168.628          ms/op
 InsertIBenchmark.benchInsertNative                       500000           50             N/A  avgt    2    165.061          ms/op
 
 
 SelectIBenchmark.benchSelectHTTP                            N/A          N/A          500000  avgt    2   1062.264          ms/op
 SelectIBenchmark.benchSelectHTTP                            N/A          N/A        10000000  avgt    2  21549.043          ms/op
 SelectIBenchmark.benchSelectNative                          N/A          N/A          500000  avgt    2    307.442          ms/op
 SelectIBenchmark.benchSelectNative                          N/A          N/A        10000000  avgt    2   5794.785          ms/op
 
 WideColumnDoubleInsertIBenchmark.benchInsertHttp         200000           20             N/A  avgt    2    717.190          ms/op
 WideColumnDoubleInsertIBenchmark.benchInsertHttp         200000           50             N/A  avgt    2   1818.837          ms/op
 WideColumnDoubleInsertIBenchmark.benchInsertHttp         500000           20             N/A  avgt    2   1829.937          ms/op
 WideColumnDoubleInsertIBenchmark.benchInsertHttp         500000           50             N/A  avgt    2   4469.529          ms/op
 WideColumnDoubleInsertIBenchmark.benchInsertNative       200000           20             N/A  avgt    2    339.103          ms/op
 WideColumnDoubleInsertIBenchmark.benchInsertNative       200000           50             N/A  avgt    2    876.465          ms/op
 WideColumnDoubleInsertIBenchmark.benchInsertNative       500000           20             N/A  avgt    2    784.621          ms/op
 WideColumnDoubleInsertIBenchmark.benchInsertNative       500000           50             N/A  avgt    2   2200.515          ms/op
 WideColumnIntInsertIBenchmark.benchInsertHttp            200000           20             N/A  avgt    2    366.501          ms/op
 WideColumnIntInsertIBenchmark.benchInsertHttp            200000           50             N/A  avgt    2    874.524          ms/op
 WideColumnIntInsertIBenchmark.benchInsertHttp            500000           20             N/A  avgt    2    930.941          ms/op
 WideColumnIntInsertIBenchmark.benchInsertHttp            500000           50             N/A  avgt    2   2174.565          ms/op
 WideColumnIntInsertIBenchmark.benchInsertNative          200000           20             N/A  avgt    2    196.612          ms/op
 WideColumnIntInsertIBenchmark.benchInsertNative          200000           50             N/A  avgt    2    500.912          ms/op
 WideColumnIntInsertIBenchmark.benchInsertNative          500000           20             N/A  avgt    2    511.506          ms/op
 WideColumnIntInsertIBenchmark.benchInsertNative          500000           50             N/A  avgt    2   1266.273          ms/op
 WideColumnStringInsertIBenchmark.benchInsertHttp         200000           20             N/A  avgt    2    454.479          ms/op
 WideColumnStringInsertIBenchmark.benchInsertHttp         200000           50             N/A  avgt    2   1063.671          ms/op
 WideColumnStringInsertIBenchmark.benchInsertHttp         500000           20             N/A  avgt    2   1143.546          ms/op
 WideColumnStringInsertIBenchmark.benchInsertHttp         500000           50             N/A  avgt    2   2671.942          ms/op
 WideColumnStringInsertIBenchmark.benchInsertNative       200000           20             N/A  avgt    2    415.649          ms/op
 WideColumnStringInsertIBenchmark.benchInsertNative       200000           50             N/A  avgt    2   1117.887          ms/op
 WideColumnStringInsertIBenchmark.benchInsertNative       500000           20             N/A  avgt    2   1040.238          ms/op
 WideColumnStringInsertIBenchmark.benchInsertNative       500000           50             N/A  avgt    2   2805.116          ms/op
 
 RowBinaryDoubleIBenchmark.benchInsertHttpRowBinary       200000           20             N/A  avgt    2    210.101          ms/op
 RowBinaryDoubleIBenchmark.benchInsertHttpRowBinary       200000           50             N/A  avgt    2    512.881          ms/op
 RowBinaryDoubleIBenchmark.benchInsertHttpRowBinary       500000           20             N/A  avgt    2    494.944          ms/op
 RowBinaryDoubleIBenchmark.benchInsertHttpRowBinary       500000           50             N/A  avgt    2   1217.977          ms/op
 RowBinaryDoubleIBenchmark.benchInsertNative              200000           20             N/A  avgt    2    317.598          ms/op
 RowBinaryDoubleIBenchmark.benchInsertNative              200000           50             N/A  avgt    2    882.392          ms/op
 RowBinaryDoubleIBenchmark.benchInsertNative              500000           20             N/A  avgt    2    786.638          ms/op
 RowBinaryDoubleIBenchmark.benchInsertNative              500000           50             N/A  avgt    2   2178.728          ms/op
 RowBinaryIntIBenchmark.benchInsertHttpRowBinary          200000           20             N/A  avgt    2    214.144          ms/op
 RowBinaryIntIBenchmark.benchInsertHttpRowBinary          200000           50             N/A  avgt    2    520.860          ms/op
 RowBinaryIntIBenchmark.benchInsertHttpRowBinary          500000           20             N/A  avgt    2    497.062          ms/op
 RowBinaryIntIBenchmark.benchInsertHttpRowBinary          500000           50             N/A  avgt    2   1318.273          ms/op
 RowBinaryIntIBenchmark.benchInsertNative                 200000           20             N/A  avgt    2    204.526          ms/op
 RowBinaryIntIBenchmark.benchInsertNative                 200000           50             N/A  avgt    2    482.022          ms/op
 RowBinaryIntIBenchmark.benchInsertNative                 500000           20             N/A  avgt    2    519.482          ms/op
 RowBinaryIntIBenchmark.benchInsertNative                 500000           50             N/A  avgt    2   1323.276          ms/op
 RowBinaryStringIBenchmark.benchInsertHttpRowBinary       200000           20             N/A  avgt    2    446.506          ms/op
 RowBinaryStringIBenchmark.benchInsertHttpRowBinary       200000           50             N/A  avgt    2   1089.095          ms/op
 RowBinaryStringIBenchmark.benchInsertHttpRowBinary       500000           20             N/A  avgt    2   1076.479          ms/op
 RowBinaryStringIBenchmark.benchInsertHttpRowBinary       500000           50             N/A  avgt    2   2845.602          ms/op
 RowBinaryStringIBenchmark.benchInsertNative              200000           20             N/A  avgt    2    418.727          ms/op
 RowBinaryStringIBenchmark.benchInsertNative              200000           50             N/A  avgt    2   1079.312          ms/op
 RowBinaryStringIBenchmark.benchInsertNative              500000           20             N/A  avgt    2   1016.174          ms/op
 RowBinaryStringIBenchmark.benchInsertNative              500000           50             N/A  avgt    2   2785.120          ms/op
```