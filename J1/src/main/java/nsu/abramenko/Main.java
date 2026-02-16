package nsu.abramenko;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final List<TestObject> objects = new ArrayList<>();
    private static final MemoryMonitor monitor = new MemoryMonitor();

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Time(s), Max Memory(MB), Total Memory(MB), Free Memory(MB), Used Memory(MB), Objects Count");

        long startTime = System.currentTimeMillis();
        int objectCount = 0;

        while (true) {
            if (objectCount % 1000 == 0) {
                var statistic = monitor.analyzeMemory(startTime);

                System.out.printf("%.2f; %d; %d; %d; %d; %d%n",
                        statistic.getTimeSeconds(),
                        statistic.getMaxMemory(),
                        statistic.getTotalMemory(),
                        statistic.getUsedMemory(),
                        statistic.getFreeMemory(),
                        objectCount);
            }
            objects.add(new TestObject());
            objectCount++;

            if (objectCount % 1000 == 0) {
                TimeUnit.MILLISECONDS.sleep(100);
            }
        }
    }
}