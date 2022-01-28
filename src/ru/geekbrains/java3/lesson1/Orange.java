package ru.geekbrains.java3.lesson1;

public class Orange extends Fruit {

    public Orange(Float weight) {
        super("Апельсин", weight);
    }

    public Orange() {
        this(1.5f);
    }
}
