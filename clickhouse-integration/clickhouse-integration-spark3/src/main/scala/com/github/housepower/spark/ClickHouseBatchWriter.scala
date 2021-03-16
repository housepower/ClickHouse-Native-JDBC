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

import com.github.housepower.client.GrpcConnection
import org.apache.spark.internal.Logging
import org.apache.spark.sql.JsonFormatUtil
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.connector.write.{DataWriter, WriterCommitMessage}
import org.apache.spark.sql.types.StructType
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util

import scala.collection.mutable

import com.github.housepower.settings.ClickHouseConfig

class ClickHouseBatchWriter(val cfg: ClickHouseConfig,
                            val queryId: String,
                            val database: String,
                            val table: String,
                            val schema: StructType,
                            val batchSize: Int = 1000
                           ) extends DataWriter[InternalRow] with Logging {

  @transient val grpcConn: GrpcConnection = GrpcConnection.create(cfg)

  val ckSchema: util.Map[String, String] = ClickHouseSchemaUtil.toClickHouseSchema(schema)
    .foldLeft(new util.LinkedHashMap[String, String]) { case (acc, (k, v)) =>
      acc.put(k, v.name()); acc
    }

  val buf: mutable.MutableList[String] = new mutable.MutableList[String]

  override def write(record: InternalRow): Unit = {
    buf += JsonFormatUtil.row2Json(record, schema)
    if (buf.size == batchSize)
      flush()
  }

  override def commit(): WriterCommitMessage = {
    if (buf.nonEmpty)
      flush()
    new WriterCommitMessage {}
  }

  override def abort(): Unit = {}

  override def close(): Unit = {}

  // TODO retry
  def flush(): Unit = {
    val result = grpcConn.syncInsert(database, table, "JSONEachRow",
      buf.mkString.getBytes(StandardCharsets.UTF_8))
    result.getException match {
      case e if e.getCode != 0 => throw new IOException(e.getDisplayText)
      case _ => buf.clear
    }
  }
}
