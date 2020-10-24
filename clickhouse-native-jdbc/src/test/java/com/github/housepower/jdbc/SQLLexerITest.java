package com.github.housepower.jdbc;

import com.github.housepower.jdbc.misc.SQLLexer;

import org.junit.Test;

import static org.junit.Assert.*;

public class SQLLexerITest extends AbstractITest {

    @Test
    public void successfullyNumber() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "123");
        assertEquals(123L, sqlLexer.numberLiteral());
    }

    @Test
    public void successfullyNegativeNumber() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "-123");
        assertEquals(-123L, sqlLexer.numberLiteral());
    }

    @Test
    public void successfullyDoubleNumber() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "-123.0 3E2");
        assertEquals(-123.0, sqlLexer.numberLiteral());
        assertEquals(300.0, sqlLexer.numberLiteral());
    }

    @Test
    public void successfullyNumberInHexa() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "-0xfp2 0xfp2 0xfp1 0xfp0 0xf -0xf");
        assertEquals(-60.0, sqlLexer.numberLiteral());
        assertEquals(60.0d, sqlLexer.numberLiteral());
        assertEquals(30.0, sqlLexer.numberLiteral());
        assertEquals(15.0, sqlLexer.numberLiteral());
        assertEquals(15L, sqlLexer.numberLiteral());
        assertEquals(-15L, sqlLexer.numberLiteral());
    }

    @Test
    public void successfullyNumberInBinary() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "0b111 -0b111 +0b111");
        assertEquals(7L, sqlLexer.numberLiteral());
        assertEquals(-7L, sqlLexer.numberLiteral());
        assertEquals(7L, sqlLexer.numberLiteral());
    }

    @Test
    public void successfullyLong() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "123 +123  -123    -1");
        assertEquals(123L, sqlLexer.longLiteral());
        assertEquals(123L, sqlLexer.longLiteral());
        assertEquals(-123L, sqlLexer.longLiteral());
        assertEquals(-1L, sqlLexer.longLiteral());
    }

    @Test
    public void successfullyStringLiteral() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "'this is a quoted message'");
        assertEquals("this is a quoted message", sqlLexer.stringLiteral().toString());
    }

    @Test
    public void successfullyStringLiteralWithSingleQuote() throws Exception {
        String s = "'this is a message with \\' char'";
        SQLLexer sqlLexer = new SQLLexer(0, s);
        System.out.println(s);
        assertEquals("this is a message with \\' char", sqlLexer.stringLiteral().toString());
    }

    @Test
    public void successfullyBareWord() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "_askes String");
        assertEquals("_askes", sqlLexer.bareWord().toString());
        assertEquals("String", sqlLexer.bareWord().toString());
    }

    @Test
    public void successfullyDateTime() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "h DateTime('Asia/Shanghai')");
        assertEquals("h", sqlLexer.bareWord().toString());
        assertEquals("DateTime", sqlLexer.bareWord().toString());
        assertEquals('(', sqlLexer.character());
        assertEquals("Asia/Shanghai", sqlLexer.stringLiteral().toString());
        assertEquals(')', sqlLexer.character());
    }
}
