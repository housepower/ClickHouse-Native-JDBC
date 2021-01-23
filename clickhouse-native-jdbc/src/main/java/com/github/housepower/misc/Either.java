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

import java.util.function.Function;

public interface Either<L, R> {

    static <L1, R1> Either<L1, R1> left(L1 value) {
        return new Left<>(value);
    }

    static <L1, R1> Either<L1, R1> right(R1 value) {
        return new Right<>(value);
    }

    boolean isRight();

    <R1> Either<L, R1> map(Function<R, R1> f);

    <R1> Either<L, R1> flatMap(Function<R, Either<L, R1>> f);

    L getLeft();

    R getRight();
}
