package com.github.housepower.jdbc;

import com.google.common.base.Joiner;

import org.junit.Assert;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * implements to test all supported datatypes
 */
public class GenericSimpleInsertITest extends AbstractITest {

    final int insertRows = (1 << 17);
    final String tableName;
    List<DataTypeApply> types = new ArrayList<>();

    public GenericSimpleInsertITest() {
        tableName = "test_" + (new Random().nextLong() & 0xffffffffL);
        initAllTypes();
    }


    @Test
    public void runGeneric() throws Exception {
        clean();
        create();
        insert();
        check();
        clean();
    }

    private void initAllTypes() {
        types.add(new DataTypeApply(() -> "Int8", (i) -> i % 128, maxExpr,
                                    (rows) -> 127.0));

        types.add(new DataTypeApply(() -> "Int16", (i) -> i % 32768, maxExpr,
                                    (rows) -> 32767.0));

        types.add(new DataTypeApply(() -> "Int32", (i) -> 3, sumExpr,
                                    (rows) -> rows * 3.0));

        types.add(new DataTypeApply(() -> "Int64", (i) -> 4, sumExpr,
                                    (rows) -> rows * 4.0));

        types.add(new DataTypeApply(() -> "UInt8", (i) -> i % 256, maxExpr,
                                    (rows) -> 255.0));

        types.add(new DataTypeApply(() -> "UInt16", (i) -> i % 65536, maxExpr,
                                    (rows) -> 65535.0));

        types.add(new DataTypeApply(() -> "UInt32", (i) -> 3, sumExpr,
                                    (rows) -> rows * 3.0));

        types.add(new DataTypeApply(() -> "UInt64", (i) -> 4, sumExpr,
                                    (rows) -> rows * 4.0));

        types.add(new DataTypeApply(() -> "Float32", (i) -> 5.0f, sumExpr,
                                    (rows) -> rows * 5.0));
        types.add(new DataTypeApply(() -> "Float64", (i) -> 6.0, sumExpr,
                                    (rows) -> rows * 6.0));

        types.add(new DataTypeApply(() -> "Nullable(Float64)", (i) -> 6.0, sumExpr,
                                    (rows) -> rows * 6.0));

        types.add(
            new DataTypeApply(() -> "String", (i) -> "00" + i,
                              (col) -> "sum(toInt64(" + col + ") % 4)",
                              (rows) -> (rows / 4.0 * (1 + 2 + 3))));

        types.add(
            new DataTypeApply(() -> "DateTime", (i) -> new Timestamp(i / 1000 * 1000), maxExpr,
                              (rows) -> new Timestamp(rows).getTime() / 1000 * 1.0));

    }

    public void create() throws Exception {
        StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + " (");
        for (int i = 0; i < types.size(); i++) {
            if (i != 0) {
                sql.append(",\n");
            }
            sql.append("col_" + i);
            sql.append(" ");
            sql.append(types.get(i).name.get());
        }

        sql.append(" ) Engine=Memory");

        withNewConnection(connection -> {
            Statement stmt = connection.createStatement();
            stmt.execute(sql.toString());
        });
    }

    public void insert() throws Exception {
        List<String> cols = new ArrayList<>();
        List<String> quotas = new ArrayList<>();

        for (int i = 0; i < types.size(); i++) {
            cols.add("col_" + i);
            quotas.add("?");
        }

        String
            sql =
            String.format(Locale.ROOT, "INSERT INTO %s (%s) VALUES (%s)", tableName,
                          Joiner.on(",").join(cols),
                          Joiner.on(",").join(quotas));

        withNewConnection(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql);
            for (int row = 0; row < insertRows; row++) {
                for (int i = 0; i < types.size(); i++) {
                    stmt.setObject(i + 1, types.get(i).data.apply(row));
                }
                stmt.addBatch();
            }
            stmt.executeBatch();
        });
    }

    public void check() throws Exception {
        StringBuilder sql = new StringBuilder("SELECT ");
        Double[] results = new Double[types.size()];

        for (int i = 0; i < types.size(); i++) {
            if (i != 0) {
                sql.append(",\n");
            }
            sql.append("toFloat64(");
            sql.append(types.get(i).expr.apply("col_" + i));
            sql.append(")");
            results[i] = types.get(i).aggr.apply(insertRows);
        }
        sql.append(" FROM ").append(tableName);

        withNewConnection(connection -> {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql.toString());

            Assert.assertTrue(rs.next());
            for (int i = 0; i < types.size(); i++) {
                Double result = rs.getDouble(i + 1);
                Assert.assertEquals("Check Aggr Error Type: " + types.get(i).name.get(), results[i],
                                    result);
            }
            rs = stmt.executeQuery("SELECT * FROM " + tableName);
            int r = 0;
            while (rs.next()) {
                for (int i = 0; i < types.size(); i++) {
                    if (types.get(i).data.apply(r) instanceof Number) {
                        Number expected = (Number) (types.get(i).data.apply(r));
                        Number actually = (Number) (rs.getObject(i + 1));

                        Assert.assertEquals(
                            "Check Item Error Type: " + types.get(i).name.get() + ", at row: " + r,
                            expected.intValue(), actually.intValue());
                        continue;
                    }
                    Assert.assertEquals(
                        "Check Item Error Type: " + types.get(i).name.get() + ", at row: " + r,
                        types.get(i).data.apply(r),
                        rs.getObject(i + 1));
                }
                r++;
            }
        });
    }

    public void clean() throws Exception {
        String sql = "DROP TABLE IF EXISTS " + tableName;
        withNewConnection(connection -> {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        });
    }

    public String getTableName() {
        return tableName;
    }

    static Function<String, String> sumExpr = s -> "sum(" + s + ")";
    static Function<String, String> maxExpr = s -> "max(" + s + ")";

    class DataTypeApply {

        Supplier<String> name;
        Function<Integer, Object> data;
        Function<String, String> expr;
        Function<Integer, Double> aggr;

        public DataTypeApply(Supplier<String> name, Function<Integer, Object> data,
                             Function<String, String> expr, Function<Integer, Double> aggr) {
            this.name = name;
            this.data = data;
            this.expr = expr;
            this.aggr = aggr;
        }
    }
}
