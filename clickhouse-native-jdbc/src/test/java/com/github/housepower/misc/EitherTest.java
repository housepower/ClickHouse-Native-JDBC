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

import com.github.housepower.exception.InvalidOperationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EitherTest {

    @Test
    public void testLeft() {
        Either<Integer, String> left = Either.left(1);
        assertFalse(left.isRight());
        assertEquals(1, left.getLeft());
        assertThrows(InvalidOperationException.class, left::getRight);
        Either<Integer, String> mappedLeft = left.map(str -> str + "haha");
        assertFalse(mappedLeft.isRight());
        assertEquals(1, mappedLeft.getLeft());
        Either<Integer, String> flatMappedLeft = left.flatMap(str -> Either.right(str + "haha"));
        assertFalse(flatMappedLeft.isRight());
        assertEquals(1, flatMappedLeft.getLeft());
    }

    @Test
    public void testRight() {
        Either<Integer, String> right = Either.right("haha");
        assertTrue(right.isRight());
        assertThrows(InvalidOperationException.class, right::getLeft);
        assertEquals("haha", right.getRight());
        Either<Integer, String> mappedRight = right.map(str -> str + "haha");
        assertTrue(mappedRight.isRight());
        assertEquals("hahahaha", mappedRight.getRight());
        Either<Integer, String> flatMappedRight = right.flatMap(str -> Either.right(str + "haha"));
        assertTrue(flatMappedRight.isRight());
        assertEquals("hahahaha", flatMappedRight.getRight());
        Either<Integer, String> flatMappedLeft = right.flatMap(str -> Either.left(1));
        assertFalse(flatMappedLeft.isRight());
        assertEquals(1, flatMappedLeft.getLeft());
    }
}
