package com.github.housepower.jdbc.benchmark;

import java.util.Locale;

/**
 * From https://gist.github.com/gothug/3b35fe0cec302efe6f8314a9cab8865e
 */
public class Profiler {
    public class ElapsedTime {
        private long elapsedTimeMillis;
        private long elapsedTimeDeltaMillis;

        public long getElapsedTimeMillis() { return elapsedTimeMillis; }
        public long getElapsedTimeDeltaMillis() { return elapsedTimeDeltaMillis; }

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
        return String.format(Locale.ROOT, "%1$"+length+ "s", string);
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
                           + usedMemoryStr + ", time elapsed: " + totalElapsedMillisStr + "(+" + millisPassedFromLastLogStr + " ms)");
    }
}