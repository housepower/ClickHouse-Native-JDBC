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

package com.github.housepower.jdbc.benchmark;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.profile.AsyncProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Benchmark {

    public static void main(String[] args) throws RunnerException {

        Options options = new OptionsBuilder()
                .forks(1)
                .mode(Mode.AverageTime)
                .warmupIterations(0)
                .measurementIterations(1)
                .include("./*IBenchmark")
                .result("reports/jmh.txt")
                .resultFormat(ResultFormatType.TEXT)
                .addProfiler(AsyncProfiler.class, "event=itimer;output=flamegraph;direction=forward;simple=true;dir=reports;width=1920")
                .build();
        new Runner(options).run();
    }
}
