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

import java.nio.CharBuffer;

public class StringView {
    private final int start;
    private final int end;
    private final char[] values;

    public StringView(int start, int end, char[] values) {
        this.start = start;
        this.end = end;
        this.values = values;
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }

    public char[] values() {
        return values;
    }

    public boolean checkEquals(String expectString) {
        if (expectString == null || expectString.length() != end - start)
            return false;

        for (int i = 0; i < expectString.length(); i++) {
            if (expectString.charAt(i) != values[start + i])
                return false;
        }
        return true;
    }

    public CharBuffer toCharBuffer() {
        return CharBuffer.wrap(values, start, end - start);
    }

    @Override
    public String toString() {
        return new String(values, start, end - start);
    }
}
