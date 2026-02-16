package nsu.abramenko;

import java.util.ArrayList;
import java.util.List;

public class MemoryMeasurement {

    // Версия 1: Базовый объект (ваш исходный)
    static class TestObjectV1 {
        private final int intValue = 42;
        private final long longValue = 100L;
        private final double doubleValue = 3.14;
        private final boolean boolValue = true;

        private final String stringValue = "Hello World";
        private final List<Integer> intArray = new ArrayList<>();
        private final List<String> stringArray = new ArrayList<>();
        private final List<String> stringList = new ArrayList<>();

        public TestObjectV1() {
            for (int i = 0; i < 100; i++) intArray.add(i);
            for (int i = 0; i < 50; i++) stringArray.add("String " + i);
            for (int i = 0; i < 20; i++) stringList.add(new String("List item " + i));
        }
    }

    // Версия 2: Только примитивы
    static class TestObjectV2 {
        private int intValue = 42;
        private long longValue = 100L;
        private double doubleValue = 3.14;
        private boolean boolValue = true;
    }

    // Версия 3: Примитивы + 1 строка
    static class TestObjectV3 {
        private int intValue = 42;
        private long longValue = 100L;
        private double doubleValue = 3.14;
        private boolean boolValue = true;
        private String stringValue = "Hello World";
    }

    // Версия 4: Примитивы + ArrayList<Integer> с 100 элементами
    static class TestObjectV4 {
        private int intValue = 42;
        private long longValue = 100L;
        private double doubleValue = 3.14;
        private boolean boolValue = true;
        private List<Integer> intArray = new ArrayList<>();

        public TestObjectV4() {
            for (int i = 0; i < 100; i++) intArray.add(i);
        }
    }

    public static void measureMemory(String name, Object prototype, int count)
            throws Exception {
        System.gc();
        Thread.sleep(1000);

        Runtime runtime = Runtime.getRuntime();
        long before = runtime.totalMemory() - runtime.freeMemory();

        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            objects.add(prototype.getClass().getDeclaredConstructor().newInstance());
        }

        long after = runtime.totalMemory() - runtime.freeMemory();
        long memoryPerObject = (after - before) / count;

        System.out.printf("%s: %d байт на объект (%.2f KB)%n",
                name, memoryPerObject, memoryPerObject / 1024.0);

        objects.clear();
        System.gc();
        Thread.sleep(500);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Измерение памяти объектов ===\n");

        // Создаем по 10000 объектов каждого типа
        measureMemory("V2 (только примитивы)", new TestObjectV2(), 10000);
        measureMemory("V3 (примитивы + строка)", new TestObjectV3(), 10000);
        measureMemory("V4 (примитивы + ArrayList<Integer>)", new TestObjectV4(), 5000);
        measureMemory("V1 (полный объект)", new TestObjectV1(), 1000);
    }
}