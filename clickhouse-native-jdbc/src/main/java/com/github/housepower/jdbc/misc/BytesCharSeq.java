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

package com.github.housepower.jdbc.misc;

public class BytesCharSeq implements CharSequence {

    private final byte[] bytes;

    public BytesCharSeq(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public int length() {
        return bytes.length;
    }

    @Override
    public char charAt(int index) {
        return (char) bytes[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        byte[] newBytes = new byte[end - start];
        System.arraycopy(bytes, start, newBytes, 0, end - start);
        return new BytesCharSeq(newBytes);
    }

    @Override
    public String toString() {
        return "BytesCharSeq, length: " + length();
    }

    public byte[] bytes() {
        return bytes;
    }
}
