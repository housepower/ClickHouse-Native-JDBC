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
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.*;

public class SocketBuffedReaderTest {

    @Test
    public void successfullyReadTwoIntValue() throws Exception {
        SocketBuffedReader buffedReader = new SocketBuffedReader(
            fragmentInput(new byte[] {1, 0, 0, 0, 2}, new byte[] {0, 3, 4}), 6);

        assertEquals(buffedReader.readBinary(), 1);

        byte[] bytes = new byte[5];
        buffedReader.readBinary(bytes);

        assertEquals(bytes[0], 0);
        assertEquals(bytes[1], 0);
        assertEquals(bytes[2], 0);
        assertEquals(bytes[3], 2);
        assertEquals(bytes[4], 0);
        assertEquals(buffedReader.readBinary(), 3);
        assertEquals(buffedReader.readBinary(), 4);
    }


    private InputStream fragmentInput(final byte[]... fragments) throws IOException {
        InputStream in = Mockito.mock(InputStream.class);
        final AtomicInteger position = new AtomicInteger(0);

        Mockito.when(in.read(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt()))
            .thenAnswer((Answer<Integer>) invocation -> {
                int offset = invocation.getArgument(1, Integer.class);
                byte[] bytes = invocation.getArgument(0, byte[].class);

                byte[] fragment = fragments[position.getAndIncrement()];
                if (bytes.length < fragment.length) {
                    throw new IOException("Failed test case.");
                }
                System.arraycopy(fragment, 0, bytes, offset, fragment.length);
                return fragment.length;
            });

        return in;
    }
}
