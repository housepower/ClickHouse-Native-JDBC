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

package com.github.housepower.spark

import com.github.housepower.data.IDataType
import com.github.housepower.data.`type`.{DataTypeInt16, DataTypeInt32, DataTypeInt64, DataTypeInt8, DataTypeUInt8}
import com.github.housepower.data.`type`.complex.{DataTypeNullable, DataTypeString}
import org.apache.spark.sql.ClickHouseAnalysisException
import org.apache.spark.sql.types.{ByteType, IntegerType, LongType, ShortType, StringType, StructField, StructType}

import java.nio.charset.StandardCharsets

object ClickHouseSchemaUtil {

  def fromClickHouseSchema(chSchema: Seq[(String, IDataType[_, _])]): StructType = {
    val structFields = chSchema
      .map {
        case (name, nullableType: DataTypeNullable) => (name, nullableType.getNestedDataType, true)
        case (name, selfType) => (name, selfType, false)
      }
      .map { case (name, chType, nullable) =>
        val fieldType = chType match {
          case _: DataTypeInt8 => ByteType
          case _: DataTypeInt16 => ShortType
          case _: DataTypeInt32 => IntegerType
          case _: DataTypeInt64 => LongType
          case _: DataTypeString => StringType
        }
        StructField(name, fieldType, nullable)
      }
    StructType(structFields)
  }

  def toClickHouseSchema(sparkSchema: StructType): Seq[(String, IDataType[_, _])] = {
    sparkSchema.fields
      .map { field =>
        val chType = field.dataType match {
          case ByteType => new DataTypeInt8
          case ShortType => new DataTypeInt16
          case IntegerType => new DataTypeInt32
          case LongType => new DataTypeInt64
          case StringType => new DataTypeString(StandardCharsets.UTF_8)
          case _ => throw ClickHouseAnalysisException(s"Unsupported field: ${field.name}[${field.dataType}]")
        }
        (field.name, if (field.nullable) nullable(chType) else chType)
      }
  }

  private def nullable(chType: IDataType[_, _]): IDataType[_, _] =
    new DataTypeNullable(s"Nullable(${chType.name()})", new DataTypeUInt8, chType)
}
