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
import com.github.housepower.jdbc.statement.ClickHouseStatement;
import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

public class FailoverConnectionProxy extends MultiHostConnectionProxy {
    private static final Logger LOG = LoggerFactory.getLogger(FailoverConnectionProxy.class);

    private static final int NO_CONNECTION_INDEX = -1;

    private int currentHostIndex = NO_CONNECTION_INDEX;

    private final int retriesAllDown;

    /**
     * Proxy class to intercept and deal with errors that may occur in any object bound to the current connection.
     * Additionally intercepts query executions and triggers an execution count on the outer class.
     */
    class FailoverJdbcInterfaceProxy extends JdbcInterfaceProxy {
        FailoverJdbcInterfaceProxy(Object toInvokeOn, FailoverConnectionProxy failoverConnectionProxy) {
            super(toInvokeOn, failoverConnectionProxy);
        }

        @SuppressWarnings("synthetic-access")
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();

            boolean isExecute = methodName.startsWith("execute");
            int tryIndex = currentHostIndex;
            Throwable t;

            do {
                tryIndex = currentHostIndex;
                try {
                    return super.invoke(proxy, method, args);
                } catch (Throwable e) {
                    t = e;

                    for (Class<?> in : proxy.getClass().getInterfaces()) {
                        if (Statement.class.isAssignableFrom(in)) {
                            super.invoke(proxy, ClickHouseStatement.class.getMethod("resetConnect", ClickHouseConnection.class, NativeContext.class), new Object[]{multiHostConnectionProxy.currentConnection, multiHostConnectionProxy.currentConnection.nativeContext()});
                            break;
                        }
                    }
                }
            }
            while (!currentConnection.isClosed() && isExecute && tryIndex != urlsList.size() - 1);

            throw t;
        }
    }

    @Override
    boolean shouldExceptionTriggerConnectionSwitch(Throwable t) {
        return t instanceof SQLException && this.retriesAllDown - 1 != this.currentHostIndex;
    }

    /*
     * Local implementation for the new connection picker.
     */
    @Override
    synchronized void pickNewConnection() throws SQLException {
        if (this.isClosed) {
            return;
        }

        failOver();
    }

    @Override
    void doClose() throws SQLException {
        this.currentConnection.close();
    }

    @Override
    void doAbort(Executor executor) throws SQLException {
        this.currentConnection.abort(executor);
    }

    @Override
    Object invokeMore(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;

        try {
            result = method.invoke(this.currentConnection, args);
            result = proxyIfReturnTypeIsJdbcInterface(method.getReturnType(), result);
        } catch (InvocationTargetException e) {
            dealWithInvocationException(e);
        }

        return result;
    }

    /**
     * Initiates a default failover procedure starting at the current connection host index.
     *
     * @throws SQLException if an error occurs
     */
    private synchronized void failOver() throws SQLException {
        failOver(this.currentHostIndex);
    }

    /**
     * Initiates a default failover procedure starting at the given host index.
     * This process tries to connect, sequentially, to the next host in the list. The primary host may or may not be excluded from the connection attempts.
     *
     * @param failedHostIdx The host index where to start from. First connection attempt will be the next one.
     * @throws SQLException if an error occurs
     */
    private synchronized void failOver(int failedHostIdx) throws SQLException {

        int nextHostIndex = nextHost(failedHostIdx);
        int firstHostIndexTried = nextHostIndex;

        SQLException lastExceptionCaught = null;
        int attempts = 0;
        boolean gotConnection = false;

        do {
            try {
                connectTo(nextHostIndex);
                gotConnection = true;
            } catch (SQLException e) {
                lastExceptionCaught = e;

                if (shouldExceptionTriggerConnectionSwitch(e)) {
                    int newNextHostIndex = nextHost(nextHostIndex);

                    if (newNextHostIndex == firstHostIndexTried && newNextHostIndex == (newNextHostIndex = nextHost(nextHostIndex))) { // Full turn
                        attempts++;

                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException ignore) {
                        }
                    }

                    nextHostIndex = newNextHostIndex;

                } else {
                    throw e;
                }
            }
        } while (attempts < this.retriesAllDown && !gotConnection);

        if (!gotConnection) {
            throw lastExceptionCaught;
        }
    }

    private int nextHost(int currHostIdx) {
        return (currHostIdx + 1) % this.urlsList.size();
    }

    /**
     * Connects this dynamic failover connection proxy to the host pointed out by the given host index.
     *
     * @param hostIndex The host index in the global hosts list.
     * @throws SQLException if an error occurs
     */
    private synchronized void connectTo(int hostIndex) throws SQLException {
        try {
            switchCurrentConnectionTo(hostIndex, createConnectionForHostIndex(hostIndex));
        } catch (SQLException e) {
            if (this.currentConnection != null) {
                String msg = "Connection to " + " url '" +
                        this.urlsList.get(hostIndex) + "' failed";
                LOG.error(msg);
            }
            throw e;
        }
    }

    /**
     * Replaces the previous underlying connection by the connection given. State from previous connection, if any, is synchronized with the new one.
     *
     * @param hostIndex  The host index in the global hosts list that matches the given connection.
     * @param connection The connection instance to switch to.
     */
    private synchronized void switchCurrentConnectionTo(int hostIndex, ClickHouseConnection connection) {
        invalidateCurrentConnection();
        this.currentConnection = connection;
        this.currentHostIndex = hostIndex;
    }

    /**
     * Creates a new connection instance for host pointed out by the given host index.
     *
     * @param hostIndex The host index in the global hosts list.
     * @return The new connection instance.
     * @throws SQLException if an error occurs
     */
    synchronized ClickHouseConnection createConnectionForHostIndex(int hostIndex) throws SQLException {
        return createConnectionForHost(this.urlsList.get(hostIndex));
    }

    public static Connection createProxyInstance(List<String> urls, Properties properties) throws SQLException {
        FailoverConnectionProxy connProxy = new FailoverConnectionProxy(urls, properties);

        return (Connection) java.lang.reflect.Proxy.newProxyInstance(ClickHouseConnection.class.getClassLoader(), new Class<?>[]{Connection.class},
                connProxy);
    }

    /**
     * Instantiates a new FailoverConnectionProxy for the given list of hosts and connection properties.
     *
     * @param urls       The connection URL that initialized this multi-host connection.
     * @param properties The connection properties.
     * @throws SQLException if an error occurs
     */
    private FailoverConnectionProxy(List<String> urls, Properties properties) throws SQLException {
        super(urls, properties);
        this.retriesAllDown = urls.size();
        pickNewConnection();
    }

    /**
     * Gets locally bound instances of FailoverJdbcInterfaceProxy.
     */
    @Override
    JdbcInterfaceProxy getNewJdbcInterfaceProxy(Object toProxy) {
        return new FailoverJdbcInterfaceProxy(toProxy, this);
    }

}
