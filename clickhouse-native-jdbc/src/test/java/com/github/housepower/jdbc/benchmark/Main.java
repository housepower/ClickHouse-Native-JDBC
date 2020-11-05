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
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 */
public class Main {
    public static void main(String[] args) throws RunnerException {
        URLClassLoader classLoader = (URLClassLoader) Main.class.getClassLoader();
        StringBuilder classpath = new StringBuilder();
        for (URL url : classLoader.getURLs())
            classpath.append(url.getPath()).append(File.pathSeparator);
        System.setProperty("java.class.path", classpath.toString());

        Options options = new OptionsBuilder().include(Main.class.getSimpleName())
                .forks(1).mode(Mode.AverageTime)
                .include("./*IBenchmark")
                .warmupIterations(0).measurementIterations(1)
                .build();
        new Runner(options).run();
    }
}
