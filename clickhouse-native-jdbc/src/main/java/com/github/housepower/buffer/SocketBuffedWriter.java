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

package com.github.housepower.buffer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SocketBuffedWriter implements BuffedWriter {

    private final OutputStream out;

    public SocketBuffedWriter(Socket socket) throws IOException {
        this.out = socket.getOutputStream();
    }

    @Override
    public void writeBinary(byte byt) throws IOException {
        out.write(byt);
    }

    @Override
    public void writeBinary(byte[] bytes, int offset, int length) throws IOException {
        out.write(bytes, offset, length);
    }

    @Override
    public void flushToTarget(boolean force) throws IOException {
        out.flush();
    }
}
