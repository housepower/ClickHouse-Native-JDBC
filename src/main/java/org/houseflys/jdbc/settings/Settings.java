package org.houseflys.jdbc.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Settings {
    private static final int INITIAL_VERSION = 0;

    private final Map<String, Setting> settings;

    Settings(Map<String, Setting> settings) {
        this.settings = settings;
    }

    Setting getSetting(String name) {
        return settings.get(name);
    }

    public static Settings fromProperties(Properties properties) {
        Settings settings = initialDefaultSettings();

        for (Object key : properties.keySet()) {
            Setting setting = settings.settings.get(String.valueOf(key));

            if (setting != null) {
                Class type = setting.getClass();

                String newValue = properties.getProperty(String.valueOf(key));

                if (Boolean.class.isAssignableFrom(type)) {
                    setting = setting.changeValue(Boolean.valueOf(newValue));
                } else if (Integer.class.isAssignableFrom(type)) {
                    setting = setting.changeValue(Integer.valueOf(newValue));
                } else if (Long.class.isAssignableFrom(type)) {
                    setting = setting.changeValue(Long.valueOf(newValue));
                } else if (Double.class.isAssignableFrom(type)) {
                    setting = setting.changeValue(Double.valueOf(newValue));
                }

                settings.settings.remove(String.valueOf(key));
                settings.settings.put(String.valueOf(key), setting);
            }
        }
        return settings;
    }

    private static Settings initialDefaultSettings() {
        return initialSettings(
            new Setting(INITIAL_VERSION, SettingsKey.ADD_HTTP_CORS_HEADER, false, "Write add http CORS header"),
            new Setting(INITIAL_VERSION, SettingsKey.AGGREGATION_MEMORY_EFFICIENT_MERGE_THREADS, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.BACKGROUND_POOL_SIZE, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.COMPILE, false, ""),
            new Setting(INITIAL_VERSION, SettingsKey.COMPRESS, true, "whether to compress transferred data or not"),
            new Setting(INITIAL_VERSION, SettingsKey.CONNECT_TIMEOUT, Integer.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.CONNECT_TIMEOUT_WITH_FAILOVER_MS, Integer.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.CONNECTIONS_WITH_FAILOVER_MAX_TRIES, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.COUNT_DISTINCT_IMPLEMENTATION, String.class,
                "What aggregate function to use for implementation of count(DISTINCT ...)"),
            new Setting(INITIAL_VERSION, SettingsKey.DATABASE, String.class, "database name used by default"),
            new Setting(INITIAL_VERSION, SettingsKey.DECOMPRESS, false,
                "whether to decompress transferred data or not"),
            new Setting(INITIAL_VERSION, SettingsKey.DISTRIBUTED_AGGREGATION_MEMORY_EFFICIENT, false,
                "Whether to optimize memory consumption for external aggregation"),
            new Setting(INITIAL_VERSION, SettingsKey.DISTRIBUTED_CONNECTIONS_POOL_SIZE, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.DISTRIBUTED_DIRECTORY_MONITOR_SLEEP_TIME_MS, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.DISTRIBUTED_GROUP_BY_NO_MERGE, false, ""),
            new Setting(INITIAL_VERSION, SettingsKey.DISTRIBUTED_PRODUCT_MODE, String.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.ENABLE_HTTP_COMPRESSION, false, ""),
            new Setting(INITIAL_VERSION, SettingsKey.EXTREMES, false, "Whether to include extreme values."),
            new Setting(INITIAL_VERSION, SettingsKey.FORCE_INDEX_BY_DATE, false, ""),
            new Setting(INITIAL_VERSION, SettingsKey.FORCE_PRIMARY_KEY, false, ""),
            new Setting(INITIAL_VERSION, SettingsKey.GLOBAL_SUBQUERIES_METHOD, String.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.GROUP_BY_TWO_LEVEL_THRESHOLD, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.GROUP_BY_TWO_LEVEL_THRESHOLD_BYTES, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.HTTP_NATIVE_COMPRESSION_DISABLE_CHECKSUMMING_ON_DECOMPRESS,
                Boolean.class, "Whether to disable checksum check on decompress"),
            new Setting(INITIAL_VERSION, SettingsKey.HTTP_ZLIB_COMPRESSION_LEVEL, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.INPUT_FORMAT_SKIP_UNKNOWN_FIELDS, false,
                "Skip columns with unknown names from input data (it works for JSONEachRow and TSKV formats)."),
            new Setting(INITIAL_VERSION, SettingsKey.INPUT_FORMAT_VALUES_INTERPRET_EXPRESSIONS, true,
                "For Values format: if field could not be parsed by streaming parser, run SQL parser and try to interpret it as SQL expression."),
            new Setting(INITIAL_VERSION, SettingsKey.INSERT_QUORUM, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.INSERT_QUORUM_TIMEOUT, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.INTERACTIVE_DELAY, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.LOAD_BALANCING, String.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.LOG_QUERIES, false, ""),
            new Setting(INITIAL_VERSION, SettingsKey.LOG_QUERIES_CUT_TO_LENGTH, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MARK_CACHE_MIN_LIFETIME, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MAX_BLOCK_SIZE, Integer.class,
                "Recommendation for what size of block (in number of rows) to load from tables"),
            new Setting(INITIAL_VERSION, SettingsKey.MAX_BYTES_BEFORE_EXTERNAL_GROUP_BY, Long.class,
                "Threshold to use external group by"),
            new Setting(INITIAL_VERSION, SettingsKey.MAX_BYTES_BEFORE_EXTERNAL_SORT, Long.class,
                "Threshold to use external sort"),
            new Setting(INITIAL_VERSION, SettingsKey.MAX_COMPRESS_BLOCK_SIZE, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MAX_CONCURRENT_QUERIES_FOR_USER, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MAX_DISTRIBUTED_CONNECTIONS, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MAX_DISTRIBUTED_PROCESSING_THREADS, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MAX_EXECUTION_TIME, Integer.class,
                "Maximum query execution time in seconds."),
            new Setting(INITIAL_VERSION, SettingsKey.MAX_INSERT_BLOCK_SIZE, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MAX_MEMORY_USAGE, Long.class,
                "The maximum amount of memory consumption when running a query on a single server."),
            new Setting(INITIAL_VERSION, SettingsKey.MAX_PARALLEL_REPLICAS, Integer.class, "Max shard replica count."),
            new Setting(INITIAL_VERSION, SettingsKey.MAX_READ_BUFFER_SIZE, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MAX_RESULT_ROWS, Integer.class,
                "Limit on the number of rows in the result. Also checked for subqueries, and on remote servers when running parts of a distributed query."),
            new Setting(INITIAL_VERSION, SettingsKey.MAX_ROWS_TO_GROUP_BY, Integer.class,
                "Maximum number of unique keys received from aggregation. This setting lets you limit memory consumption when aggregating."),
            new Setting(INITIAL_VERSION, SettingsKey.MAX_STREAMS_TO_MAX_THREADS_RATIO, Double.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MAX_THREADS, Integer.class,
                "The maximum number of query processing threads"),
            new Setting(INITIAL_VERSION, SettingsKey.MAX_QUERY_SIZE, Long.class, "Maximum size of query"),
            new Setting(INITIAL_VERSION, SettingsKey.MEMORY_TRACKER_FAULT_PROBABILITY, Double.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MERGE_TREE_COARSE_INDEX_GRANULARITY, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MERGE_TREE_MAX_ROWS_TO_USE_CACHE, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MERGE_TREE_MIN_ROWS_FOR_CONCURRENT_READ, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MERGE_TREE_MIN_ROWS_FOR_SEEK, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MERGE_TREE_UNIFORM_READ_DISTRIBUTION, true, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MIN_BYTES_TO_USE_DIRECT_IO, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MIN_COMPRESS_BLOCK_SIZE, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MIN_COUNT_TO_COMPILE, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.MIN_INSERT_BLOCK_SIZE_BYTES, Long.class,
                "Squash blocks passed to INSERT query to specified size in bytes, if blocks are not big enoug"),
            new Setting(INITIAL_VERSION, SettingsKey.MIN_INSERT_BLOCK_SIZE_ROWS, Long.class,
                "Squash blocks passed to INSERT query to specified size in rows, if blocks are not big enough."),
            new Setting(INITIAL_VERSION, SettingsKey.NETWORK_COMPRESSION_METHOD, String.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.OPTIMIZE_MIN_EQUALITY_DISJUNCTION_CHAIN_LENGTH, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.OPTIMIZE_MOVE_TO_PREWHERE, true, ""),
            new Setting(INITIAL_VERSION, SettingsKey.OUTPUT_FORMAT_JSON_QUOTE_64BIT_INTEGERS, true,
                "Controls quoting of 64-bit integers in JSON output format."),
            new Setting(INITIAL_VERSION, SettingsKey.OUTPUT_FORMAT_PRETTY_MAX_ROWS, Long.class,
                "Rows limit for Pretty formats."),
            new Setting(INITIAL_VERSION, SettingsKey.OUTPUT_FORMAT_WRITE_STATISTICS, true,
                "Write statistics about read rows, bytes, time elapsed in suitable output formats"),
            new Setting(INITIAL_VERSION, SettingsKey.PARALLEL_REPLICAS_COUNT, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.PARALLEL_REPLICA_OFFSET, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.PASSWORD, String.class, "user password, by default null"),
            new Setting(INITIAL_VERSION, SettingsKey.POLL_INTERVAL, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.PRIORITY, Integer.class,
                "The lower the value the bigger the priority."),
            new Setting(INITIAL_VERSION, SettingsKey.PROFILE, String.class,
                "Settings profile: a collection of settings grouped under the same name"),
            new Setting(INITIAL_VERSION, SettingsKey.RECEIVE_TIMEOUT, Integer.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.READ_BACKOFF_MAX_THROUGHPUT, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.READ_BACKOFF_MIN_EVENTS, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.READ_BACKOFF_MIN_INTERVAL_BETWEEN_EVENTS_MS, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.READ_BACKOFF_MIN_LATENCY_MS, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.REPLACE_RUNNING_QUERY, false, ""),
            new Setting(INITIAL_VERSION, SettingsKey.REPLICATION_ALTER_COLUMNS_TIMEOUT, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.REPLICATION_ALTER_PARTITIONS_SYNC, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.RESHARDING_BARRIER_TIMEOUT, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.RESULT_OVERFLOW_MODE, String.class,
                "What to do if the volume of the result exceeds one of the limits: 'throw' or 'break'. By default, throw. Using 'break' is similar to using LIMIT."),
            new Setting(INITIAL_VERSION, SettingsKey.SELECT_SEQUENTIAL_CONSISTENCY, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.SEND_TIMEOUT, Integer.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.SESSION_CHECK, false, ""),
            new Setting(INITIAL_VERSION, SettingsKey.SESSION_ID, String.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.SESSION_TIMEOUT, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.SKIP_UNAVAILABLE_SHARDS, false, ""),
            new Setting(INITIAL_VERSION, SettingsKey.STRICT_INSERT_DEFAULTS, false, ""),
            new Setting(INITIAL_VERSION, SettingsKey.TABLE_FUNCTION_REMOTE_MAX_ADDRESSES, Long.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.TOTALS_AUTO_THRESHOLD, Double.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.TOTALS_MODE, String.class,
                "How to calculate TOTALS when HAVING is present, as well as when max_rows_to_group_by and group_by_overflow_mode = 'any' are present."),
            new Setting(INITIAL_VERSION, SettingsKey.QUERY_ID, String.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.QUEUE_MAX_WAIT_MS, Integer.class, ""),
            new Setting(INITIAL_VERSION, SettingsKey.QUOTA_KEY, String.class,
                "quota is calculated for each quota_key value. For example here may be some user name."),
            new Setting(INITIAL_VERSION, SettingsKey.use_client_time_zone, false, ""),
            new Setting(INITIAL_VERSION, SettingsKey.USE_UNCOMPRESSED_CACHE, true,
                "Use client timezone for interpreting DateTime string values, instead of adopting server timezone."),
            new Setting(INITIAL_VERSION, SettingsKey.USER, String.class, "user name, by default - default"),
            new Setting(INITIAL_VERSION, SettingsKey.PREFERRED_BLOCK_SIZE_BYTES, Long.class,
                "Adaptively estimates number of required rows in a block.")
        );
    }

    private static Settings initialSettings(Setting... settings) {
        Map<String, Setting> res = new HashMap<String, Setting>();
        for (Setting setting : settings) {
            res.put(setting.key(), setting);
        }

        return new Settings(res);
    }
}
