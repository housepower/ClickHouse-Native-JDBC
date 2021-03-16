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

import org.apache.spark.internal.Logging
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.connector.write.{DataWriter, WriterCommitMessage}

/**
 * Directly write data to local tables behind the Distributed table
 */
class ClickHouseDistributedBatchWriter extends DataWriter[InternalRow] with Logging {

  override def write(record: InternalRow): Unit = ???

  override def commit(): WriterCommitMessage = ???

  override def abort(): Unit = ???

  override def close(): Unit = ???
}
