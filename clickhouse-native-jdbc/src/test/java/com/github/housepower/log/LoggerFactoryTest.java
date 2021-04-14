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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoggerFactoryTest {

    @Test
    void test() {
        LoggerFactoryAdaptor originalAdaptor = LoggerFactory.currentAdaptor();

        LoggerFactory.setAdaptor(new JdkLoggerFactoryAdaptor());
        Logger logger1 = LoggerFactory.getLogger("haha");
        assertEquals("haha", logger1.getName());
        assertEquals(JdkLogger.class, logger1.getClass());
        Logger logger2 = LoggerFactory.getLogger(LoggerFactoryTest.class);
        assertEquals(LoggerFactoryTest.class.getName(), logger2.getName());
        assertEquals(JdkLogger.class, logger2.getClass());

        LoggerFactory.setAdaptor(new Slf4jLoggerFactoryAdaptor());
        Logger logger3 = LoggerFactory.getLogger("haha");
        assertEquals("haha", logger3.getName());
        assertEquals(Slf4jLogger.class, logger3.getClass());
        Logger logger4 = LoggerFactory.getLogger(LoggerFactoryTest.class);
        assertEquals(LoggerFactoryTest.class.getName(), logger4.getName());
        assertEquals(Slf4jLogger.class, logger4.getClass());

        LoggerFactory.setAdaptor(originalAdaptor);
    }
}
