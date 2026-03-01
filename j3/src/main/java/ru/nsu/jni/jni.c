// jni/jni.c
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "ru_nsu_NativeService.h"

// Объявление функций для цепочки вызовов
void level1();
void level2();
void level3();

// Структура для демонстрации работы с нативными данными
typedef struct {
    int id;
    char* name;
    double values[5];
    int counter;
} NativeData;

// Реализация функций для цепочки вызовов
void level3() {
    printf("      [C] level3: about to crash...\n");
    // Искусственное падение - разыменование NULL
    int* ptr = NULL;
    *ptr = 42;  // Segmentation fault здесь!
}

void level2() {
    printf("      [C] level2: calling level3\n");
    level3();
}

void level1() {
    printf("      [C] level1: calling level2\n");
    level2();
}

// 1. Метод который жрет память внутри C и роняет все
JNIEXPORT void JNICALL Java_ru_nsu_NativeService_crashTheProcess
  (JNIEnv *env, jobject obj) {
    printf("   [C] WARNING: Starting memory eating...\n");

    int blocks = 0;
    while(1) {
        // Выделяем по 100 MB
        void* memory = malloc(100 * 1024 * 1024);
        if (memory == NULL) {
            printf("   [C] Failed to allocate memory after %d blocks\n", blocks);
            break;
        }
        // Заполняем память, чтобы она реально использовалась
        memset(memory, 1, 100 * 1024 * 1024);
        blocks++;
        printf("   [C] Allocated block #%d (100 MB), total: %d MB\n",
               blocks, blocks * 100);
    }

    // После того как память закончилась - вызываем падение
    printf("   [C] Attempting to crash...\n");
    int *p = (int*)0x0;
    *p = 10;  // Доступ по нулевому адресу
}

// 2. Метод который аллоцирует 1 Кб памяти
JNIEXPORT void JNICALL Java_ru_nsu_NativeService_allocate1KbMemory
  (JNIEnv *env, jobject obj) {
    printf("   [C] Allocating 1 KB of native memory\n");

    void* memory = malloc(1024);
    if (memory != NULL) {
        memset(memory, 0xFF, 1024);
        printf("   [C] Memory allocated at: %p\n", memory);
        printf("   [C] NOTE: This memory is NOT visible to Runtime.xxxMemory()\n");
        printf("   [C] Runtime shows only JVM heap, not native allocations!\n");
        // Не освобождаем - демонстрируем утечку
    } else {
        printf("   [C] Failed to allocate memory\n");
    }
}

// 3. Метод с цепочкой вызовов, который ломается
JNIEXPORT void JNICALL Java_ru_nsu_NativeService_chainOfCalls
  (JNIEnv *env, jobject obj) {
    printf("   [C] Starting chain of calls that will crash...\n");
    printf("   [C] Call stack:\n");
    level1();
}

// 4. Получить строку и вернуть длину
JNIEXPORT jint JNICALL Java_ru_nsu_NativeService_getStringLength
  (JNIEnv *env, jobject obj, jstring str) {

    printf("   [C] Getting string length\n");

    if (str == NULL) {
        printf("   [C] String is null\n");
        return 0;
    }

    // Получаем строку в UTF-8
    const char* utfString = (*env)->GetStringUTFChars(env, str, NULL);
    if (utfString == NULL) {
        printf("   [C] Failed to get string chars\n");
        return -1;
    }

    jint length = strlen(utfString);
    printf("   [C] String: \"%s\", length: %d\n", utfString, length);

    // Освобождаем ресурсы
    (*env)->ReleaseStringUTFChars(env, str, utfString);

    return length;
}

// 5. Получить объект и вызвать у него метод
JNIEXPORT void JNICALL Java_ru_nsu_NativeService_callObjectMethod
  (JNIEnv *env, jobject obj, jobject bean) {

    printf("   [C] Calling methods on Java object\n");

    if (bean == NULL) {
        printf("   [C] Bean is null\n");
        return;
    }

    // Получаем класс объекта
    jclass beanClass = (*env)->GetObjectClass(env, bean);

    // Находим метод printMessage(String)
    jmethodID printMethod = (*env)->GetMethodID(env, beanClass,
                                                "printMessage",
                                                "(Ljava/lang/String;)V");
    if (printMethod != NULL) {
        jstring message = (*env)->NewStringUTF(env, "Hello from C!");
        printf("   [C] Calling printMessage...\n");
        (*env)->CallVoidMethod(env, bean, printMethod, message);
        (*env)->DeleteLocalRef(env, message);
    } else {
        printf("   [C] Method printMessage not found\n");
    }

    // Находим и вызываем метод add(int, int)
    jmethodID addMethod = (*env)->GetMethodID(env, beanClass, "add", "(II)I");
    if (addMethod != NULL) {
        printf("   [C] Calling add(10, 20)...\n");
        jint result = (*env)->CallIntMethod(env, bean, addMethod, 10, 20);
        printf("   [C] Result from Java: %d\n", result);
    }

    (*env)->DeleteLocalRef(env, beanClass);
}

// 6. Получить объект и поменять значение его поля
JNIEXPORT void JNICALL Java_ru_nsu_NativeService_modifyObjectField
  (JNIEnv *env, jobject obj, jobject bean) {

    printf("   [C] Modifying Java object fields\n");

    if (bean == NULL) {
        printf("   [C] Bean is null\n");
        return;
    }

    jclass beanClass = (*env)->GetObjectClass(env, bean);

    // Меняем intField
    jfieldID intFieldId = (*env)->GetFieldID(env, beanClass, "intField", "I");
    if (intFieldId != NULL) {
        jint currentValue = (*env)->GetIntField(env, bean, intFieldId);
        printf("   [C] Current intField: %d\n", currentValue);

        jint newValue = currentValue + 500;
        (*env)->SetIntField(env, bean, intFieldId, newValue);
        printf("   [C] Set intField to: %d\n", newValue);
    }

    // Меняем stringField
    jfieldID stringFieldId = (*env)->GetFieldID(env, beanClass,
                                                "stringField",
                                                "Ljava/lang/String;");
    if (stringFieldId != NULL) {
        jstring newString = (*env)->NewStringUTF(env, "Modified by C code!");
        (*env)->SetObjectField(env, bean, stringFieldId, newString);
        (*env)->DeleteLocalRef(env, newString);
        printf("   [C] Modified stringField\n");
    }

    // Меняем элементы массивов
    jfieldID byteArrayId = (*env)->GetFieldID(env, beanClass,
                                              "byteArray", "[B");
    if (byteArrayId != NULL) {
        jbyteArray byteArray = (*env)->GetObjectField(env, bean, byteArrayId);
        if (byteArray != NULL) {
            jbyte* elements = (*env)->GetByteArrayElements(env, byteArray, NULL);
            if (elements != NULL) {
                printf("   [C] Original byteArray[0]: %d\n", elements[0]);
                elements[0] = 99;
                (*env)->ReleaseByteArrayElements(env, byteArray, elements, 0);
                printf("   [C] Modified byteArray[0] to: 99\n");
            }
            (*env)->DeleteLocalRef(env, byteArray);
        }
    }

    (*env)->DeleteLocalRef(env, beanClass);
}

// 7.1 Аллоцировать структуру, вернуть указатель
JNIEXPORT jlong JNICALL Java_ru_nsu_NativeService_createNativeStruct
  (JNIEnv *env, jobject obj, jint value, jstring name) {

    printf("   [C] Creating native structure\n");

    // Выделяем память под структуру
    NativeData* data = (NativeData*)malloc(sizeof(NativeData));
    if (data == NULL) {
        printf("   [C] Failed to allocate structure\n");
        return 0;
    }

    // Заполняем структуру
    data->id = value;
    data->counter = 100;

    // Заполняем массив значений
    for (int i = 0; i < 5; i++) {
        data->values[i] = value * (i + 1) * 1.5;
    }

    // Копируем имя
    const char* nameStr = (*env)->GetStringUTFChars(env, name, NULL);
    if (nameStr != NULL) {
        data->name = (char*)malloc(strlen(nameStr) + 1);
        if (data->name != NULL) {
            strcpy(data->name, nameStr);
        }
        (*env)->ReleaseStringUTFChars(env, name, nameStr);
    } else {
        data->name = NULL;
    }

    printf("   [C] Structure created at: %p\n", data);
    printf("   [C]   id: %d\n", data->id);
    printf("   [C]   name: %s\n", data->name ? data->name : "null");

    return (jlong)(intptr_t)data;
}

// 7.2 Получить указатель, вернуть значение из структуры
JNIEXPORT jint JNICALL Java_ru_nsu_NativeService_getValueFromNativeStruct
  (JNIEnv *env, jobject obj, jlong pointer) {

    // Преобразуем long обратно в указатель
    NativeData* data = (NativeData*)(intptr_t)pointer;

    if (data == NULL) {
        printf("   [C] Invalid pointer\n");
        return -1;
    }

    printf("   [C] Reading from structure at: %p\n", data);
    printf("   [C]   id: %d\n", data->id);
    printf("   [C]   name: %s\n", data->name ? data->name : "null");
    printf("   [C]   values: [");
    for (int i = 0; i < 5; i++) {
        printf("%.1f%s", data->values[i], i < 4 ? ", " : "");
    }
    printf("]\n");

    return data->id;
}

// 7.3 Получить указатель и освободить память
JNIEXPORT void JNICALL Java_ru_nsu_NativeService_freeNativeStruct
  (JNIEnv *env, jobject obj, jlong pointer) {

    NativeData* data = (NativeData*)(intptr_t)pointer;

    if (data != NULL) {
        printf("   [C] Freeing structure at: %p\n", data);

        // Освобождаем внутренние строки
        if (data->name != NULL) {
            free(data->name);
        }

        // Освобождаем саму структуру
        free(data);
        printf("   [C] Structure freed\n");
    }
}