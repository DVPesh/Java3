package ru.geekbrains.java3.lesson1;

public abstract class Fruit {

    private final String fruitName;
    private final Float weight;

    public Fruit(String name, Float weight) {
        fruitName = name;
        this.weight = weight;
    }

    public String getFruitName() {
        return fruitName;
    }

    public Float getWeight() {
        return weight;
    }
}
