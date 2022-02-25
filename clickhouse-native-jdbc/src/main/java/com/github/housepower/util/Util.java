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
package com.github.housepower.util;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Util {
    /** Cache for the JDBC interfaces already verified */
    private static final ConcurrentMap<Class<?>, Boolean> isJdbcInterfaceCache = new ConcurrentHashMap<>();

    /**
     * Recursively checks for interfaces on the given class to determine if it implements a java.sql, javax.sql or com.github.housepower.jdbc interface.
     *
     * @param clazz
     *            The class to investigate.
     * @return boolean
     */
    public static boolean isJdbcInterface(Class<?> clazz) {
        if (Util.isJdbcInterfaceCache.containsKey(clazz)) {
            return (Util.isJdbcInterfaceCache.get(clazz));
        }

        if (clazz.isInterface()) {
            try {
                if (isJdbcPackage(clazz.getPackage().getName())) {
                    Util.isJdbcInterfaceCache.putIfAbsent(clazz, true);
                    return true;
                }
            } catch (Exception ex) {
                /*
                 * We may experience a NPE from getPackage() returning null, or class-loading facilities.
                 * This happens when this class is instrumented to implement runtime-generated interfaces.
                 */
            }
        }

        for (Class<?> iface : clazz.getInterfaces()) {
            if (isJdbcInterface(iface)) {
                Util.isJdbcInterfaceCache.putIfAbsent(clazz, true);
                return true;
            }
        }

        if (clazz.getSuperclass() != null && isJdbcInterface(clazz.getSuperclass())) {
            Util.isJdbcInterfaceCache.putIfAbsent(clazz, true);
            return true;
        }

        Util.isJdbcInterfaceCache.putIfAbsent(clazz, false);
        return false;
    }

    /**
     * Check if the package name is a known JDBC package.
     *
     * @param packageName
     *            The package name to check.
     * @return boolean
     */
    public static boolean isJdbcPackage(String packageName) {
        return packageName != null
                && (packageName.startsWith("java.sql") || packageName.startsWith("javax.sql") || packageName.startsWith("com.github.housepower.jdbc"));
    }

    /** Cache for the implemented interfaces searched. */
    private static final ConcurrentMap<Class<?>, Class<?>[]> implementedInterfacesCache = new ConcurrentHashMap<>();


    /**
     * Retrieves a list with all interfaces implemented by the given class. If possible gets this information from a cache instead of navigating through the
     * object hierarchy. Results are stored in a cache for future reference.
     *
     * @param clazz
     *            The class from which the interface list will be retrieved.
     * @return
     *         An array with all the interfaces for the given class.
     */
    public static Class<?>[] getImplementedInterfaces(Class<?> clazz) {
        Class<?>[] implementedInterfaces = Util.implementedInterfacesCache.get(clazz);
        if (implementedInterfaces != null) {
            return implementedInterfaces;
        }

        Set<Class<?>> interfaces = new LinkedHashSet<>();
        Class<?> superClass = clazz;
        do {
            Collections.addAll(interfaces, superClass.getInterfaces());
        } while ((superClass = superClass.getSuperclass()) != null);

        implementedInterfaces = interfaces.toArray(new Class<?>[interfaces.size()]);
        Class<?>[] oldValue = Util.implementedInterfacesCache.putIfAbsent(clazz, implementedInterfaces);
        if (oldValue != null) {
            implementedInterfaces = oldValue;
        }

        return implementedInterfaces;
    }
}
