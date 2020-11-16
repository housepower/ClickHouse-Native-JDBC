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

package examples;

import com.github.housepower.jdbc.BalancedClickhouseDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;

public class HikariCpITest extends DataSourceITest {

    @BeforeEach
    public void reset() throws SQLException {
        resetDriverManager();
    }

    @Test
    public void testHikariDataSource() throws Exception {
        HikariConfig conf = new HikariConfig();
        conf.setJdbcUrl(getJdbcUrl());
        conf.setDriverClassName(DRIVER_CLASS_NAME);
        try (HikariDataSource ds = new HikariDataSource(conf)) {
            runSql(ds);
        }
    }

    @Test
    public void testHikariWrappedDataSource() throws Exception {
        DataSource balancedCkDs = new BalancedClickhouseDataSource(getJdbcUrl());
        HikariConfig conf = new HikariConfig();
        conf.setDataSource(balancedCkDs);
        try (HikariDataSource ds = new HikariDataSource(conf)) {
            runSql(ds);
        }
    }
}
