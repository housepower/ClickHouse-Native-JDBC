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

package com.github.housepower.serde;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.housepower.buffer.ByteArrayWriter;

class BinarySerializerTest {
    private BinarySerializer serializer;
    private ByteArrayWriter writer;
    private Random rand;

    @BeforeEach
    void setup() {
        rand = new Random();
        
        writer = new ByteArrayWriter(1024);
        serializer = new BinarySerializer(writer, false);
    }
    
    @Test
    void WHEN_writeInt_THEN_reconstruct_same_int() throws IOException {
        int input = rand.nextInt();
        // WHEN
        serializer.writeInt(input);
        
        writer.flushToTarget(true);
        
        // THEN
        ByteBuffer buffer = writer.getBufferList().get(0);
        buffer.order(ByteOrder.LITTLE_ENDIAN).flip();
        int output = buffer.getInt();
        
        Assertions.assertEquals(input, output);
    }
    
    @Test
    void WHEN_writeLong_THEN_reconstruct_same_long() throws IOException {
        long input = rand.nextLong();
        // WHEN
        serializer.writeLong(input);
        
        writer.flushToTarget(true);
        
        // THEN
        ByteBuffer buffer = writer.getBufferList().get(0);
        buffer.order(ByteOrder.LITTLE_ENDIAN).flip();
        long output = buffer.getLong();
        
        Assertions.assertEquals(input, output);
    }
    
    @Test
    void WHEN_writeShort_THEN_reconstruct_same_short() throws IOException {
        short input = (short) rand.nextInt();
        // WHEN
        serializer.writeShort(input);
        
        writer.flushToTarget(true);
        
        // THEN
        ByteBuffer buffer = writer.getBufferList().get(0);
        buffer.order(ByteOrder.LITTLE_ENDIAN).flip();
        short output = buffer.getShort();
        
        Assertions.assertEquals(input, output);
    }
    
    @Test
    void WHEN_writeDouble_THEN_reconstruct_same_double() throws IOException {
        double input = rand.nextDouble();
        // WHEN
        serializer.writeDouble(input);
        
        writer.flushToTarget(true);
        
        // THEN
        ByteBuffer buffer = writer.getBufferList().get(0);
        buffer.order(ByteOrder.LITTLE_ENDIAN).flip();
        double output = buffer.getDouble();
        
        Assertions.assertEquals(input, output);
    }
}
