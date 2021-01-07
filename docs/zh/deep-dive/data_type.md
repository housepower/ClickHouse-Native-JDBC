ClickHouse 数据类型
====================

## Version
```
SELECT version()

┌─version()─┐
│ 20.8.5.45 │
└───────────┘
```

## Data Type Families

```
SELECT * FROM system.data_type_families

┌─name────────────────────────────┬─case_insensitive─┬─alias_to────┐
│ Polygon                         │                0 │             │
│ Ring                            │                0 │             │
│ MultiPolygon                    │                0 │             │
│ IPv6                            │                0 │             │
│ IntervalSecond                  │                0 │             │
│ IPv4                            │                0 │             │
│ UInt32                          │                0 │             │
│ IntervalYear                    │                0 │             │
│ IntervalQuarter                 │                0 │             │
│ IntervalMonth                   │                0 │             │
│ Int64                           │                0 │             │
│ IntervalDay                     │                0 │             │
│ IntervalHour                    │                0 │             │
│ UInt256                         │                0 │             │
│ Int16                           │                0 │             │
│ LowCardinality                  │                0 │             │
│ AggregateFunction               │                0 │             │
│ Nothing                         │                0 │             │
│ Decimal256                      │                1 │             │
│ Tuple                           │                0 │             │
│ Array                           │                0 │             │
│ Enum16                          │                0 │             │
│ Enum8                           │                0 │             │
│ IntervalMinute                  │                0 │             │
│ FixedString                     │                0 │             │
│ String                          │                0 │             │
│ DateTime                        │                1 │             │
│ UUID                            │                0 │             │
│ Decimal64                       │                1 │             │
│ Nullable                        │                0 │             │
│ Enum                            │                0 │             │
│ Int32                           │                0 │             │
│ UInt8                           │                0 │             │
│ Date                            │                1 │             │
│ Decimal32                       │                1 │             │
│ Point                           │                0 │             │
│ Float64                         │                0 │             │
│ DateTime64                      │                1 │             │
│ Int128                          │                0 │             │
│ Decimal128                      │                1 │             │
│ Int8                            │                0 │             │
│ SimpleAggregateFunction         │                0 │             │
│ Nested                          │                0 │             │
│ Decimal                         │                1 │             │
│ Int256                          │                0 │             │
│ IntervalWeek                    │                0 │             │
│ UInt64                          │                0 │             │
│ UInt16                          │                0 │             │
│ Float32                         │                0 │             │
│ INET6                           │                1 │ IPv6        │
│ INET4                           │                1 │ IPv4        │
│ BINARY                          │                1 │ FixedString │
│ NATIONAL CHAR VARYING           │                1 │ String      │
│ BINARY VARYING                  │                1 │ String      │
│ NCHAR LARGE OBJECT              │                1 │ String      │
│ NATIONAL CHARACTER VARYING      │                1 │ String      │
│ NATIONAL CHARACTER LARGE OBJECT │                1 │ String      │
│ NATIONAL CHARACTER              │                1 │ String      │
│ NATIONAL CHAR                   │                1 │ String      │
│ CHARACTER VARYING               │                1 │ String      │
│ LONGBLOB                        │                1 │ String      │
│ MEDIUMTEXT                      │                1 │ String      │
│ TEXT                            │                1 │ String      │
│ TINYBLOB                        │                1 │ String      │
│ VARCHAR2                        │                1 │ String      │
│ CHARACTER LARGE OBJECT          │                1 │ String      │
│ DOUBLE PRECISION                │                1 │ Float64     │
│ LONGTEXT                        │                1 │ String      │
│ NVARCHAR                        │                1 │ String      │
│ INT1 UNSIGNED                   │                1 │ UInt8       │
│ VARCHAR                         │                1 │ String      │
│ CHAR VARYING                    │                1 │ String      │
│ MEDIUMBLOB                      │                1 │ String      │
│ NCHAR                           │                1 │ String      │
│ CHAR                            │                1 │ String      │
│ SMALLINT UNSIGNED               │                1 │ UInt16      │
│ TIMESTAMP                       │                1 │ DateTime    │
│ FIXED                           │                1 │ Decimal     │
│ TINYTEXT                        │                1 │ String      │
│ NUMERIC                         │                1 │ Decimal     │
│ DEC                             │                1 │ Decimal     │
│ TINYINT UNSIGNED                │                1 │ UInt8       │
│ INTEGER UNSIGNED                │                1 │ UInt32      │
│ INT UNSIGNED                    │                1 │ UInt32      │
│ CLOB                            │                1 │ String      │
│ MEDIUMINT UNSIGNED              │                1 │ UInt32      │
│ BOOL                            │                1 │ Int8        │
│ SMALLINT                        │                1 │ Int16       │
│ INTEGER SIGNED                  │                1 │ Int32       │
│ NCHAR VARYING                   │                1 │ String      │
│ INT SIGNED                      │                1 │ Int32       │
│ TINYINT SIGNED                  │                1 │ Int8        │
│ BIGINT SIGNED                   │                1 │ Int64       │
│ BINARY LARGE OBJECT             │                1 │ String      │
│ SMALLINT SIGNED                 │                1 │ Int16       │
│ MEDIUMINT                       │                1 │ Int32       │
│ INTEGER                         │                1 │ Int32       │
│ INT1 SIGNED                     │                1 │ Int8        │
│ BIGINT UNSIGNED                 │                1 │ UInt64      │
│ BYTEA                           │                1 │ String      │
│ INT                             │                1 │ Int32       │
│ SINGLE                          │                1 │ Float32     │
│ FLOAT                           │                1 │ Float32     │
│ MEDIUMINT SIGNED                │                1 │ Int32       │
│ BOOLEAN                         │                1 │ Int8        │
│ DOUBLE                          │                1 │ Float64     │
│ INT1                            │                1 │ Int8        │
│ CHAR LARGE OBJECT               │                1 │ String      │
│ TINYINT                         │                1 │ Int8        │
│ BIGINT                          │                1 │ Int64       │
│ CHARACTER                       │                1 │ String      │
│ BYTE                            │                1 │ Int8        │
│ BLOB                            │                1 │ String      │
│ REAL                            │                1 │ Float32     │
└─────────────────────────────────┴──────────────────┴─────────────┘
```