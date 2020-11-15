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

package com.github.housepower.jdbc.exception;

/**
 * <p> Use {@link ClickHouseException} internal, wrapped with {@link java.sql.SQLException} only on JDBC interfaces.
 * throw unchecked exception rather than checked exception.
 * <p> Please avoid using CheckedException internal. See detail at <a>https://www.artima.com/intv/handcuffs.html</a>
 */
public abstract class ClickHouseException extends RuntimeException {

    /**
     * {@inheritDoc}
     */
    public ClickHouseException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public ClickHouseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public ClickHouseException(Throwable cause) {
        super(cause);
    }
}
