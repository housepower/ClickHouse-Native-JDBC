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

package com.github.housepower.jdbc.statement;


import com.github.housepower.jdbc.ClickHouseConnection;
import com.github.housepower.jdbc.connect.NativeContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;

public class ClickHousePreparedQueryStatement extends AbstractPreparedStatement {

    public ClickHousePreparedQueryStatement(ClickHouseConnection conn, NativeContext nativeContext, String query) {
        this(conn, nativeContext, splitQueryByQuestionMark(query));
    }

    private ClickHousePreparedQueryStatement(ClickHouseConnection conn, NativeContext nativeContext, String[] parts) {
        super(conn, nativeContext, parts);
    }

    @Override
    public boolean execute() throws SQLException {
        return execute(assembleQueryPartsAndParameters());
    }

    @Override
    public int executeUpdate() throws SQLException {
        return executeUpdate(assembleQueryPartsAndParameters());
    }

    @Override
    public int[] executeBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException("");
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return executeQuery(assembleQueryPartsAndParameters());
    }

    @Override
    public void setObject(int index, Object x) throws SQLException {
        parameters[index - 1] = x;
    }

    private static String[] splitQueryByQuestionMark(String query) {
        int lastPos = 0;
        List<String> queryParts = new ArrayList<String>();
        boolean inQuotes = false, inBackQuotes = false;
        for (int i = 0; i < query.length(); i++) {
            char ch = query.charAt(i);
            if (ch == '`') {
                inBackQuotes = !inBackQuotes;
            } else if (ch == '\'') {
                inQuotes = !inQuotes;
            } else if (!inBackQuotes && !inQuotes) {
                if (ch == '?') {
                    queryParts.add(query.substring(lastPos, i));
                    lastPos = i + 1;
                }
            }
        }
        queryParts.add(query.substring(lastPos));
        return queryParts.toArray(new String[0]);
    }

    public String toString() {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(super.toString());
        try {
            queryBuilder.append(": ");
            queryBuilder.append(assembleQueryPartsAndParameters());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryBuilder.toString();
    }
}
