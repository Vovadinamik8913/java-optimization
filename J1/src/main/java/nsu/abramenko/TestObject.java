package nsu.abramenko;

import java.util.ArrayList;
import java.util.List;

public class TestObject {
    private final int intValue = 42;
    private final long longValue = 100L;
    private final double doubleValue = 3.14;
    private final boolean boolValue = true;

    private final String stringValue = "Hello World";
    private final List<Integer> intArray = new ArrayList<>();
    private final List<String> stringArray = new ArrayList<>();
    private final List<String> stringList = new ArrayList<>();

    public TestObject() {
        for (int i = 0; i < 100; i++) {
            intArray.add(i);
        }

        for (int i = 0; i < 50; i++) {
            stringArray.add("String " + i);
        }

        for (int i = 0; i < 20; i++) {
            stringList.add(new String("List item " + i));
        }
    }
}
