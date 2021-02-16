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

import com.github.housepower.exception.ClickHouseException;
import io.netty.buffer.ByteBuf;

public class ExceptionResponse implements Response {

    public static ExceptionResponse readFrom(ByteBuf buf) {
        ClickHouseException ex = readExceptionFrom(buf);
        return new ExceptionResponse(ex);
    }

    private static ClickHouseException readExceptionFrom(ByteBuf buf) {
        int errCode = buf.readIntLE();
        String name = helper.readUTF8Binary(buf);
        String message = helper.readUTF8Binary(buf);
        String stackTrace = helper.readUTF8Binary(buf);
        boolean hasInner = buf.readBoolean();
        ClickHouseException innerEx = hasInner ? readExceptionFrom(buf) : null;
        String errMsg = "ERROR[" + errCode + "] " + message + "\n" +
                "=== stack trace ===" + "\n" +
                stackTrace;
        return new ClickHouseException(errCode, errMsg, innerEx);
    }

    private final ClickHouseException exception;

    public ExceptionResponse(ClickHouseException exception) {
        this.exception = exception;
    }

    @Override
    public ProtoType type() {
        return ProtoType.RESPONSE_EXCEPTION;
    }

    public ClickHouseException exception() {
        return exception;
    }
}
