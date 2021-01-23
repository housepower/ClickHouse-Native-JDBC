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

package com.github.housepower.jdbc;

import com.github.housepower.client.NativeContext;
import com.github.housepower.misc.Validate;
import com.github.housepower.settings.ClickHouseConfig;
import com.github.housepower.settings.ClickHouseDefines;
import com.github.housepower.stream.QueryResult;
import com.github.housepower.stream.QueryResultBuilder;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ClickHouseResultSetBuilder {

    private final QueryResultBuilder queryResultBuilder;

    private ClickHouseConfig cfg;
    private String db = ClickHouseDefines.DEFAULT_DATABASE;
    private String table = "unknown";

    public static ClickHouseResultSetBuilder builder(int columnsNum, NativeContext.ServerContext serverContext) {
        return new ClickHouseResultSetBuilder(QueryResultBuilder.builder(columnsNum, serverContext));
    }

    private ClickHouseResultSetBuilder(QueryResultBuilder queryResultBuilder) {
        this.queryResultBuilder = queryResultBuilder;
    }

    public ClickHouseResultSetBuilder cfg(ClickHouseConfig cfg) {
        this.cfg = cfg;
        return this;
    }

    public ClickHouseResultSetBuilder db(String db) {
        this.db = db;
        return this;
    }

    public ClickHouseResultSetBuilder table(String table) {
        this.table = table;
        return this;
    }

    public ClickHouseResultSetBuilder columnNames(String... names) {
        return columnNames(Arrays.asList(names));
    }

    public ClickHouseResultSetBuilder columnNames(List<String> names) {
        this.queryResultBuilder.columnNames(names);
        return this;
    }

    public ClickHouseResultSetBuilder columnTypes(String... types) throws SQLException {
        return columnTypes(Arrays.asList(types));
    }

    public ClickHouseResultSetBuilder columnTypes(List<String> types) throws SQLException {
        this.queryResultBuilder.columnTypes(types);
        return this;
    }

    public ClickHouseResultSetBuilder addRow(Object... row) {
        return addRow(Arrays.asList(row));
    }

    public ClickHouseResultSetBuilder addRow(List<?> row) {
        this.queryResultBuilder.addRow(row);
        return this;
    }

    public ClickHouseResultSet build() throws SQLException {
        Validate.ensure(cfg != null);
        QueryResult queryResult = this.queryResultBuilder.build();
        return new ClickHouseResultSet(null, cfg, db, table, queryResult.header(), queryResult.data());
    }
}
