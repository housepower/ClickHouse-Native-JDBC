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

package com.github.housepower.log;

public interface Logger {

    String getName();

    boolean isTraceEnabled();

    void trace(String format, Object... arguments);

    void trace(String msg, Throwable t);

    boolean isDebugEnabled();

    void debug(String format, Object... arguments);

    void debug(String msg, Throwable t);

    boolean isInfoEnabled();

    void info(String format, Object... arguments);

    void info(String msg, Throwable t);

    boolean isWarnEnabled();

    void warn(String format, Object... arguments);

    void warn(String msg, Throwable t);

    boolean isErrorEnabled();

    void error(String format, Object... arguments);

    void error(String msg, Throwable t);
}
