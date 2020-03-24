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
 
 Benchmark                                                       Score   (batchSize)  (columnNum) (selectNumber)
 InsertIBenchmark.benchInsertHttp                        322.867 ms/op     200000           20        N/A  
 InsertIBenchmark.benchInsertHttp                        323.162 ms/op     200000           50        N/A  
 InsertIBenchmark.benchInsertHttp                        827.496 ms/op     500000           20        N/A  
 InsertIBenchmark.benchInsertHttp                        836.865 ms/op     500000           50        N/A  
 InsertIBenchmark.benchInsertNative                       72.309 ms/op     200000           20        N/A  
 InsertIBenchmark.benchInsertNative                       72.948 ms/op     200000           50        N/A  
 InsertIBenchmark.benchInsertNative                      168.628 ms/op     500000           20        N/A  
 InsertIBenchmark.benchInsertNative                      165.061 ms/op     500000           50        N/A  
 SelectIBenchmark.benchSelectHTTP                       1062.264 ms/op        N/A          N/A     500000  
 SelectIBenchmark.benchSelectHTTP                      21549.043 ms/op        N/A          N/A   10000000  
 SelectIBenchmark.benchSelectNative                      307.442 ms/op        N/A          N/A     500000  
 SelectIBenchmark.benchSelectNative                     5794.785 ms/op        N/A          N/A   10000000  
 WideColumnDoubleInsertIBenchmark.benchInsertHttp        717.190 ms/op     200000           20        N/A  
 WideColumnDoubleInsertIBenchmark.benchInsertHttp       1818.837 ms/op     200000           50        N/A  
 WideColumnDoubleInsertIBenchmark.benchInsertHttp       1829.937 ms/op     500000           20        N/A  
 WideColumnDoubleInsertIBenchmark.benchInsertHttp       4469.529 ms/op     500000           50        N/A  
 WideColumnDoubleInsertIBenchmark.benchInsertNative      339.103 ms/op     200000           20        N/A  
 WideColumnDoubleInsertIBenchmark.benchInsertNative      876.465 ms/op     200000           50        N/A  
 WideColumnDoubleInsertIBenchmark.benchInsertNative      784.621 ms/op     500000           20        N/A  
 WideColumnDoubleInsertIBenchmark.benchInsertNative     2200.515 ms/op     500000           50        N/A  
 WideColumnIntInsertIBenchmark.benchInsertHttp           366.501 ms/op     200000           20        N/A  
 WideColumnIntInsertIBenchmark.benchInsertHttp           874.524 ms/op     200000           50        N/A  
 WideColumnIntInsertIBenchmark.benchInsertHttp           930.941 ms/op     500000           20        N/A  
 WideColumnIntInsertIBenchmark.benchInsertHttp          2174.565 ms/op     500000           50        N/A  
 WideColumnIntInsertIBenchmark.benchInsertNative         196.612 ms/op     200000           20        N/A  
 WideColumnIntInsertIBenchmark.benchInsertNative         500.912 ms/op     200000           50        N/A  
 WideColumnIntInsertIBenchmark.benchInsertNative         511.506 ms/op     500000           20        N/A  
 WideColumnIntInsertIBenchmark.benchInsertNative        1266.273 ms/op     500000           50        N/A  
 WideColumnStringInsertIBenchmark.benchInsertHttp        454.479 ms/op     200000           20        N/A  
 WideColumnStringInsertIBenchmark.benchInsertHttp       1063.671 ms/op     200000           50        N/A  
 WideColumnStringInsertIBenchmark.benchInsertHttp       1143.546 ms/op     500000           20        N/A  
 WideColumnStringInsertIBenchmark.benchInsertHttp       2671.942 ms/op     500000           50        N/A  
 WideColumnStringInsertIBenchmark.benchInsertNative      415.649 ms/op     200000           20        N/A  
 WideColumnStringInsertIBenchmark.benchInsertNative     1117.887 ms/op     200000           50        N/A  
 WideColumnStringInsertIBenchmark.benchInsertNative     1040.238 ms/op     500000           20        N/A  
 WideColumnStringInsertIBenchmark.benchInsertNative     2805.116 ms/op     500000           50        N/A  
 RowBinaryDoubleIBenchmark.benchInsertHttpRowBinary      210.101 ms/op     200000           20        N/A  
 RowBinaryDoubleIBenchmark.benchInsertHttpRowBinary      512.881 ms/op     200000           50        N/A  
 RowBinaryDoubleIBenchmark.benchInsertHttpRowBinary      494.944 ms/op     500000           20        N/A  
 RowBinaryDoubleIBenchmark.benchInsertHttpRowBinary     1217.977 ms/op     500000           50        N/A  
 RowBinaryDoubleIBenchmark.benchInsertNative             317.598 ms/op     200000           20        N/A  
 RowBinaryDoubleIBenchmark.benchInsertNative             882.392 ms/op     200000           50        N/A  
 RowBinaryDoubleIBenchmark.benchInsertNative             786.638 ms/op     500000           20        N/A  
 RowBinaryDoubleIBenchmark.benchInsertNative            2178.728 ms/op     500000           50        N/A  
 RowBinaryIntIBenchmark.benchInsertHttpRowBinary         214.144 ms/op     200000           20        N/A  
 RowBinaryIntIBenchmark.benchInsertHttpRowBinary         520.860 ms/op     200000           50        N/A  
 RowBinaryIntIBenchmark.benchInsertHttpRowBinary         497.062 ms/op     500000           20        N/A  
 RowBinaryIntIBenchmark.benchInsertHttpRowBinary        1318.273 ms/op     500000           50        N/A  
 RowBinaryIntIBenchmark.benchInsertNative                204.526 ms/op     200000           20        N/A  
 RowBinaryIntIBenchmark.benchInsertNative                482.022 ms/op     200000           50        N/A  
 RowBinaryIntIBenchmark.benchInsertNative                519.482 ms/op     500000           20        N/A  
 RowBinaryIntIBenchmark.benchInsertNative               1323.276 ms/op     500000           50        N/A  
 RowBinaryStringIBenchmark.benchInsertHttpRowBinary      446.506 ms/op     200000           20        N/A  
 RowBinaryStringIBenchmark.benchInsertHttpRowBinary     1089.095 ms/op     200000           50        N/A  
 RowBinaryStringIBenchmark.benchInsertHttpRowBinary     1076.479 ms/op     500000           20        N/A  
 RowBinaryStringIBenchmark.benchInsertHttpRowBinary     2845.602 ms/op     500000           50        N/A  
 RowBinaryStringIBenchmark.benchInsertNative             418.727 ms/op     200000           20        N/A  
 RowBinaryStringIBenchmark.benchInsertNative            1079.312 ms/op     200000           50        N/A  
 RowBinaryStringIBenchmark.benchInsertNative            1016.174 ms/op     500000           20        N/A  
 RowBinaryStringIBenchmark.benchInsertNative            2785.120 ms/op     500000           50        N/A  
```