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

import java.sql.Types
import java.util.Locale

import org.apache.spark.internal.Logging
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils
import org.apache.spark.sql.types._

import scala.util.matching.Regex

/**
 * ClickHouseDialects
 * TODO: support Nullable Type
 */
object ClickHouseDialect extends JdbcDialect with Logging {

  val arrayTypePattern: Regex = "^Array\\((.*)\\)$".r
  // TODO Decimal32(S), Decimal64(S), Decimal128(S), Decimal256(S)
  val decimalTypePattern: Regex = "^Decimal(\\((\\d+),\\s*(\\d+)\\))?$".r
  val dateTimeTypePattern: Regex = "^DateTime(64)?(\\((.*)\\))?$".r

  override def canHandle(url: String): Boolean =
    url.toLowerCase(Locale.ROOT).startsWith("jdbc:clickhouse")

  override def getCatalystType(sqlType: Int,
                               typeName: String,
                               size: Int, md: MetadataBuilder): Option[DataType] = {
    sqlType match {
      case Types.ARRAY =>
        logDebug(s"sqlType: $sqlType, typeName: $typeName, size: $size")
        typeName match {
          case arrayTypePattern(nestType) =>
            val scale = md.build.getLong("scale").toInt
            toCatalystType(nestType, size, scale).map(ArrayType(_, containsNull = false))
          case _ => None
        }
      case _ => None
    }
  }

  private def toCatalystType(typeName: String,
                             precision: Int,
                             scale: Int): Option[DataType] = typeName match {
    case "String" => Some(StringType)
    case "Int8" => Some(ByteType)
    case "UInt8" | "Int16" => Some(ShortType)
    case "UInt16" | "Int32" => Some(IntegerType)
    case "UInt32" | "UInt64" | "Int64" => Some(LongType)
    case "Float32" => Some(FloatType)
    case "Float64" => Some(DoubleType)
    case decimalTypePattern(precision, scale, _) => Some(DecimalType(precision.toInt, scale.toInt))
    case "Date" => Some(DateType)
    case dateTimeTypePattern() => Some(TimestampType)
    case _ => None
  }

  override def getJDBCType(dt: DataType): Option[JdbcType] = dt match {
    case StringType => Some(JdbcType("String", Types.VARCHAR))
    // ClickHouse doesn't have the concept of encodings. Strings can contain an arbitrary set of bytes,
    // which are stored and output as-is.
    // See detail at https://clickhouse.tech/docs/en/sql-reference/data-types/string/
    case BinaryType => Some(JdbcType("String", Types.BINARY))
    case BooleanType => Some(JdbcType("UInt8", Types.BOOLEAN))
    case ByteType => Some(JdbcType("Int8", Types.TINYINT))
    case ShortType => Some(JdbcType("Int16", Types.SMALLINT))
    case IntegerType => Option(JdbcType("Int32", Types.INTEGER))
    case LongType => Option(JdbcType("Int64", Types.BIGINT))
    case FloatType => Some(JdbcType("Float32", Types.FLOAT))
    case DoubleType => Some(JdbcType("Float64", Types.DOUBLE))
    case t: DecimalType => Some(JdbcType(s"Decimal(${t.precision},${t.scale})", Types.DECIMAL))
    case DateType => Option(JdbcType("Date", Types.DATE))
    case TimestampType => Option(JdbcType("DateTime", Types.TIMESTAMP))
    case ArrayType(et, _) if et.isInstanceOf[AtomicType] =>
      getJDBCType(et)
        .orElse(JdbcUtils.getCommonJDBCType(et))
        .map(jdbcType => JdbcType(s"${jdbcType.databaseTypeDefinition}[]", Types.ARRAY))
    case _ => None
  }

  override def quoteIdentifier(colName: String): String = s"`$colName`"

  override def isCascadingTruncateTable: Option[Boolean] = Some(false)
}
