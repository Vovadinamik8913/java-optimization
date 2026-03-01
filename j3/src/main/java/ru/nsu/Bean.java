package ru.nsu;

import java.util.Arrays;

public class Bean {
    public byte byteField = 1;
    public short shortField = 2;
    public int intField = 3;
    public long longField = 4L;
    public float floatField = 5.0f;
    public double doubleField = 6.0;
    public boolean booleanField = true;
    public char charField = 'A';

    public String stringField = "initial";

    public Bean childBean;

    public byte[] byteArray = {10, 20, 30};
    public int[] intArray = {100, 200, 300};

    public Bean[] beanArray;

    private int privateField = 42;

    public Bean() {
        this.childBean = new Bean(false);
        this.beanArray = new Bean[]{new Bean(false), new Bean(false)};
    }

    private Bean(boolean skip) {
        this.beanArray = new Bean[0];
    }

    public int getPrivateField() {
        return privateField;
    }

    public void setPrivateField(int value) {
        this.privateField = value;
    }

    public void printMessage(String message) {
        System.out.println("  [Java] Called from C: " + message);
    }

    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public String toString() {
        return String.format("Bean{intField=%d, stringField='%s', byteArray=%s, intArray=%s}",
                intField, stringField, Arrays.toString(byteArray), Arrays.toString(intArray));
    }
}