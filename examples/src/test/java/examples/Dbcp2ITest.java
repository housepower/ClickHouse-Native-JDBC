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

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Properties;

public class Dbcp2ITest extends DataSourceITest {

    @BeforeEach
    public void reset() throws SQLException {
        resetDriverManager();
    }

    @Test
    public void testDbcpBasicDataSource() throws Exception {
        Properties prop = new Properties();
        prop.setProperty("url", getJdbcUrl());
        prop.setProperty("driverClassName", DRIVER_CLASS_NAME);
        try (BasicDataSource ds = BasicDataSourceFactory.createDataSource(prop)) {
            runSql(ds);
        }
    }
}
