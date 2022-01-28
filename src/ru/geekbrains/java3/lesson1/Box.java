package ru.geekbrains.java3.lesson1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Box<T extends Fruit> {

    private final List<T> fruits = new ArrayList<>();

    public Float getWeight() {
        Float weight = 0f;
        for (T fruit : fruits) {
            weight += fruit.getWeight();
        }
        return weight;
    }

    public void put(T fruit) {
        fruits.add(fruit);
    }

    public void put(T... fruits) {
        this.fruits.addAll(Arrays.asList(fruits));
    }

    public boolean compare(Box<?> box) {
        return Math.abs(this.getWeight() - box.getWeight()) < 0.001;
    }

    public void putInAnotherBox(Box<T> box) {
        if (this == box) return;
        for (T fruit : fruits) box.put(fruit);
        this.fruits.clear();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("[ ");
        for (T fruit : fruits) {
            str.append(String.format("%s(%s) ", fruit.getFruitName(), fruit.getWeight()));
        }
        return str.append(']').toString();
    }
}
