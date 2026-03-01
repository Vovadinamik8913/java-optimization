package ru.nsu;

public class Main {
    public static void main(String[] args) {
        System.out.println("\n=== JNI DEMO: Выполнение всех требований задания ===\n");

        NativeService nativeService = new NativeService();
        Bean bean = new Bean();

        System.out.println("Тест получения строки и возврата длины:");
        String testString = "Hello from Java!";
        int length = nativeService.getStringLength(testString);
        System.out.println("   Строка: \"" + testString + "\"");
        System.out.println("   Длина (из C): " + length + " символов");
        System.out.println();

        System.out.println("Тест вызова метода Java объекта из C:");
        nativeService.callObjectMethod(bean);
        System.out.println();

        System.out.println("Тест изменения полей Java объекта из C:");
        System.out.println("   До изменения: " + bean);
        nativeService.modifyObjectField(bean);
        System.out.println("   После изменения: " + bean);
        System.out.println();

        System.out.println("Тест работы с нативными структурами данных:");
        System.out.println("   - Создание структуры в C...");
        long ptr = nativeService.createNativeStruct(42, "TestStruct");
        System.out.println("   Указатель на структуру: 0x" + Long.toHexString(ptr));

        System.out.println("   - Чтение значения из структуры...");
        int value = nativeService.getValueFromNativeStruct(ptr);
        System.out.println("   Значение id из структуры: " + value);

        System.out.println("   - Освобождение структуры...");
        nativeService.freeNativeStruct(ptr);
        System.out.println("   Структура освобождена");
        System.out.println();

        System.out.println("Тест аллокации 1KB памяти и мониторинга через Runtime:");
        Runtime runtime = Runtime.getRuntime();
        Runtime rt = Runtime.getRuntime();
        System.out.println("Total: " + rt.totalMemory());
        System.out.println("Free: " + rt.freeMemory());
        nativeService.allocate1KbMemory();
        rt = Runtime.getRuntime();
        System.out.println("Total: " + rt.totalMemory());
        System.out.println("Free: " + rt.freeMemory());

        // Демонстрация падений (закомментировано для безопасности)
        System.out.println("Демонстрация методов, вызывающих падения:");
        System.out.println("   - crashTheProcess() - съедает всю память и падает");
        System.out.println("   - chainOfCalls() - вызывает segmentation fault");
        System.out.println();

        //System.out.println("\nЗапуск цепочки вызовов с падением...");
        //nativeService.chainOfCalls();

        System.out.println("\nЗапуск метода, съедающего память...");
        nativeService.crashTheProcess();
    }
}