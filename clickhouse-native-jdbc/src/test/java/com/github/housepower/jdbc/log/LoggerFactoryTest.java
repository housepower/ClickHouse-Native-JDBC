package com.github.housepower.jdbc.log;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
