package ru.geekbrains.java3.lesson1;

public class MyClass {

    char value1;
    float value2;

    public MyClass(char value1, float value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    @Override
    public String toString() {
        return "MyClass{" +
                "value1=" + value1 +
                ", value2=" + value2 +
                '}';
    }
}
