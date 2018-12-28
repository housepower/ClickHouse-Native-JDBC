package examples;

import java.sql.*;

/**
 */
public class MetaDataQuery {

    public static void main(String[] args) throws Exception {
        Class.forName("com.github.housepower.jdbc.ClickHouseDriver");
        Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000");
        DatabaseMetaData md = connection.getMetaData();
        ResultSet tables = md.getTables(null, null, null, null);
        while (tables.next()){
            System.out.println(String.format("%s %s %s %s",
                    tables.getString(1),
                    tables.getString(2),
                    tables.getString(3),
                    tables.getString(4)
                    ));
        }
    }
}
