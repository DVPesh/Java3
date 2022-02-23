package ru.geekbrains.java3.lesson7.tests;

import ru.geekbrains.java3.lesson7.annotations.Test;

public class TestSet4 {

    @Test(priority = 7)
    public void testJob3() {
        System.out.println("Выполняется тест Job3 с приоритетом 7");
    }

    @Test(priority = 3)
    private void testJob1() {
        System.out.println("Выполняется тест Job1 с приоритетом 3");
    }

    @Test(priority = 7)
    public static void testJob2() {
        System.out.println("Выполняется тест Job2 с приоритетом 7");
    }

    @Test(priority = 9)
    protected void testJob5() {
        System.out.println("Выполняется тест Job5 с приоритетом 9");
    }

    @Test(priority = 7)
    protected static void testJob4() {
        System.out.println("Выполняется тест Job4 с приоритетом 7");
    }

}
