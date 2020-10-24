package com.github.housepower.jdbc;

import com.google.common.base.Strings;

import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.*;

/**
 *
 */
public class IssueReproduceITest extends AbstractITest {
    @Test
    public void testIssue63() throws Exception {
        withNewConnection(connection -> {
            int columnNum = 36;
            Statement statement = connection.createStatement();
            statement.executeQuery("DROP TABLE IF EXISTS test");
            String params = Strings.repeat("?, ", columnNum);
            StringBuilder columnTypes = new StringBuilder();
            for (int i = 0; i < columnNum; i++) {
                if (i != 0) {
                    columnTypes.append(", ");
                }
                columnTypes.append("t_").append(i).append(" String");
            }

            statement.executeQuery("CREATE TABLE test( " + columnTypes + ")ENGINE=Log");
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO test values(" + params.substring(0, params.length() - 2) + ")");

            for (int i = 0; i < 100; ++i) {
                for (int j = 0; j < columnNum; j++) {
                    pstmt.setString(j + 1, "String" + j);
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();

            ResultSet rs = statement.executeQuery("SELECT count(1) FROM test limit 1");
            assertTrue(rs.next());
            assertEquals(100, rs.getInt(1));
            statement.executeQuery("DROP TABLE IF EXISTS test");
        }, true);
    }

}
