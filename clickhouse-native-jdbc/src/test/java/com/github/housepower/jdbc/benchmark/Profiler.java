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

import java.util.Locale;

/**
 * From https://gist.github.com/gothug/3b35fe0cec302efe6f8314a9cab8865e
 */
public class Profiler {
    public static class ElapsedTime {
        private final long elapsedTimeMillis;
        private final long elapsedTimeDeltaMillis;

        public long getElapsedTimeMillis() {
            return elapsedTimeMillis;
        }

        public long getElapsedTimeDeltaMillis() {
            return elapsedTimeDeltaMillis;
        }

        public ElapsedTime(long etMillis, long etDeltaMillis) {
            elapsedTimeMillis = etMillis;
            elapsedTimeDeltaMillis = etDeltaMillis;
        }
    }

    private static final long MEGABYTE = 1024L * 1024L;

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

    public static String fixedLengthString(String string, int length) {
        return String.format(Locale.ROOT, "%1$" + length + "s", string);
    }


    long startTimeMillis;
    long lastEventTimeMillis;
    Runtime runtime;

    private static final Profiler _instance = new Profiler();

    public static synchronized Profiler getInstance() {
        return _instance;
    }

    private Profiler() {
        startTimeMillis = System.currentTimeMillis();
        lastEventTimeMillis = startTimeMillis;
        runtime = Runtime.getRuntime();
    }

    public void start() {
        System.out.println("Profiler started");
    }

    private ElapsedTime getElapsedTime() {
        long now = System.currentTimeMillis();
        long elapsedMillis = now - startTimeMillis;
        long elapsedDeltaMillis = now - lastEventTimeMillis;
        lastEventTimeMillis = now;
        return new ElapsedTime(elapsedMillis, elapsedDeltaMillis);
    }

    public void logStat(long percentComplete, int runGc) {
        if (runGc == 1) {
            runtime.gc();
        }
        // Calculate the used memory
        long totalMemory = runtime.totalMemory();
        long memory = totalMemory - runtime.freeMemory();

        ElapsedTime elapsedTime = getElapsedTime();
        long elapsedMillis = elapsedTime.getElapsedTimeMillis();
        long elapsedDeltaMillis = elapsedTime.getElapsedTimeDeltaMillis();

        String percentCompleteStr = fixedLengthString(String.valueOf(percentComplete), 3) + "%";
        String totalMemoryStr = fixedLengthString(String.valueOf(bytesToMegabytes(totalMemory)), 5);
        String usedMemoryStr = fixedLengthString(String.valueOf(bytesToMegabytes(memory)), 5);
        String totalElapsedMillisStr = fixedLengthString(String.valueOf(elapsedMillis), 6);
        String millisPassedFromLastLogStr = fixedLengthString(String.valueOf(elapsedDeltaMillis), 6);
        System.out.println(percentCompleteStr + " Total memory (MB): " + totalMemoryStr + ", used memory (MB): "
                + usedMemoryStr + ", time elapsed: " + totalElapsedMillisStr
                + "(+" + millisPassedFromLastLogStr + " ms)");
    }
}