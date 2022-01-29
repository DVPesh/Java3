package ru.geekbrains.java3.lesson1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Homework1 {

    private final static Double[] doubles = new Double[]{1.0, 2.0, 3.0, 4.0};
    private final static String[] strings = new String[]{"first", "second", "third"};
    private final static MyClass[] myClassObjects = new MyClass[]{new MyClass('a', 1f), new MyClass('b', 2f)};

    public static void main(String[] args) {
        System.out.println("----------------------задание №1--------------------------------------");
        //задание №1
        task1();
        System.out.println("----------------------задание №2--------------------------------------");
        //задание №2
        task2();
        System.out.println("----------------------задание №3--------------------------------------");
        //задание №3
        task3();
    }

    private static void task1() {
        System.out.println(Arrays.toString(doubles));
        swapElements(doubles, 0, 2);
        System.out.println(Arrays.toString(doubles));

        System.out.println(Arrays.toString(strings));
        swapElements(strings, 0, 2);
        System.out.println(Arrays.toString(strings));

        System.out.println(Arrays.toString(myClassObjects));
        swapElements(myClassObjects, 0, 1);
        System.out.println(Arrays.toString(myClassObjects));
    }

    public static <T> void swapElements(T[] array, int index1, int index2) {
        T value = array[index1];
        array[index1] = array[index2];
        array[index2] = value;
    }

    private static void task2() {
        List<Double> doubleList = castArrayToArrayList(doubles);
        System.out.println(doubleList);
        doubleList.add(5.0);
        doubleList.add(6.0);
        System.out.println(doubleList);
        List<String> stringList = castArrayToArrayList(strings);
        System.out.println(stringList);
        stringList.add("fourth");
        stringList.add("fifth");
        System.out.println(stringList);
        List<MyClass> myClassList = castArrayToArrayList(myClassObjects);
        System.out.println(myClassList);
        myClassList.add(new MyClass('c', 3f));
        System.out.println(myClassList);
    }

    public static <T> ArrayList<T> castArrayToArrayList(T[] array) {
        return new ArrayList<T>(Arrays.asList(array)); //получаем ArrayList, у которого можно изменять ёмкость
    }

    private static void task3() {
        Box<Apple> box1 = new Box<>();
        Box<Orange> box2 = new Box<>();
        Box<Apple> box3 = new Box<>();
        Box<Orange> box4 = new Box<>();

        box1.put(new Apple(1f));
        box1.put(new Apple(1.1f));
        box1.put(new Apple(1.2f));
        box1.put(new Apple(0.7f));
        System.out.println("Содержимое коробки 1: " + box1);
//        box1.put(new Orange(1.5f)); //нельзя положить апельсин в коробку для яблок
        System.out.printf("вес коробки 1: %.2f%n", box1.getWeight());
        box2.put(new Orange(1.4f));
        box2.put(new Orange(1.6f));
        System.out.println("Содержимое коробки 2: " + box2);
//        box2.put(new Apple(1f)); //нельзя положить яблоко в коробку для апельсинов
        System.out.printf("вес коробки 2: %.2f%n", box2.getWeight());
        System.out.println("У коробок 1 и 2 вес " + (box2.compare(box1) ? "одинаковый" : "разный"));
        box2.put(new Orange(1f));
        System.out.println("Содержимое коробки 2: " + box2);
        System.out.printf("вес коробки 2: %.2f%n", box2.getWeight());
        System.out.println("У коробок 1 и 2 вес " + (box1.compare(box2) ? "одинаковый" : "разный"));

        System.out.printf("вес коробки 3: %.2f%n", box3.getWeight());
        System.out.println("пересыпаем коробку 1 в коробку 3");
        box1.putInAnotherBox(box3);
        System.out.printf("вес коробки 1: %.2f%n", box1.getWeight());
        System.out.printf("вес коробки 3: %.2f%n", box3.getWeight());
        System.out.println("Содержимое коробки 1: " + box1);
        System.out.println("Содержимое коробки 3: " + box3);
//        box1.putInAnotherBox(box2); //нельзя смешивать яблоки с апельсинами
//        box2.putInAnotherBox(box1); //нельзя смешивать яблоки с апельсинами
        System.out.printf("вес коробки 4: %.2f%n", box4.getWeight());
        System.out.println("пересыпаем коробку 2 в коробку 4");
        box2.putInAnotherBox(box4);
        System.out.printf("вес коробки 2: %.2f%n", box2.getWeight());
        System.out.printf("вес коробки 4: %.2f%n", box4.getWeight());
        System.out.println("Содержимое коробки 2: " + box2);
        System.out.println("Содержимое коробки 4: " + box4);

        System.out.println("универсальная коробка 5 позволяет смешивать фрукты");
        Box box5 = new Box<>();
        box3.putInAnotherBox(box5);
        box4.putInAnotherBox(box5);
        System.out.printf("вес коробки 3: %.2f%n", box3.getWeight());
        System.out.printf("вес коробки 4: %.2f%n", box4.getWeight());
        System.out.printf("вес коробки 5: %.2f%n", box5.getWeight());
        System.out.println("Содержимое универсальной коробки 5: " + box5);
    }
}
