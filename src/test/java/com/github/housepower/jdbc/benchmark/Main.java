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
        for(URL url : classLoader.getURLs())
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
