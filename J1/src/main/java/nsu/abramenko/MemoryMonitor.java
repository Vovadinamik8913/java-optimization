package nsu.abramenko;

public class MemoryMonitor{
    private final Runtime runtime = Runtime.getRuntime();

    public Statistic analyzeMemory(long startTime) {
        long timeElapsed = System.currentTimeMillis() - startTime;
        double timeSeconds = timeElapsed / 1000.0;

        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        long usedMemory = totalMemory - freeMemory;
        return new Statistic(timeSeconds, maxMemory, totalMemory, usedMemory, freeMemory);
    }
}
