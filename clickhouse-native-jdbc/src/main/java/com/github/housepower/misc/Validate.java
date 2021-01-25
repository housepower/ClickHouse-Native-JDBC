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

package com.github.housepower.misc;

import com.github.housepower.exception.InvalidValueException;

import javax.annotation.Nullable;
import java.sql.SQLException;

public class Validate {

    // We need to migrate from #isTrue to #ensure to avoid throwing checked exception internal.
    public static void ensure(boolean expr) {
        ensure(expr, "");
    }

    public static void ensure(boolean expr, String message) {
        if (!expr) {
            throw new InvalidValueException(message);
        }
    }

    public static void isTrue(boolean expression) throws SQLException {
        isTrue(expression, null);
    }

    public static void isTrue(boolean expression, @Nullable String message) throws SQLException {
        if (!expression) {
            throw new SQLException(message);
        }
    }
}
