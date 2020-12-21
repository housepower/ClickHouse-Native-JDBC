/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.housepower.jdbc.settings;

import com.github.housepower.jdbc.serde.SettingSerde;

public class SettingKey {
    public static SettingKey min_compress_block_size = new SettingKey(SettingSerde.Int64, "The actual size of the block to compress, if the uncompressed data less than max_compress_block_size is no less than this value and no less than the volume of data for one mark.");
    public static SettingKey max_compress_block_size = new SettingKey(SettingSerde.Int64, "The maximum size of blocks of uncompressed data before compressing for writing to a table.");
    public static SettingKey max_block_size = new SettingKey(SettingSerde.Int64, "Maximum block size for reading");
    public static SettingKey max_insert_block_size = new SettingKey(SettingSerde.Int64, "The maximum block size for insertion, if we control the creation of blocks for insertion.");
    public static SettingKey min_insert_block_size_rows = new SettingKey(SettingSerde.Int64, "Squash blocks passed to INSERT query to specified size in rows, if blocks are not big enough.");
    public static SettingKey min_insert_block_size_bytes = new SettingKey(SettingSerde.Int64, "Squash blocks passed to INSERT query to specified size in bytes, if blocks are not big enough.");
    public static SettingKey max_read_buffer_size = new SettingKey(SettingSerde.Int64, "The maximum size of the buffer to read from the filesystem.");
    public static SettingKey max_distributed_connections = new SettingKey(SettingSerde.Int64, "The maximum number of connections for distributed processing of one query (should be greater than max_threads).");
    public static SettingKey max_query_size = new SettingKey(SettingSerde.Int64, "Which part of the query can be read into RAM for parsing (the remaining data for INSERT, if any, is read later)");
    public static SettingKey interactive_delay = new SettingKey(SettingSerde.Int64, "The interval in microseconds to check if the request is cancelled, and to send progress info.");
    public static SettingKey connect_timeout = new SettingKey(SettingSerde.Seconds, "Connection timeout if there are no replicas.");
    public static SettingKey connect_timeout_with_failover_ms = new SettingKey(SettingSerde.Milliseconds, "Connection timeout for selecting first healthy replica.");
    public static SettingKey queue_max_wait_ms = new SettingKey(SettingSerde.Milliseconds, "The wait time in the request queue, if the number of concurrent requests exceeds the maximum.");
    public static SettingKey poll_interval = new SettingKey(SettingSerde.Int64, "Block at the query wait loop on the server for the specified number of seconds.");
    public static SettingKey distributed_connections_pool_size = new SettingKey(SettingSerde.Int64, "Maximum number of connections with one remote server in the pool.");
    public static SettingKey connections_with_failover_max_tries = new SettingKey(SettingSerde.Int64, "The maximum number of attempts to connect to replicas.");
    public static SettingKey extremes = new SettingKey(SettingSerde.Bool, "Calculate minimums and maximums of the result columns. They can be output in JSON-formats.");
    public static SettingKey use_uncompressed_cache = new SettingKey(SettingSerde.Bool, "Whether to use the cache of uncompressed blocks.");
    public static SettingKey replace_running_query = new SettingKey(SettingSerde.Bool, "Whether the running request should be canceled with the same id as the new one.");
    public static SettingKey background_pool_size = new SettingKey(SettingSerde.Int64, "Number of threads performing background work for tables (for example, merging in merge tree). Only has meaning at server startup.");
    public static SettingKey background_schedule_pool_size = new SettingKey(SettingSerde.Int64, "Number of threads performing background tasks for replicated tables. Only has meaning at server startup.");
    public static SettingKey distributed_directory_monitor_sleep_time_ms = new SettingKey(SettingSerde.Milliseconds, "Sleep time for StorageDistributed DirectoryMonitors in case there is no work or exception has been thrown.");
    public static SettingKey distributed_directory_monitor_batch_inserts = new SettingKey(SettingSerde.Bool, "Should StorageDistributed DirectoryMonitors try to batch individual inserts into bigger ones.");
    public static SettingKey optimize_move_to_prewhere = new SettingKey(SettingSerde.Bool, "Allows disabling WHERE to PREWHERE optimization in SELECT queries from MergeTree.");
    public static SettingKey replication_alter_partitions_sync = new SettingKey(SettingSerde.Int64, "Wait for actions to manipulate the partitions. 0 - do not wait, 1 - wait for execution only of itself, 2 - wait for everyone.");
    public static SettingKey replication_alter_columns_timeout = new SettingKey(SettingSerde.Int64, "Wait for actions to change the table structure within the specified number of seconds. 0 - wait unlimited time.");
    public static SettingKey totals_auto_threshold = new SettingKey(SettingSerde.Float32, "The threshold for totals_mode = 'auto'.");
    public static SettingKey compile = new SettingKey(SettingSerde.Bool, "Whether query compilation is enabled.");
    public static SettingKey compile_expressions = new SettingKey(SettingSerde.Bool, "Compile some scalar functions and operators to native code.");
    public static SettingKey min_count_to_compile = new SettingKey(SettingSerde.Int64, "The number of structurally identical queries before they are compiled.");
    public static SettingKey group_by_two_level_threshold = new SettingKey(SettingSerde.Int64, "From what number of keys, a two-level aggregation starts. 0 - the threshold is not set.");
    public static SettingKey group_by_two_level_threshold_bytes = new SettingKey(SettingSerde.Int64, "From what size of the aggregation state in bytes, a two-level aggregation begins to be used. 0 - the threshold is not set. Two-level aggregation is used when at least one of the thresholds is triggered.");
    public static SettingKey distributed_aggregation_memory_efficient = new SettingKey(SettingSerde.Bool, "Is the memory-saving mode of distributed aggregation enabled.");
    public static SettingKey aggregation_memory_efficient_merge_threads = new SettingKey(SettingSerde.Int64, "Number of threads to use for merge intermediate aggregation results in memory efficient mode. When bigger, then more memory is consumed. 0 means - same as 'max_threads'.");
    public static SettingKey max_threads = new SettingKey(SettingSerde.Int64, "The maximum number of threads to execute the request. By default, it is determined automatically.");
    public static SettingKey max_parallel_replicas = new SettingKey(SettingSerde.Int64, "The maximum number of replicas of each shard used when the query is executed. For consistency (to get different parts of the same partition), this option only works for the specified sampling key. The lag of the replicas is not controlled.");
    public static SettingKey skip_unavailable_shards = new SettingKey(SettingSerde.Bool, "Silently skip unavailable shards.");
    public static SettingKey distributed_group_by_no_merge = new SettingKey(SettingSerde.Bool, "Do not merge aggregation states from different servers for distributed query processing - in case it is for certain that there are different keys on different shards.");
    public static SettingKey merge_tree_min_rows_for_concurrent_read = new SettingKey(SettingSerde.Int64, "If at least as many lines are read from one file, the reading can be parallelized.");
    public static SettingKey merge_tree_min_rows_for_seek = new SettingKey(SettingSerde.Int64, "You can skip reading more than that number of rows at the price of one seek per file.");
    public static SettingKey merge_tree_coarse_index_granularity = new SettingKey(SettingSerde.Int64, "If the index segment can contain the required keys, divide it into as many parts and recursively check them. ");
    public static SettingKey merge_tree_max_rows_to_use_cache = new SettingKey(SettingSerde.Int64, "The maximum number of rows per request, to use the cache of uncompressed data. If the request is large, the cache is not used. (For large queries not to flush out the cache.)");
    public static SettingKey merge_tree_uniform_read_distribution = new SettingKey(SettingSerde.Bool, "Distribute read from MergeTree over threads evenly, ensuring stable average execution time of each thread within one read operation.");
    public static SettingKey mysql_max_rows_to_insert = new SettingKey(SettingSerde.Int64, "The maximum number of rows in MySQL batch insertion of the MySQL storage engine");
    public static SettingKey optimize_min_equality_disjunction_chain_length = new SettingKey(SettingSerde.Int64, "The minimum length of the expression `expr = x1 OR ... expr = xN` for optimization ");
    public static SettingKey min_bytes_to_use_direct_io = new SettingKey(SettingSerde.Int64, "The minimum number of bytes for input/output operations is bypassing the page cache. 0 - disabled.");
    public static SettingKey force_index_by_date = new SettingKey(SettingSerde.Bool, "Throw an exception if there is a partition key in a table, and it is not used.");
    public static SettingKey force_primary_key = new SettingKey(SettingSerde.Bool, "Throw an exception if there is primary key in a table, and it is not used.");
    public static SettingKey mark_cache_min_lifetime = new SettingKey(SettingSerde.Int64, "If the maximum size of mark_cache is exceeded, delete only records older than mark_cache_min_lifetime seconds.");
    public static SettingKey max_streams_to_max_threads_ratio = new SettingKey(SettingSerde.Float32, "Allows you to use more sources than the number of threads - to more evenly distribute work across threads. It is assumed that this is a temporary solution, since it will be possible in the future to make the number of sources equal to the number of threads, but for each source to dynamically select available work for itself.");
    public static SettingKey network_zstd_compression_level = new SettingKey(SettingSerde.Int64, "Allows you to select the level of ZSTD compression.");
    public static SettingKey priority = new SettingKey(SettingSerde.Int64, "Priority of the query. 1 - the highest, higher value - lower priority; 0 - do not use priorities.");
    public static SettingKey log_queries = new SettingKey(SettingSerde.Bool, "Log requests and write the log to the system table.");
    public static SettingKey log_queries_cut_to_length = new SettingKey(SettingSerde.Int64, "If query length is greater than specified threshold (in bytes), then cut query when writing to query log. Also limit length of printed query in ordinary text log.");
    public static SettingKey max_concurrent_queries_for_user = new SettingKey(SettingSerde.Int64, "The maximum number of concurrent requests per user.");
    public static SettingKey insert_deduplicate = new SettingKey(SettingSerde.Bool, "For INSERT queries in the replicated table, specifies that deduplication of insertings blocks should be preformed");
    public static SettingKey insert_quorum = new SettingKey(SettingSerde.Int64, "For INSERT queries in the replicated table, wait writing for the specified number of replicas and linearize the addition of the data. 0 - disabled.");
    public static SettingKey select_sequential_consistency = new SettingKey(SettingSerde.Int64, "For SELECT queries from the replicated table, throw an exception if the replica does not have a chunk written with the quorum; do not read the parts that have not yet been written with the quorum.");
    public static SettingKey table_function_remote_max_addresses = new SettingKey(SettingSerde.Int64, "The maximum number of different shards and the maximum number of replicas of one shard in the `remote` function.");
    public static SettingKey read_backoff_min_latency_ms = new SettingKey(SettingSerde.Milliseconds, "Setting to reduce the number of threads in case of slow reads. Pay attention only to reads that took at least that much time.");
    public static SettingKey read_backoff_max_throughput = new SettingKey(SettingSerde.Int64, "Settings to reduce the number of threads in case of slow reads. Count events when the read bandwidth is less than that many bytes per second.");
    public static SettingKey read_backoff_min_interval_between_events_ms = new SettingKey(SettingSerde.Milliseconds, "Settings to reduce the number of threads in case of slow reads. Do not pay attention to the event, if the previous one has passed less than a certain amount of time.");
    public static SettingKey read_backoff_min_events = new SettingKey(SettingSerde.Int64, "Settings to reduce the number of threads in case of slow reads. The number of events after which the number of threads will be reduced.");
    public static SettingKey memory_tracker_fault_probability = new SettingKey(SettingSerde.Float32, "For testing of `exception safety` - throw an exception every time you allocate memory with the specified probability.");
    public static SettingKey enable_http_compression = new SettingKey(SettingSerde.Bool, "Compress the result if the client over HTTP said that it understands data compressed by gzip or deflate.");
    public static SettingKey http_zlib_compression_level = new SettingKey(SettingSerde.Int64, "Compression level - used if the client on HTTP said that it understands data compressed by gzip or deflate.");
    public static SettingKey http_native_compression_disable_checksumming_on_decompress = new SettingKey(SettingSerde.Bool, "If you uncompress the POST data from the client compressed by the native format, do not check the checksum.");
    public static SettingKey count_distinct_implementation = new SettingKey(SettingSerde.UTF8, "What aggregate function to use for implementation of count(DISTINCT ...)");
    public static SettingKey output_format_write_statistics = new SettingKey(SettingSerde.Bool, "Write statistics about read rows, bytes, time elapsed in suitable output formats.");
    public static SettingKey add_http_cors_header = new SettingKey(SettingSerde.Bool, "Write add http CORS header.");
    public static SettingKey input_format_skip_unknown_fields = new SettingKey(SettingSerde.Bool, "Skip columns with unknown names from input data (it works for JSONEachRow and TSKV formats).");
    public static SettingKey input_format_values_interpret_expressions = new SettingKey(SettingSerde.Bool, "For Values format: if field could not be parsed by streaming parser, run SQL parser and try to interpret it as SQL expression.");
    public static SettingKey output_format_json_quote_64bit_integers = new SettingKey(SettingSerde.Bool, "Controls quoting of 64-bit integers in JSON output format.");
    public static SettingKey output_format_json_quote_denormals = new SettingKey(SettingSerde.Bool, "Enables '+nan', '-nan', '+inf', '-inf' outputs in JSON output format.");
    public static SettingKey output_format_pretty_max_rows = new SettingKey(SettingSerde.Int64, "Rows limit for Pretty formats.");
    public static SettingKey use_client_time_zone = new SettingKey(SettingSerde.Bool, "Use client timezone for interpreting DateTime string values, instead of adopting server timezone.");
    public static SettingKey send_progress_in_http_headers = new SettingKey(SettingSerde.Bool, "Send progress notifications using X-ClickHouse-Progress headers. Some clients do not support high amount of HTTP headers (Python requests in particular), so it is disabled by default.");
    public static SettingKey http_headers_progress_interval_ms = new SettingKey(SettingSerde.Int64, "Do not send HTTP headers X-ClickHouse-Progress more frequently than at each specified interval.");
    public static SettingKey fsync_metadata = new SettingKey(SettingSerde.Bool, "Do fsync after changing metadata for tables and databases (.sql files). Could be disabled in case of poor latency on server with high load of DDL queries and high load of disk subsystem.");
    public static SettingKey input_format_allow_errors_num = new SettingKey(SettingSerde.Int64, "Maximum absolute amount of errors while reading text formats (like CSV, TSV). In case of error, if both absolute and relative values are non-zero, and at least absolute or relative amount of errors is lower than corresponding value, will skip until next line and continue.");
    public static SettingKey input_format_allow_errors_ratio = new SettingKey(SettingSerde.Float32, "Maximum relative amount of errors while reading text formats (like CSV, TSV). In case of error, if both absolute and relative values are non-zero, and at least absolute or relative amount of errors is lower than corresponding value, will skip until next line and continue.");
    public static SettingKey join_use_nulls = new SettingKey(SettingSerde.Bool, "Use NULLs for non-joined rows of outer JOINs. If false, use default value of corresponding columns data type.");
    public static SettingKey max_replica_delay_for_distributed_queries = new SettingKey(SettingSerde.Int64, "If set, distributed queries of Replicated tables will choose servers with replication delay in seconds less than the specified value (not inclusive). Zero means do not take delay into account.");
    public static SettingKey fallback_to_stale_replicas_for_distributed_queries = new SettingKey(SettingSerde.Bool, "Suppose max_replica_delay_for_distributed_queries is set and all replicas for the queried table are stale. If this setting is enabled, the query will be performed anyway, otherwise the error will be reported.");
    public static SettingKey preferred_max_column_in_block_size_bytes = new SettingKey(SettingSerde.Int64, "Limit on max column size in block while reading. Helps to decrease cache misses count. Should be close to L2 cache size.");
    public static SettingKey insert_distributed_sync = new SettingKey(SettingSerde.Bool, "If setting is enabled, insert query into distributed waits until data will be sent to all nodes in cluster.");
    public static SettingKey insert_distributed_timeout = new SettingKey(SettingSerde.Int64, "Timeout for insert query into distributed. Setting is used only with insert_distributed_sync enabled. Zero value means no timeout.");
    public static SettingKey distributed_ddl_task_timeout = new SettingKey(SettingSerde.Int64, "Timeout for DDL query responses from all hosts in cluster. Negative value means infinite.");
    public static SettingKey stream_flush_interval_ms = new SettingKey(SettingSerde.Milliseconds, "Timeout for flushing data from streaming storages.");
    public static SettingKey format_schema = new SettingKey(SettingSerde.UTF8, "Schema identifier (used by schema-based formats)");
    public static SettingKey insert_allow_materialized_columns = new SettingKey(SettingSerde.Bool, "If setting is enabled, Allow materialized columns in INSERT.");
    public static SettingKey http_connection_timeout = new SettingKey(SettingSerde.Seconds, "HTTP connection timeout.");
    public static SettingKey http_send_timeout = new SettingKey(SettingSerde.Seconds, "HTTP send timeout");
    public static SettingKey http_receive_timeout = new SettingKey(SettingSerde.Seconds, "HTTP receive timeout");
    public static SettingKey optimize_throw_if_noop = new SettingKey(SettingSerde.Bool, "If setting is enabled and OPTIMIZE query didn't actually assign a merge then an explanatory exception is thrown");
    public static SettingKey use_index_for_in_with_subqueries = new SettingKey(SettingSerde.Bool, "Try using an index if there is a subquery or a table expression on the right side of the IN operator.");
    public static SettingKey empty_result_for_aggregation_by_empty_set = new SettingKey(SettingSerde.Bool, "Return empty result when aggregating without keys on empty set.");
    public static SettingKey allow_distributed_ddl = new SettingKey(SettingSerde.Bool, "If it is set to true, then a user is allowed to executed distributed DDL queries.");
    public static SettingKey odbc_max_field_size = new SettingKey(SettingSerde.Int64, "Max size of filed can be read from ODBC dictionary. Long strings are truncated.");
    public static SettingKey max_rows_to_read = new SettingKey(SettingSerde.Int64, "Limit on read rows from the most 'deep' sources. That is, only in the deepest subquery. When reading from a remote server, it is only checked on a remote server.");
    public static SettingKey max_bytes_to_read = new SettingKey(SettingSerde.Int64, "Limit on read bytes (after decompression) from the most 'deep' sources. That is, only in the deepest subquery. When reading from a remote server, it is only checked on a remote server.");
    public static SettingKey max_result_rows = new SettingKey(SettingSerde.Int64, "Limit on result size in rows. Also checked for intermediate data sent from remote servers.");
    public static SettingKey max_result_bytes = new SettingKey(SettingSerde.Int64, "Limit on result size in bytes (uncompressed). Also checked for intermediate data sent from remote servers.");
    public static SettingKey result_overflow_mode = new SettingKey(SettingSerde.UTF8, "What to do when the limit is exceeded.");
    public static SettingKey min_execution_speed = new SettingKey(SettingSerde.Int64, "In rows per second.");
    public static SettingKey timeout_before_checking_execution_speed = new SettingKey(SettingSerde.Seconds, "Check that the speed is not too low after the specified time has elapsed.");
    public static SettingKey max_ast_depth = new SettingKey(SettingSerde.Int64, "Maximum depth of query syntax tree. Checked after parsing.");
    public static SettingKey max_ast_elements = new SettingKey(SettingSerde.Int64, "Maximum size of query syntax tree in number of nodes. Checked after parsing.");
    public static SettingKey max_expanded_ast_elements = new SettingKey(SettingSerde.Int64, "Maximum size of query syntax tree in number of nodes after expansion of aliases and the asterisk.");
    public static SettingKey readonly = new SettingKey(SettingSerde.Int64, "0 - everything is allowed. 1 - only read requests. 2 - only read requests, as well as changing settings, except for the 'readonly' setting.");
    public static SettingKey max_rows_in_set = new SettingKey(SettingSerde.Int64, "Maximum size of the set (in number of elements) resulting from the execution of the IN section.");
    public static SettingKey max_bytes_in_set = new SettingKey(SettingSerde.Int64, "Maximum size of the set (in bytes in memory) resulting from the execution of the IN section.");
    public static SettingKey max_rows_in_join = new SettingKey(SettingSerde.Int64, "Maximum size of the hash table for JOIN (in number of rows).");
    public static SettingKey max_bytes_in_join = new SettingKey(SettingSerde.Int64, "Maximum size of the hash table for JOIN (in number of bytes in memory).");
    public static SettingKey max_rows_to_transfer = new SettingKey(SettingSerde.Int64, "Maximum size (in rows) of the transmitted external table obtained when the GLOBAL IN/JOIN section is executed.");
    public static SettingKey max_bytes_to_transfer = new SettingKey(SettingSerde.Int64, "Maximum size (in uncompressed bytes) of the transmitted external table obtained when the GLOBAL IN/JOIN section is executed.");
    public static SettingKey max_rows_in_distinct = new SettingKey(SettingSerde.Int64, "Maximum number of elements during execution of DISTINCT.");
    public static SettingKey max_bytes_in_distinct = new SettingKey(SettingSerde.Int64, "Maximum total size of state (in uncompressed bytes) in memory for the execution of DISTINCT.");
    public static SettingKey max_memory_usage = new SettingKey(SettingSerde.Int64, "Maximum memory usage for processing of single query. Zero means unlimited.");
    public static SettingKey max_memory_usage_for_user = new SettingKey(SettingSerde.Int64, "Maximum memory usage for processing all concurrently running queries for the user. Zero means unlimited.");
    public static SettingKey max_memory_usage_for_all_queries = new SettingKey(SettingSerde.Int64, "Maximum memory usage for processing all concurrently running queries on the server. Zero means unlimited.");
    public static SettingKey max_network_bandwidth = new SettingKey(SettingSerde.Int64, "The maximum speed of data exchange over the network in bytes per second for a query. Zero means unlimited.");
    public static SettingKey max_network_bytes = new SettingKey(SettingSerde.Int64, "The maximum number of bytes (compressed) to receive or transmit over the network for execution of the query.");
    public static SettingKey max_network_bandwidth_for_user = new SettingKey(SettingSerde.Int64, "The maximum speed of data exchange over the network in bytes per second for all concurrently running user queries. Zero means unlimited.");
    public static SettingKey max_network_bandwidth_for_all_users = new SettingKey(SettingSerde.Int64, "The maximum speed of data exchange over the network in bytes per second for all concurrently running queries. Zero means unlimited.");
    public static SettingKey format_csv_delimiter = new SettingKey(SettingSerde.Char, "The character to be considered as a delimiter in CSV data. If setting with a string, a string has to have a length of 1.");
    public static SettingKey enable_conditional_computation = new SettingKey(SettingSerde.Int64, "Enable conditional computations");
    public static SettingKey allow_experimental_bigint_types = new SettingKey(SettingSerde.Int64, "Allow Int128, Int256, UInt256 and Decimal256 types");


    public static SettingKey port = new SettingKey(SettingSerde.Int32, "");
    public static SettingKey user = new SettingKey(SettingSerde.UTF8, "");
    public static SettingKey host = new SettingKey(SettingSerde.UTF8, "");
    public static SettingKey database = new SettingKey(SettingSerde.UTF8, "");
    public static SettingKey password = new SettingKey(SettingSerde.UTF8, "");
    public static SettingKey query_timeout = new SettingKey(SettingSerde.Seconds, "");
    public static SettingKey tcp_keep_alive = new SettingKey(SettingSerde.Bool, "");
    public static SettingKey charset = new SettingKey(SettingSerde.UTF8, "charset for converting between Bytes and String");


    private final String describe;
    private final SettingSerde<?> serde;

    public SettingKey(SettingSerde<?> serde, String describe) {
        this.serde = serde;
        this.describe = describe;
    }

    public String describe() {
        return describe;
    }

    public SettingSerde<?> type() {
        return serde;
    }
}
