package com.github.housepower.jdbc.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class SocketBuffedReaderTest {

    @Test
    public void successfullyReadTwoIntValue() throws Exception {
        SocketBuffedReader buffedReader = new SocketBuffedReader(
            fragmentInput(new byte[] {1, 0, 0, 0, 2}, new byte[] {0, 3, 4}), 6);

        Assert.assertEquals(buffedReader.readBinary(), 1);

        byte[] bytes = new byte[5];
        buffedReader.readBinary(bytes);

        Assert.assertEquals(bytes[0], 0);
        Assert.assertEquals(bytes[1], 0);
        Assert.assertEquals(bytes[2], 0);
        Assert.assertEquals(bytes[3], 2);
        Assert.assertEquals(bytes[4], 0);
        Assert.assertEquals(buffedReader.readBinary(), 3);
        Assert.assertEquals(buffedReader.readBinary(), 4);
    }


    private InputStream fragmentInput(final byte[]... fragments) throws IOException {
        InputStream in = Mockito.mock(InputStream.class);
        final AtomicInteger position = new AtomicInteger(0);

        Mockito.when(in.read(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt()))
            .thenAnswer(new Answer<Integer>() {
                @Override
                public Integer answer(InvocationOnMock invocation) throws Throwable {
                    int offset = invocation.getArgumentAt(1, int.class);
                    byte[] bytes = invocation.getArgumentAt(0, byte[].class);

                    byte[] fragment = fragments[position.getAndIncrement()];
                    if (bytes.length < fragment.length) {
                        throw new IOException("Failed test case.");
                    }
                    System.arraycopy(fragment, 0, bytes, offset, fragment.length);
                    return fragment.length;
                }
            });

        return in;
    }
}
