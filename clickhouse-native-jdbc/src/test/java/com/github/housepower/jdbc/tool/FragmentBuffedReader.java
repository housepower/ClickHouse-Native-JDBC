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

package com.github.housepower.jdbc.tool;

import java.io.IOException;

import com.github.housepower.buffer.BuffedReader;

public class FragmentBuffedReader implements BuffedReader {

    private int fragmentPos;

    private int bytesPosition;

    private final byte[][] fragments;

    public FragmentBuffedReader(byte[]... fragments) {
        this.fragments = fragments;
    }

    @Override
    public int readBinary() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int readBinary(byte[] bytes) throws IOException {

        for (int i = 0; i < bytes.length; ) {
            if (bytesPosition == fragments[fragmentPos].length) {
                fragmentPos++;
                bytesPosition = 0;
            }


            byte[] fragment = fragments[fragmentPos];

            int pending = bytes.length - i;
            int fillLength = Math.min(pending, fragment.length - bytesPosition);

            if (fillLength > 0) {
                System.arraycopy(fragment, bytesPosition, bytes, i, fillLength);

                i += fillLength;
                bytesPosition += fillLength;
            }
        }
        return bytes.length;
    }
}
