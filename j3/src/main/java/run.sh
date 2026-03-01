#!/bin/bash

echo "=== Compiling Java classes ==="
javac -h ./ru/nsu/jni ru/nsu/*.java

echo "=== Compiling native library ==="
JAVA_HOME=${JAVA_HOME:-$(/usr/libexec/java_home)}
gcc -shared -fPIC \
    -I"$JAVA_HOME/include" \
    -I"$JAVA_HOME/include/darwin" \
    -o ./ru/nsu/jni/libnative.dylib ./ru/nsu/jni/jni.c

echo "=== Setting library path ==="
export DYLD_LIBRARY_PATH=./ru/nsu/jni:$DYLD_LIBRARY_PATH

echo "=== Running Java program ==="
java -cp . -Djava.library.path=./ru/nsu/jni ru.nsu.Main

echo "=== Done ==="