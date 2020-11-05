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

package org.apache.spark.sql.jdbc

import org.apache.spark.sql.jdbc.ClickHouseDialect._
import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api.Test

class ClickHouseDialectTest {

  @Test
  def testArrayTypeRegex(): Unit = {
    "Array(String)" match {
      case arrayTypePattern(nestType) => assertEquals("String", nestType)
      case _ => fail()
    }

    "Array(Nullable(String))" match {
      case arrayTypePattern(nestType) => assertEquals("Nullable(String)", nestType)
      case _ => fail()
    }

    "Array(Array(String))" match {
      case arrayTypePattern(nestType) => assertEquals("Array(String)", nestType)
      case _ => fail()
    }

    "array(String)" match {
      case arrayTypePattern(_) => fail()
      case _ => assertTrue(true)
    }

    "Array(String" match {
      case arrayTypePattern(_) => fail()
      case _ => assertTrue(true)
    }
  }

  @Test
  def testDateTypeRegex(): Unit = {
    "Date" match {
      case dateTypePattern() => assertTrue(true)
      case _ => fail()
    }

    "dAtE" match {
      case dateTypePattern() => assertTrue(true)
      case _ => fail()
    }

    "DT" match {
      case dateTypePattern(_) => fail()
      case _ => assertTrue(true)
    }
  }

  @Test
  def testDateTimeTypeRegex(): Unit = {
    "DateTime" match {
      case dateTimeTypePattern(_, _, _) => assertTrue(true)
      case _ => fail()
    }

    "dAtEtiMe(Asia/Shanghai)" match {
      case dateTimeTypePattern(_, _, tz) => assertEquals("Asia/Shanghai", tz)
      case _ => fail()
    }

    "DateTime64" match {
      case dateTimeTypePattern(_64, _, _) => assertEquals("64", _64)
      case _ => fail()
    }

    "DaTeTiMe64(Europe/Moscow)" match {
      case dateTimeTypePattern(_64, _, tz) =>
        assertEquals("64", _64)
        assertEquals("Europe/Moscow", tz)
      case _ => fail()
    }

    "DT" match {
      case dateTimeTypePattern(_) => fail()
      case _ => assertTrue(true)
    }
  }

  @Test
  def testDecimalTypeRegex(): Unit = {
    "Decimal(1,2)" match {
      case decimalTypePattern(p, s) =>
        assertEquals("1", p)
        assertEquals("2", s)
      case _ => fail()
    }

    "DeCiMaL(1, 2)" match {
      case decimalTypePattern(p, s) =>
        assertEquals("1", p)
        assertEquals("2", s)
      case _ => fail()
    }

    "Decimal" match {
      case decimalTypePattern(_, _) => fail()
      case _ => assertTrue(true)
    }

    "Decimal(String" match {
      case decimalTypePattern(_, _) => fail()
      case _ => assertTrue(true)
    }
  }

  @Test
  def testDecimalTypeRegex2(): Unit = {
    "Decimal32(5)" match {
      case decimalTypePattern2(a, s) => assertEquals(("32", "5"), (a, s))
      case _ => fail()
    }

    "decimal64(5)" match {
      case decimalTypePattern2(a, s) => assertEquals(("64", "5"), (a, s))
      case _ => fail()
    }

    "DeCiMaL128(5)" match {
      case decimalTypePattern2(a, s) => assertEquals(("128", "5"), (a, s))
      case _ => fail()
    }

    "DeCiMaL256(5)" match {
      case decimalTypePattern2(a, s) => assertEquals(("256", "5"), (a, s))
      case _ => fail()
    }

    "Decimal32(5" match {
      case decimalTypePattern2(a, s) => fail()
      case _ => assertTrue(true)
    }
  }

  @Test
  def testEnumTypeRegex(): Unit = {
    "Enum8" match {
      case enumTypePattern(w) => assertEquals("8", w)
      case _ => fail()
    }

    "Enum16" match {
      case enumTypePattern(w) => assertEquals("16", w)
      case _ => fail()
    }

    "Enum" match {
      case enumTypePattern(w) => fail()
      case _ => assertTrue(true)
    }

    "Enum(8)" match {
      case enumTypePattern(_) => fail()
      case _ => assertTrue(true)
    }

    "enum8" match {
      case enumTypePattern(_) => fail()
      case _ => assertTrue(true)
    }

    "String" match {
      case enumTypePattern(_) => fail()
      case _ => assertTrue(true)
    }
  }

  @Test
  def testFixedStringTypeRegex(): Unit = {
    "FixedString(5)" match {
      case fixedStringTypePattern(l) => assertEquals("5", l)
      case _ => fail()
    }

    "fixedString(5)" match {
      case fixedStringTypePattern(_) => fail()
      case _ => assertTrue(true)
    }

    "String" match {
      case decimalTypePattern2(a, s) => fail()
      case _ => assertTrue(true)
    }
  }

  @Test
  def testNullableTypeRegex(): Unit = {
    assertEquals((true, "String"), unwrapNullable("Nullable(String)"))
    assertEquals((false, "nullable(String)"), unwrapNullable("nullable(String)"))
    assertEquals((false, "String"), unwrapNullable("String"))
  }
}
