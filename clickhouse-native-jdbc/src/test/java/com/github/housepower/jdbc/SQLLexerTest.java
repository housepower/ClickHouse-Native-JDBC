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

package com.github.housepower.jdbc;

import com.github.housepower.misc.SQLLexer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SQLLexerTest {

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
    public void successfullyInt() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "123 +123  -123    -1");
        assertEquals(123, sqlLexer.intLiteral());
        assertEquals(123, sqlLexer.intLiteral());
        assertEquals(-123, sqlLexer.intLiteral());
        assertEquals(-1, sqlLexer.intLiteral());
    }

    @Test
    public void successfullyStringLiteral() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "'this is a quoted message'");
        assertEquals("this is a quoted message", sqlLexer.stringLiteral());
    }

    @Test
    public void successfullyStringLiteralWithSingleQuote() throws Exception {
        String s = "'this is a message with \\' char'";
        SQLLexer sqlLexer = new SQLLexer(0, s);
        assertEquals("this is a message with \\' char", sqlLexer.stringLiteral());
    }

    @Test
    public void successfullyBareWord() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "_askes String");
        assertTrue(sqlLexer.bareWord().checkEquals("_askes"));
        assertTrue(sqlLexer.bareWord().checkEquals("String"));
    }

    @Test
    public void successfullyDateTime() throws Exception {
        SQLLexer sqlLexer = new SQLLexer(0, "h DateTime('Asia/Shanghai')");
        assertTrue(sqlLexer.bareWord().checkEquals("h"));
        assertTrue(sqlLexer.bareWord().checkEquals("DateTime"));
        assertEquals('(', sqlLexer.character());
        assertEquals("Asia/Shanghai", sqlLexer.stringLiteral());
        assertEquals(')', sqlLexer.character());
    }
}
