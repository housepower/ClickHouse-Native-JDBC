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

import java.util.ResourceBundle;

public class JdkLogger implements Logger {

    private static final Object[] EMPTY_ARRAY = new Object[]{};

    private final java.util.logging.Logger logger;

    public JdkLogger(java.util.logging.Logger logger) {
        this.logger = logger;
    }

    @Override
    public String getName() {
        return this.logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return this.logger.isLoggable(java.util.logging.Level.FINEST);
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (logger.isLoggable(java.util.logging.Level.FINEST)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
            doLog(java.util.logging.Level.FINEST, ft);
        }
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (logger.isLoggable(java.util.logging.Level.FINEST)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(msg, EMPTY_ARRAY, t);
            doLog(java.util.logging.Level.FINEST, ft);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return this.logger.isLoggable(java.util.logging.Level.FINE);
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (logger.isLoggable(java.util.logging.Level.FINE)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
            doLog(java.util.logging.Level.FINE, ft);
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (logger.isLoggable(java.util.logging.Level.FINE)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(msg, EMPTY_ARRAY, t);
            doLog(java.util.logging.Level.FINE, ft);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return this.logger.isLoggable(java.util.logging.Level.INFO);
    }

    @Override
    public void info(String format, Object... arguments) {
        if (logger.isLoggable(java.util.logging.Level.INFO)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
            doLog(java.util.logging.Level.INFO, ft);
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        if (logger.isLoggable(java.util.logging.Level.INFO)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(msg, EMPTY_ARRAY, t);
            doLog(java.util.logging.Level.INFO, ft);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return this.logger.isLoggable(java.util.logging.Level.WARNING);
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (logger.isLoggable(java.util.logging.Level.WARNING)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
            doLog(java.util.logging.Level.WARNING, ft);
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (logger.isLoggable(java.util.logging.Level.WARNING)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(msg, EMPTY_ARRAY, t);
            doLog(java.util.logging.Level.WARNING, ft);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return this.logger.isLoggable(java.util.logging.Level.SEVERE);
    }

    @Override
    public void error(String format, Object... arguments) {
        if (logger.isLoggable(java.util.logging.Level.SEVERE)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
            doLog(java.util.logging.Level.SEVERE, ft);
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        if (logger.isLoggable(java.util.logging.Level.SEVERE)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(msg, EMPTY_ARRAY, t);
            doLog(java.util.logging.Level.SEVERE, ft);
        }
    }

    private void doLog(java.util.logging.Level level, FormattingTuple ft) {
        logger.logrb(level, null, null, (ResourceBundle) null, ft.getMessage(), ft.getThrowable());
    }
}
