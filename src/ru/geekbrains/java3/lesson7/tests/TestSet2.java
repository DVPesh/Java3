package ru.geekbrains.java3.lesson7.tests;

import ru.geekbrains.java3.lesson7.annotations.AfterSuite;
import ru.geekbrains.java3.lesson7.annotations.BeforeSuite;
import ru.geekbrains.java3.lesson7.annotations.Test;

public class TestSet2 {

    @Test
    public void testJob1() {
        System.out.println("Выполняется тест Job1 с приоритетом по умолчанию (1)");
    }

    @Test(priority = 1)
    public void testJob2() {
        System.out.println("Выполняется тест Job2 с приоритетом 1");
    }

    @Test(priority = 2)
    public void testJob3() {
        System.out.println("Выполняется тест Job3 с приоритетом 2");
    }

    @Test(priority = 3)
    public void testJob4() {
        System.out.println("Выполняется тест Job4 с приоритетом 3");
    }

    @BeforeSuite
    private void prepare() {
        System.out.println("Выполняется перед запуском тестов");
    }

    @AfterSuite
    private void terminate() {
        System.out.println("Выполняется после всех тестов");
    }

    @BeforeSuite
    private void prepare2() {
        System.out.println("Выполняется перед запуском тестов");
    }

}
