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

import java.util

import scala.collection.JavaConverters._

import com.github.housepower.settings.ClickHouseConfig
import org.apache.spark.sql.connector.catalog._
import org.apache.spark.sql.connector.catalog.TableCapability._
import org.apache.spark.sql.connector.expressions.Transform
import org.apache.spark.sql.connector.write.LogicalWriteInfo
import org.apache.spark.sql.types.StructType

case class Shard()

class ClickHouseTable(ident: Identifier,
                      override val schema: StructType,
                      override val properties: util.Map[String, String],
                      cfg: ClickHouseConfig
                     ) extends Table with SupportsWrite with SupportsMetadataColumns {

  override val name: String = ident.toString

  override def capabilities(): util.Set[TableCapability] =
    Set(BATCH_READ, BATCH_WRITE, TRUNCATE).asJava

  override def partitioning(): Array[Transform] = super.partitioning()

  override def newWriteBuilder(info: LogicalWriteInfo): ClickHouseWriteBuilder = {
    new ClickHouseWriteBuilder(info, cfg, ident.namespace().head, ident.name())
  }

  // TODO cluster, shard, partition
  override def metadataColumns(): Array[MetadataColumn] = Array()
}
