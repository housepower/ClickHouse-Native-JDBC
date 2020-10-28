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

import org.apache.spark.sql.jdbc.ClickHouseDialect.{arrayTypePattern, dateTimeTypePattern, decimalTypePattern}
import org.junit.Assert._
import org.junit.Test

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

    "Array(String" match {
      case arrayTypePattern(_) => fail()
      case _ => assertTrue("not match", true)
    }
  }

  @Test
  def testDecimalTypeRegex(): Unit = {
    "Decimal" match {
      case decimalTypePattern(_, _, _) => assertTrue(true)
      case _ => fail()
    }

    "Decimal(1,2)" match {
      case decimalTypePattern(_, p, s) =>
        assertEquals("1", p)
        assertEquals("2", s)
      case _ => fail()
    }

    "Decimal(1, 2)" match {
      case decimalTypePattern(_, p, s) =>
        assertEquals("1", p)
        assertEquals("2", s)
      case _ => fail()
    }

    // TODO Decimal32(S), Decimal64(S), Decimal128(S), Decimal256(S)

    "Decimal(String" match {
      case decimalTypePattern(_) => fail()
      case _ => assertTrue(true)
    }
  }

  @Test
  def testDateTimeTypeRegex(): Unit = {
    "DateTime" match {
      case dateTimeTypePattern(_, _, _) => assertTrue(true)
      case _ => fail()
    }

    "DateTime(Asia/Shanghai)" match {
      case dateTimeTypePattern(_, _, tz) => assertEquals("Asia/Shanghai", tz)
      case _ => fail()
    }

    "DateTime64" match {
      case dateTimeTypePattern(_64, _, _) => assertEquals("64", _64)
      case _ => fail()
    }

    "DateTime64(Europe/Moscow)" match {
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
}
