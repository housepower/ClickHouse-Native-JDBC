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

package com.github.housepower.protocol;

import com.github.housepower.exception.ClickHouseSQLException;
import io.netty.buffer.ByteBuf;

import java.sql.SQLException;

public class ExceptionResponse implements Response {

    public static SQLException readExceptionFrom(ByteBuf buf) {
        int code = buf.readIntLE();
        String name = helper.readUTF8Binary(buf);
        String message = helper.readUTF8Binary(buf);
        String stackTrace = helper.readUTF8Binary(buf);

        if (buf.readBoolean()) {
            return new ClickHouseSQLException(
                    code,
                    name + message + ". Stack trace:\n\n" + stackTrace,
                    readExceptionFrom(buf));
        }

        return new ClickHouseSQLException(code, name + message + ". Stack trace:\n\n" + stackTrace);
    }

    @Override
    public ProtoType type() {
        return ProtoType.RESPONSE_EXCEPTION;
    }
}
