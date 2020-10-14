package com.github.housepower.jdbc;

import org.junit.Assert;
import org.junit.Test;
import com.github.housepower.jdbc.misc.SQLLexer;

public class SQLLexerITest extends AbstractITest {

    @Test
    public void successfullyNumber() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "123");
        Assert.assertEquals(sqlLexer.numberLiteral(), 123L);
    }

    @Test
    public void successfullyNegativeNumber() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "-123");
        Assert.assertEquals(sqlLexer.numberLiteral(), -123L);
    }

    @Test
    public void successfullyDoubleNumber() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "-123.0 3E2");
        Assert.assertEquals(sqlLexer.numberLiteral(), -123.0);
        Assert.assertEquals(sqlLexer.numberLiteral(), 300.0);
    }

    @Test
    public void successfullyNumberInHexa() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "-0xfp2 0xfp2 0xfp1 0xfp0 0xf");
        Assert.assertEquals(sqlLexer.numberLiteral(), -60.0);
        Assert.assertEquals(sqlLexer.numberLiteral(), 60.0d);
        Assert.assertEquals(sqlLexer.numberLiteral(), 30.0);
        Assert.assertEquals(sqlLexer.numberLiteral(), 15.0);
        Assert.assertEquals(sqlLexer.numberLiteral(), 15L);
    }

    @Test
    public void successfullyNumberInBinary() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "0b111 -0b111 +0b111");
        Assert.assertEquals(sqlLexer.numberLiteral(), 7L);
        Assert.assertEquals(sqlLexer.numberLiteral(), -7L);
        Assert.assertEquals(sqlLexer.numberLiteral(), 7L);
    }

    @Test
    public void successfullyLong() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "123 +123  -123    -1");
        Assert.assertEquals(sqlLexer.longLiteral(), 123L);
        Assert.assertEquals(sqlLexer.longLiteral(), 123L);
        Assert.assertEquals(sqlLexer.longLiteral(), -123L);
        Assert.assertEquals(sqlLexer.longLiteral(), -1L);
    }

    @Test
    public void successfullyStringLiteral() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "'this is a quoted message'");
        Assert.assertEquals(sqlLexer.stringLiteral().toString(), "this is a quoted message");

    }

    @Test
    public void successfullyStringLiteralWithSingleQuote() throws Exception {
        String s =  "'this is a message with \\' char'";
        SQLLexer sqlLexer = new SQLLexer(0,s);
        System.out.println(s);
        Assert.assertEquals("this is a message with \\' char", sqlLexer.stringLiteral().toString());
    }

    @Test
    public void successfullyBareWord() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "_askes String");
        Assert.assertEquals(sqlLexer.bareWord().toString(), "_askes");
        Assert.assertEquals(sqlLexer.bareWord().toString(), "String");

    }

}
