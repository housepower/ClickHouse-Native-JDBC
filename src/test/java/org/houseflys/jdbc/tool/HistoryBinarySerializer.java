package org.houseflys.jdbc.tool;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.mockito.Mockito;

public class HistoryBinarySerializer extends BinarySerializer {

    private final List<Object> receiveHistory = new ArrayList<Object>();

    public HistoryBinarySerializer() throws IOException {
        super(Mockito.mock(Socket.class));
    }

    @Override
    public void writeVarInt(long x) throws IOException {
        receiveHistory.add(x);
    }

    @Override
    public void writeStringBinary(String binary) throws IOException {
        receiveHistory.add(binary);
    }

    public void assertAll(Object... expects) {
        Iterator<Object> iterator = receiveHistory.iterator();

        for (Object expect : expects) {
            Assert.assertTrue(iterator.hasNext());
            Assert.assertEquals(expect, iterator.next());
        }
        Assert.assertFalse(iterator.hasNext());
    }
}
