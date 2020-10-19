package com.github.housepower.jdbc;

import com.google.common.base.Strings;

import org.junit.Assert;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 */
public class IssueReproduceITest extends AbstractITest{
    @Test
    public void testIssue63() throws Exception {
        withNewConnection(connection -> {
            int columnNum = 36;
            Statement statement = connection.createStatement();
            statement.executeQuery("DROP TABLE IF EXISTS test");
            String params = Strings.repeat("?, ", columnNum);
            String columnTypes = "";
            for (int i = 0; i < columnNum; i++) {
                if (i != 0) {
                    columnTypes += ", ";
                }
                columnTypes += "t_" +i + " String";
            }

            statement.executeQuery("CREATE TABLE test( "  + columnTypes +  ")ENGINE=Log");
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO test values(" + params.substring(0, params.length() - 2) + ")");

            for (int i = 0; i < 100; ++i) {
                for (int j = 0; j < columnNum; j ++) {
                    pstmt.setString(j + 1, "String" + j);
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();

            ResultSet rs = statement.executeQuery("SELECT count(1) FROM test limit 1");
            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getInt(1), 100);
            statement.executeQuery("DROP TABLE IF EXISTS test");
        }, true);
    }

}
