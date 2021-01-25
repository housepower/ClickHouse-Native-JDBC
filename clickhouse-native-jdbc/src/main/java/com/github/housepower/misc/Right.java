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

import java.util.Locale;
import java.util.function.Function;

public class Right<L, R> implements Either<L, R> {

    private final R value;

    Right(R value) {
        this.value = value;
    }

    @Override
    public boolean isRight() {
        return true;
    }

    @Override
    public <R1> Either<L, R1> map(Function<R, R1> f) {
        return Either.right(f.apply(value));
    }

    @Override
    public <R1> Either<L, R1> flatMap(Function<R, Either<L, R1>> f) {
        return f.apply(value);
    }

    @Override
    public L getLeft() {
        throw new InvalidOperationException("Right not support #getLeft");
    }

    @Override
    public R getRight() {
        return value;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "Right(%s)", value);
    }
}
