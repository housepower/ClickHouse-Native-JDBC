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


import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.github.housepower.settings.ClickHouseConfig;
import com.github.housepower.util.Util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * An abstract class that processes generic multi-host configurations. This class has to be sub-classed by specific multi-host implementations, such as
 * load-balancing and failover.
 */
public abstract class MultiHostConnectionProxy implements InvocationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MultiHostConnectionProxy.class);

    private static final String METHOD_EQUALS = "equals";
    private static final String METHOD_CLOSE = "close";
    private static final String METHOD_ABORT = "abort";
    private static final String METHOD_IS_CLOSED = "isClosed";

    List<String> urlsList;
    boolean isClosed = false;

    protected Properties properties;

    ClickHouseConnection currentConnection = null;

    // Keep track of the last exception processed in 'dealWithInvocationException()' in order to avoid creating connections repeatedly from each time the same
    // exception is caught in every proxy instance belonging to the same call stack.
    protected Throwable lastExceptionDealtWith = null;

    /**
     * Proxy class to intercept and deal with errors that may occur in any object bound to the current connection.
     */
    class JdbcInterfaceProxy implements InvocationHandler {
        Object invokeOn;
        MultiHostConnectionProxy multiHostConnectionProxy;

        JdbcInterfaceProxy(Object toInvokeOn, MultiHostConnectionProxy multiHostConnectionProxy) {
            this.invokeOn = toInvokeOn;
            this.multiHostConnectionProxy = multiHostConnectionProxy;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (METHOD_EQUALS.equals(method.getName())) {
                // Let args[0] "unwrap" to its InvocationHandler if it is a proxy.
                return args[0].equals(this);
            }

            synchronized (MultiHostConnectionProxy.this) {
                Object result = null;

                try {
                    result = method.invoke(this.invokeOn, args);
                    result = proxyIfReturnTypeIsJdbcInterface(method.getReturnType(), result);
                } catch (InvocationTargetException e) {
                    dealWithInvocationException(e);
                }

                return result;
            }
        }
    }

    /**
     * If the given return type is or implements a JDBC interface, proxies the given object so that we can catch SQL errors and fire a connection switch.
     *
     * @param returnType The type the object instance to proxy is supposed to be.
     * @param toProxy    The object instance to proxy.
     * @return The proxied object or the original one if it does not implement a JDBC interface.
     */
    Object proxyIfReturnTypeIsJdbcInterface(Class<?> returnType, Object toProxy) {
        if (toProxy != null) {
            if (Util.isJdbcInterface(returnType)) {
                Class<?> toProxyClass = toProxy.getClass();
                return Proxy.newProxyInstance(toProxyClass.getClassLoader(), Util.getImplementedInterfaces(toProxyClass), getNewJdbcInterfaceProxy(toProxy));
            }
        }
        return toProxy;
    }

    /**
     * Instantiates a new JdbcInterfaceProxy for the given object. Subclasses can override this to return instances of JdbcInterfaceProxy subclasses.
     *
     * @param toProxy The object instance to be proxied.
     * @return The new InvocationHandler instance.
     */
    InvocationHandler getNewJdbcInterfaceProxy(Object toProxy) {
        return new JdbcInterfaceProxy(toProxy, this);
    }

    /**
     * Deals with InvocationException from proxied objects.
     *
     * @param e The Exception instance to check.
     * @throws SQLException              if an error occurs
     * @throws Throwable                 if an error occurs
     * @throws InvocationTargetException if an error occurs
     */
    void dealWithInvocationException(InvocationTargetException e) throws SQLException, Throwable, InvocationTargetException {
        Throwable t = e.getTargetException();

        if (t != null) {
            if (this.lastExceptionDealtWith != t && shouldExceptionTriggerConnectionSwitch(t)) {
                invalidateCurrentConnection();
                pickNewConnection();
                this.lastExceptionDealtWith = t;
            }
            throw t;
        }
        throw e;
    }

    /**
     * Checks if the given throwable should trigger a connection switch.
     *
     * @param t The Throwable instance to analyze.
     * @return true if the given throwable should trigger a connection switch
     */
    abstract boolean shouldExceptionTriggerConnectionSwitch(Throwable t);

    /**
     * Invalidates the current connection.
     *
     */
    synchronized void invalidateCurrentConnection() {
        invalidateConnection(this.currentConnection);
    }

    /**
     * Invalidates the specified connection by closing it.
     *
     * @param conn The connection instance to invalidate.
     */
    synchronized void invalidateConnection(ClickHouseConnection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException ignore) {
            // swallow this exception, current connection should be useless anyway.
        }
    }

    /**
     * Picks the "best" connection to use from now on. Each subclass needs to implement its connection switch strategy on it.
     *
     * @throws SQLException if an error occurs
     */
    abstract void pickNewConnection() throws SQLException;

    /**
     * Proxies method invocation on the java.sql.Connection interface, trapping multi-host specific methods and generic methods.
     * Subclasses have to override this to complete the method invocation process, deal with exceptions and decide when to switch connection.
     * To avoid unnecessary additional exception handling overriders should consult #canDealWith(Method) before chaining here.
     *
     * @param proxy  proxy object
     * @param method method to invoke
     * @param args   method parameters
     * @return method result
     * @throws Throwable if an error occurs
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();

        if (METHOD_EQUALS.equals(methodName)) {
            // Let args[0] "unwrap" to its InvocationHandler if it is a proxy.
            return args[0].equals(this);
        }

        // Execute remaining ubiquitous methods right away.
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(this, args);
        }

        synchronized (this) {
            if (METHOD_CLOSE.equals(methodName)) {
                doClose();
                this.isClosed = true;
                return null;
            }

            if (METHOD_ABORT.equals(methodName) && args.length == 1) { // TODO
                this.isClosed = true;
                doAbort((Executor) args[0]);
                return null;
            }

            if (METHOD_IS_CLOSED.equals(methodName)) {
                return this.isClosed;
            }

            try {
                return invokeMore(proxy, method, args);
            } catch (InvocationTargetException e) {
                throw e.getCause() != null ? e.getCause() : e;
            } catch (Exception e) {
                // Check if the captured exception must be wrapped by an unchecked exception.
                Class<?>[] declaredException = method.getExceptionTypes();
                for (Class<?> declEx : declaredException) {
                    if (declEx.isAssignableFrom(e.getClass())) {
                        throw e;
                    }
                }
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    /**
     * Executes a close() invocation;
     *
     * @throws SQLException if an error occurs
     */
    abstract void doClose() throws SQLException;

    /**
     * Executes a abort() invocation;
     *
     * @param executor executor
     * @throws SQLException if an error occurs
     */
    abstract void doAbort(Executor executor) throws SQLException;

    /**
     * Continuation of the method invocation process, to be implemented within each subclass.
     *
     * @param proxy  proxy object
     * @param method method to invoke
     * @param args   method parameters
     * @return method result
     * @throws Throwable if an error occurs
     */
    abstract Object invokeMore(Object proxy, Method method, Object[] args) throws Throwable;


    /**
     * Constructs a MultiHostConnectionProxy instance for the given connection URL.
     *
     * @param urls       The connection URL that initialized this multi-host connection.
     * @param properties The connection properties.
     */
    MultiHostConnectionProxy(List<String> urls, Properties properties) {
        initializeHostsSpecs(urls, properties);
    }

    /**
     * Initializes the hosts lists and makes a "clean" local copy of the given connection properties so that it can be later used to create standard
     * connections.
     *
     * @param urls       The connection URL that initialized this multi-host connection.
     * @param properties The connection properties.
     */
    void initializeHostsSpecs(List<String> urls, Properties properties) {
        this.properties = properties;
        this.urlsList = urls;
    }

    /**
     * Creates a new physical connection for the given
     *
     * @param url The url info instance.
     * @return The new Connection instance.
     * @throws SQLException if an error occurs
     */
    synchronized ClickHouseConnection createConnectionForHost(String url) throws SQLException {
        LOG.debug("Create connect.Url is {}", url);
        ClickHouseConfig cfg = ClickHouseConfig.Builder.builder()
                .withJdbcUrl(url)
                .withProperties(this.properties)
                .build();
        return ClickHouseConnection.createClickHouseConnection(cfg.withJdbcUrl(url));
    }

}
