package examples;

import com.google.common.base.Joiner;

import com.github.housepower.jdbc.ClickHouseDriver;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import ru.yandex.clickhouse.ClickHouseConnectionImpl;
import ru.yandex.clickhouse.ClickHouseDataSource;

/**
 *
 */
public class TestFetchData {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try {
            List<ThreadTestFetchData> threads = new ArrayList<ThreadTestFetchData>();
            for (int i = 0; i < 10; i++) {
                ThreadTestFetchData t = new ThreadTestFetchData(args[0], Long.parseLong(args[1]), Integer.parseInt(args[2]));
                threads.add(t);
                t.start();
            }
            for (ThreadTestFetchData t: threads) {
                t.join();
            }
        } catch (Exception e) {

        }
        long end = System.currentTimeMillis() - start;
        System.out.println( "ARGS [" +  Joiner.on(" ").join(args) +   "] costs" + end);
    }
}


class ClickHouseJDBCUtil {
    public static ClickHouseConnectionImpl getConnectionJDBCHTTP() throws SQLException {
        ClickHouseDataSource
            dataSource = new ClickHouseDataSource("jdbc:clickhouse://127.0.0.1:8123/default?keepAliveTimeout=300000&socket_timeout=300000&dataTransferTimeout=300000");
        ClickHouseConnectionImpl connection = (ClickHouseConnectionImpl) dataSource.getConnection();
        return connection;
    }

    public static Connection getConnectionJDBCNative() throws SQLException {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            DriverManager.deregisterDriver(drivers.nextElement());
        }
        DriverManager.registerDriver(new ClickHouseDriver());
        Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000");
        return connection;
    }
}

class ThreadTestFetchData extends Thread {
    private String mode;
    private long number;
    private int read;
    ThreadTestFetchData(String mode, long number, int read) {
        this.mode = mode;
        this.number = number;
        this.read = read;
    }

    public void run() {
        try {
            Connection connection;
            if (mode.equalsIgnoreCase("native")) {
                connection = ClickHouseJDBCUtil.getConnectionJDBCNative();
            } else {
                connection = ClickHouseJDBCUtil.getConnectionJDBCHTTP();
            }

            for (int i = 0; i < 10; i++) {
                test1(connection);
            }
            System.out.println("done");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test1(Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement("select * from numbers(?)");
        p.setLong(1, number);
        ResultSet rs = p.executeQuery();

        if (read > 0) {
            long sum = 0;
            while (rs.next()) {
                sum += rs.getInt(1);
            }
        }

    }
}
