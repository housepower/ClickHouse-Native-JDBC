package org.apache.spark.sql.jdbc

import java.sql.Types
import java.util.Locale

import org.apache.spark.internal.Logging
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils
import org.apache.spark.sql.types._

import scala.util.matching.Regex

object ClickHouseDialects extends JdbcDialect with Logging {

  private val arrayTypePattern: Regex = "^Array\\((.*)\\)$".r

  override def canHandle(url: String): Boolean =
    url.toLowerCase(Locale.ROOT).startsWith("jdbc:clickhouse")

  override def getCatalystType(sqlType: Int,
                               typeName: String,
                               size: Int, md: MetadataBuilder): Option[DataType] = {
    sqlType match {
      case Types.ARRAY =>
        logDebug(s"sqlType: $sqlType, typeName: $typeName, size: $size")
        typeName match {
          case arrayTypePattern(nestType) if nestType == "String" => Some(ArrayType(StringType))
          // TODO
          case arrayTypePattern(nestType) =>
            logWarning(s"Not implemented nestType: $nestType")
            None
          case _ => None
        }
      case _ => None
    }
  }

  // TODO consider nullable
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
