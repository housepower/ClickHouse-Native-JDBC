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

package com.github.housepower.misc;

public class StringView implements CharSequence {

    private final String str;

    private final int start;

    private final int end;

    private int hashVal;

    private String repr;

    public StringView(String str, int start, int end) {
        this.str = str;
        this.start = Math.max(start, 0);
        this.end = Math.min(end, str.length());
    }

    @Override
    public int length() {
        return end - start;
    }

    @Override
    public char charAt(int index) {
        return str.charAt(start + index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return subview(start, end);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StringView)) {
            return false;
        }
        StringView other = (StringView) obj;
        int len = length();
        if (len != other.length()) {
            return false;
        }
        int start1 = start;
        int start2 = other.start;
        while (start1 < end && start2 < other.end) {
            if (str.charAt(start1) != other.str.charAt(start2)) {
                return false;
            }
            start1++;
            start2++;
        }
        return true;
    }

    /**
     * A modified hash function from older JDK. The initial value is a large prime.
     * The value 31 is a small prime.
     */
    @Override
    public int hashCode() {
        if (hashVal == 0) {
            int h = 0x01000193;
            for (int i = start; i < end; i++) {
                h = 31 * h + str.charAt(i);
            }
            if (h == 0) {
                h++;
            }
            hashVal = h;
        }
        return hashVal;
    }

    @Override
    public String toString() {
        return repr();
    }

    public String data() {
        return str;
    }

    public int start() {
        return this.start;
    }

    public int end() {
        return this.end;
    }

    public StringView subview(int start0, int end0) {
        return new StringView(str, start + start0, start + end0);
    }

    public String repr() {
        if (repr == null) {
            repr = str.substring(this.start, this.end);
        }
        return repr;
    }

    public boolean checkEquals(String expectString) {
        if (expectString == null || expectString.length() != end - start)
            return false;

        for (int i = 0; i < expectString.length(); i++) {
            if (expectString.charAt(i) != str.charAt(start + i))
                return false;
        }
        return true;
    }

    public boolean checkEqualsIgnoreCase(String expectString) {
        if (expectString == null || expectString.length() != end - start)
            return false;

        for (int i = 0; i < expectString.length(); i++) {
            if (expectString.charAt(i) != str.charAt(start + i))
                return false;
        }
        return true;
    }
}
