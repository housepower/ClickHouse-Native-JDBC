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

import java.util.Collections;
import java.util.List;

public class ConnectionUrl {
    private final Type type;
    private final List<String> singleConnectUrls;

    /**
     * The rules describing the number of hosts a database URL may contain.
     */
    public enum HostsCardinality {
        SINGLE {
            @Override
            public boolean assertSize(int n) {
                return n == 1;
            }
        },
        MULTIPLE {
            @Override
            public boolean assertSize(int n) {
                return n > 1;
            }
        },
        ONE_OR_MORE {
            @Override
            public boolean assertSize(int n) {
                return n >= 1;
            }
        };

        public abstract boolean assertSize(int n);
    }

    /**
     * The database URL type which is determined by the scheme section of the connection string.
     */
    public enum Type {
        // Standard schemes:
        SINGLE_CONNECTION("jdbc:clickhouse:", HostsCardinality.SINGLE), //
        FAILOVER_CONNECTION("jdbc:clickhouse:", HostsCardinality.MULTIPLE);

        private final String scheme;
        private final HostsCardinality cardinality;

        Type(String scheme, HostsCardinality cardinality) {
            this.scheme = scheme;
            this.cardinality = cardinality;
        }

        public String getScheme() {
            return this.scheme;
        }

        public HostsCardinality getCardinality() {
            return this.cardinality;
        }

        /**
         * Returns the {@link Type} corresponding to the given scheme and number of hosts, if any.
         * Calling this method with the argument n lower than 0 skips the hosts cardinality validation.
         *
         * @param scheme one of supported schemes
         * @param n      the number of hosts in the database URL
         * @return the {@link Type} corresponding to the given protocol and number of hosts
         */
        public static Type fromValue(String scheme, int n) {
            for (Type t : values()) {
                if (t.getScheme().equalsIgnoreCase(scheme) && (n < 0 || t.getCardinality().assertSize(n))) {
                    return t;
                }
            }

            return Type.SINGLE_CONNECTION;
        }

        public static Type getConnectionType(ClickhouseJdbcUrlParser parser) {
            return fromValue(parser.getScheme(), parser.getHosts().size());
        }
    }

    public ConnectionUrl(String connString) {
        ClickhouseJdbcUrlParser parser = ClickhouseJdbcUrlParser.parseConnectionString(connString);

        this.singleConnectUrls = parser.getSingleHostJdbcUrls();
        this.type = Type.getConnectionType(parser);
    }

    public Type getType() {
        return type;
    }

    public List<String> getSingleConnectUrls() {
        return Collections.unmodifiableList(singleConnectUrls);
    }

}
