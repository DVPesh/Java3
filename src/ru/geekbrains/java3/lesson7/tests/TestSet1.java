package ru.geekbrains.java3.lesson7.tests;

import ru.geekbrains.java3.lesson7.annotations.AfterSuite;
import ru.geekbrains.java3.lesson7.annotations.BeforeSuite;
import ru.geekbrains.java3.lesson7.annotations.Test;

public class TestSet1 {

    @BeforeSuite
    private void prepare() {
        System.out.println("Выполняется перед запуском тестов");
    }

    @AfterSuite
    private void terminate() {
        System.out.println("Выполняется после всех тестов");
    }

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

    @Test(priority = 3)
    public static void testJob5() {
        System.out.println("Выполняется тест Job5 с приоритетом 3");
    }

    @Test(priority = 3)
    private void testJob6() {
        System.out.println("Выполняется тест Job6 с приоритетом 3");
    }

    @Test(priority = 3)
    protected void testJob7() {
        System.out.println("Выполняется тест Job7 с приоритетом 3");
    }

    @Test(priority = 4)
    private static void testJob8() {
        System.out.println("Выполняется тест Job8 с приоритетом 4");
    }

    @Test(priority = 5)
    void testJob9() {
        System.out.println("Выполняется тест Job9 с приоритетом 5");
    }

    @Test(priority = 6)
    public void testJob10() {
        System.out.println("Выполняется тест Job10 с приоритетом 6");
    }

    @Test(priority = 7)
    public void testJob11() {
        System.out.println("Выполняется тест Job11 с приоритетом 7");
    }

    @Test(priority = 8)
    public void testJob12() {
        System.out.println("Выполняется тест Job12 с приоритетом 8");
    }

    @Test(priority = 9)
    public void testJob13() {
        System.out.println("Выполняется тест Job13 с приоритетом 9");
    }

    @Test(priority = 10)
    public static void testJob14() {
        System.out.println("Выполняется тест Job14 с приоритетом 10");
    }

    @Test(priority = 10)
    private void testJob15() {
        System.out.println("Выполняется тест Job15 с приоритетом 10");
    }

    protected void testJob16() {
        System.out.println("Выполняется тест Job16 без аннотации");
    }

}
