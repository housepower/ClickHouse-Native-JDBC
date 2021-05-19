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

package com.github.housepower.jdbc.tool;

import com.github.housepower.jdbc.AbstractITest;
import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.google.common.base.Joiner;

import java.sql.Array;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class TestHarness extends AbstractITest {
    static final Logger LOG = LoggerFactory.getLogger(TestHarness.class);
    static final int RECORD_COUNT = (1 << 17);
    static Function<String, String> SUM_EXPR = s -> "sum(" + s + ")";
    static Function<String, String> MAX_EXPR = s -> "max(" + s + ")";
    static List<DataTypeApply> ALL_TYPES = new ArrayList<>();

    static {
        ALL_TYPES.add(new DataTypeApply(
                "Int8",
                (i) -> i % 128,
                MAX_EXPR,
                (rows) -> 127.0)
        );

        ALL_TYPES.add(new DataTypeApply(
                "Int16",
                (i) -> i % 32768,
                MAX_EXPR,
                (rows) -> 32767.0)
        );

        ALL_TYPES.add(new DataTypeApply(
                "Int32",
                (i) -> 3,
                SUM_EXPR,
                (rows) -> rows * 3.0)
        );

        ALL_TYPES.add(new DataTypeApply(
                "Int64",
                (i) -> 4,
                SUM_EXPR,
                (rows) -> rows * 4.0)
        );

        ALL_TYPES.add(new DataTypeApply(
                "UInt8",
                (i) -> i % 256,
                MAX_EXPR,
                (rows) -> 255.0)
        );

        ALL_TYPES.add(new DataTypeApply(
                "UInt16",
                (i) -> i % 65536,
                MAX_EXPR,
                (rows) -> 65535.0)
        );

        ALL_TYPES.add(new DataTypeApply(
                "UInt32",
                (i) -> 3,
                SUM_EXPR,
                (rows) -> rows * 3.0)
        );

        ALL_TYPES.add(new DataTypeApply(
                "UInt64",
                (i) -> 4,
                SUM_EXPR,
                (rows) -> rows * 4.0)
        );

        ALL_TYPES.add(new DataTypeApply(
                "Float32",
                (i) -> 5.0f,
                SUM_EXPR,
                (rows) -> rows * 5.0)
        );

        ALL_TYPES.add(new DataTypeApply(
                "Float64",
                (i) -> 6.0,
                SUM_EXPR,
                (rows) -> rows * 6.0)
        );

        ALL_TYPES.add(new DataTypeApply(
                "Nullable(Float64)",
                (i) -> 6.0,
                SUM_EXPR,
                (rows) -> rows * 6.0)
        );

        ALL_TYPES.add(new DataTypeApply(
                "String",
                (i) -> "00" + i,
                (col) -> "sum(toInt64(" + col + ") % 4)",
                (rows) -> (rows / 4.0 * (1 + 2 + 3)))
        );

        ALL_TYPES.add(new DataTypeApply(
                "Date",
                (i) -> Date.valueOf(LocalDate.ofEpochDay(i / 10000)),
                MAX_EXPR,
                (rows) -> rows / 10000 * 1.0)
        );

        ALL_TYPES.add(new DataTypeApply(
                "DateTime",
                (i) -> new Timestamp(i / 1000 * 1000),
                MAX_EXPR,
                (rows) -> new Timestamp(rows).getTime() / 1000 * 1.0)
        );

        ALL_TYPES.add(new DataTypeApply(
                "Array(String)",
                (i) -> new Object[]{"00" + i},
                (col) -> "sum(toInt64(" + col + "[1]) % 4)",
                (rows) -> (rows / 4.0 * (1 + 2 + 3)))
        );
    }

    private final String tableName;

    private final DataTypeApply[] types;

    public TestHarness() {
        this(ALL_TYPES.stream().map(type -> type.name).toArray(String[]::new));
    }

    public TestHarness(String... allowedDataTypes) {
        this.tableName = "test_" + (new Random().nextLong() & 0xffffffffL);
        this.types = ALL_TYPES.stream()
                .filter(type -> Arrays.stream(allowedDataTypes).anyMatch(allowed -> allowed.equals(type.name)))
                .toArray(DataTypeApply[]::new);
        LOG.info("Create TestHarness with table: {}", tableName);
    }

    public void create() throws Exception {
        String sql = buildCreateTableSQL();

        withStatement(stmt -> stmt.execute(sql));
    }

    public void insert() throws Exception {
        String sql = buildInsertPreparedSQL();
        withPreparedStatement(sql, pstmt -> {
            for (int row = 0; row < RECORD_COUNT; row++) {
                for (int i = 0; i < types.length; i++) {
                    if ("Array(String)".equals(types[i].name)) { // only support Array(String) here
                        Array array = pstmt.getConnection().createArrayOf("String", (Object[]) types[i].data.apply(row));
                        pstmt.setObject(i + 1, array);
                    } else {
                        pstmt.setObject(i + 1, types[i].data.apply(row));
                    }
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        });
    }

    public void checkAgg() throws Exception {
        Double[] results = new Double[types.length];

        for (int i = 0; i < types.length; i++) {
            results[i] = types[i].agg.apply(RECORD_COUNT);
        }

        withStatement(stmt -> {
            ResultSet rs = stmt.executeQuery(buildSelectAggSQL());
            assertTrue(rs.next());
            for (int i = 0; i < types.length; i++) {
                String typeName = types[i].name;
                Double result = rs.getDouble(i + 1);
                assertEquals(results[i], result, "Check Agg Error Type: " + typeName);
            }
        });
    }

    public void checkItem() throws Exception {
        withStatement(stmt -> {
            ResultSet rs = stmt.executeQuery(buildSelectStarSQL());
            Integer r = 0;
            while (rs.next()) {
                for (int i = 0; i < types.length; i++) {
                    String typeName = types[i].name;
                    if (types[i].data.apply(r) instanceof Number) {
                        Number expected = (Number) (types[i].data.apply(r));
                        Number actual = (Number) (rs.getObject(i + 1));
                        assertEquals(expected.intValue(), actual.intValue(),
                                "Check Item Error Type: " + typeName + ", at row: " + r);
                    } else if (types[i].data.apply(r) instanceof Object[]) {
                        Object[] expected = (Object[]) types[i].data.apply(r);
                        Object[] actual = (Object[]) rs.getArray(i + 1).getArray();
                        assertArrayEquals(expected, actual,
                                "Check Item Error Type: " + typeName + ", at row: " + r);
                    } else {
                        assertEquals(types[i].data.apply(r), rs.getObject(i + 1),
                                "Check Item Error Type: " + typeName + ", at row: " + r);
                    }
                }
                r++;
            }
        });
    }

    public void clean() throws Exception {
        String sql = buildDropTableSQL();
        withStatement(stmt -> stmt.execute(sql));
    }

    public String getTableName() {
        return tableName;
    }

    public DataTypeApply[] getTypes() {
        return types;
    }

    private String buildCreateTableSQL() {
        StringBuilder sb = new StringBuilder("CREATE TABLE " + tableName + " (");
        for (int i = 0; i < types.length; i++) {
            if (i != 0) {
                sb.append(",\n");
            }
            sb.append("col_").append(i).append(" ").append(types[i].name);
        }
        sb.append(" ) Engine=Memory");
        String sql = sb.toString();
        LOG.trace("CREATE TABLE DDL: \n{}", sql);
        return sql;
    }

    private String buildInsertPreparedSQL() {
        List<String> cols = new ArrayList<>();
        List<String> quotas = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            cols.add("col_" + i);
            quotas.add("?");
        }
        return String.format(Locale.ROOT, "INSERT INTO %s (%s) VALUES (%s)",
                tableName,
                Joiner.on(",").join(cols),
                Joiner.on(",").join(quotas));
    }

    private String buildSelectStarSQL() {
        return "SELECT * FROM " + tableName;
    }

    private String buildSelectAggSQL() {
        List<String> aggCols = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            aggCols.add("toFloat64(" + types[i].expr.apply("col_" + i) + ")");
        }
        return String.format(Locale.ROOT, "SELECT %s FROM %s",
                Joiner.on(",").join(aggCols),
                tableName);
    }

    private String buildDropTableSQL() {
        return "DROP TABLE IF EXISTS " + tableName;
    }

    public static class DataTypeApply {
        String name;
        Function<Integer, Object> data;
        Function<String, String> expr;
        Function<Integer, Double> agg;

        public DataTypeApply(String name,
                             Function<Integer, Object> data,
                             Function<String, String> expr,
                             Function<Integer, Double> agg) {
            this.name = name;
            this.data = data;
            this.expr = expr;
            this.agg = agg;
        }
    }
}
