package org.apache.spark.sql

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.json.{JSONOptions, JacksonGenerator}
import org.apache.spark.sql.types.StructType

import java.io.StringWriter
import java.time.ZoneId

object JsonFormatUtil {

  def row2Json(row: InternalRow, schema: StructType): String = {
    val line = new StringWriter()
    val gen = new JacksonGenerator(schema, line, new JSONOptions(Map.empty[String, String], ZoneId.systemDefault().getId))
    gen.write(row)
    gen.writeLineEnding
    gen.flush
    line.toString
  }
}
