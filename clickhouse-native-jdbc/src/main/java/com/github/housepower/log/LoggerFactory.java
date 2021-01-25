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

public class LoggerFactory {

    private static LoggerFactoryAdaptor adaptor;

    static {
        try {
            if (org.slf4j.LoggerFactory.getILoggerFactory() != null) {
                adaptor = new Slf4jLoggerFactoryAdaptor();
            }
        } catch (Throwable ignore) {
            adaptor = new JdkLoggerFactoryAdaptor();
        }
    }

    public static Logger getLogger(Class<?> clazz) {
        return adaptor.getLogger(clazz);
    }

    public static Logger getLogger(String name) {
        return adaptor.getLogger(name);
    }

    public static LoggerFactoryAdaptor currentAdaptor() {
        return adaptor;
    }

    // for testing
    public static void setAdaptor(LoggerFactoryAdaptor adaptor) {
        LoggerFactory.adaptor = adaptor;
    }

    private LoggerFactory() {
    }
}
