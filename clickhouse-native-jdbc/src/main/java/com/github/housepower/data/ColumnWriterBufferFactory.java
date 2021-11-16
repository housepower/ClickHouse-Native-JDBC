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
package com.github.housepower.data;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Factory of column writer that can recycle {@link ColumnWriterBuffer}.
 * 
 * <p>
 * The factory is thread-safe and can be used by multiple threads or connections.
 * </p>
 *
 */
public class ColumnWriterBufferFactory {

    private final ConcurrentLinkedDeque<ColumnWriterBuffer> stack = new ConcurrentLinkedDeque<>();

    private static final ColumnWriterBufferFactory INSTANCE = new ColumnWriterBufferFactory();

    public static ColumnWriterBufferFactory getInstance() {
        return INSTANCE;
    }

    private ColumnWriterBufferFactory() {
    }

    /**
     * Obtain an instance of {@link ColumnWriterBuffer}.
     * <p>
     * The method tries to get an instance from storage. If one is available, the buffer is reset and returned. Otherwise a new one is created and return.
     * </p>
     * 
     * @return
     */
    public ColumnWriterBuffer getBuffer() {
        ColumnWriterBuffer pop = stack.pollLast();
        if (pop == null) {
            return new ColumnWriterBuffer();
        } else {
            pop.reset();
            return pop;
        }
    }

    /**
     * Clear all buffers to free the buffered memory
     * This method is exported to be called by users manually
     */
    public void clearAllBuffers() {
        while (true)  {
            ColumnWriterBuffer pop = stack.pollLast();
            if (pop == null) {
                break;
            }
        }
    }

    public void recycleBuffer(ColumnWriterBuffer buffer) {
        stack.addLast(buffer);
    }
}
