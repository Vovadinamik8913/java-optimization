package ru.nsu;

public class NativeService {
    static {
        System.loadLibrary("native");
    }

    // Метод который жрет память внутри C и роняет все
    public native void crashTheProcess();

    // Метод который аллоцирует 1 Кб памяти
    public native void allocate1KbMemory();

    // Метод с цепочкой вызовов в C, который ломается
    public native void chainOfCalls();

    // Получить строку String и вернуть длину строки
    public native int getStringLength(String str);

    // Получить объект и вызвать у него метод
    public native void callObjectMethod(Bean bean);

    // Получить объект и поменять значение его java-поля
    public native void modifyObjectField(Bean bean);

    // Три метода для работы с нативными структурами данных:
    // 1. Аллоцировать структуру, вернуть указатель
    public native long createNativeStruct(int value, String name);

    // 2. Получить указатель, вернуть значение из структуры
    public native int getValueFromNativeStruct(long pointer);

    // 3. Получить указатель и освободить память
    public native void freeNativeStruct(long pointer);
}