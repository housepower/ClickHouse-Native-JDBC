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

import javax.annotation.Nullable;

public class StrUtil {

    /**
     * Return a default string if given string is null or empty.
     *
     * @param origin       origin string
     * @param defaultValue default value
     * @return if origin == null || origin.isEmpty, return defaultValue, else return origin
     */
    public static String getOrDefault(@Nullable String origin, String defaultValue) {
        if (origin == null || origin.isEmpty())
            return defaultValue;
        return origin;
    }

    public static String toString(@Nullable Object object) {
        return object == null ? "" : object.toString();
    }

    public static boolean isEmpty(@Nullable String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isBlank(@Nullable String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(@Nullable String str) {
        return !isEmpty(str);
    }

    public static boolean isNotBlank(@Nullable String str) {
        return !isBlank(str);
    }
}
