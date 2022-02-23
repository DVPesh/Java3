package ru.geekbrains.java3.lesson7;

import ru.geekbrains.java3.lesson7.annotations.AfterSuite;
import ru.geekbrains.java3.lesson7.annotations.BeforeSuite;
import ru.geekbrains.java3.lesson7.annotations.Test;
import ru.geekbrains.java3.lesson7.tests.TestSet1;
import ru.geekbrains.java3.lesson7.tests.TestSet2;
import ru.geekbrains.java3.lesson7.tests.TestSet3;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Homework7 {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("**************************** TestSet1 ****************************");
        start(TestSet1.class);
        System.out.println("**************************** TestSet2 ****************************");
        try {
            start(TestSet2.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread.sleep(1000);
        System.out.println("**************************** TestSet3 ****************************");
        try {
            start(TestSet3.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread.sleep(1000);
        System.out.println("**************************** TestSet4 ****************************");
        start("ru.geekbrains.java3.lesson7.tests.TestSet4");
    }

    public static void start(Class testClass) {
        Method beforeMethod = defineBeforeMethod(testClass);
        Method afterMethod = defineAfterMethod(testClass);
        Object instance = null;
        try {
            instance = executeMethod(testClass, beforeMethod, instance);
            instance = executeTests(testClass, instance);
            executeMethod(testClass, afterMethod, instance);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | InstantiationException e) {
            System.err.println("сбой функции start");
            e.printStackTrace();
        }
    }

    public static void start(String className) {
        try {
            Class clazz = Class.forName(className);
            start(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Object executeMethod(Class testClass, Method method, Object instance)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        if (method != null) {
            method.setAccessible(true);
            if (Modifier.isStatic(method.getModifiers())) {
                method.invoke(null, null);
            } else {
                if (instance == null) {
                    instance = testClass.getDeclaredConstructor().newInstance();
                }
                method.invoke(instance, null);
            }
        }
        return instance;
    }

    private static Method defineAfterMethod(Class testClass) {
        Method afterMethod = null;
        int afterAnnotationQuantity = 0;

        for (Method method : testClass.getDeclaredMethods()) {
            if (method.getAnnotation(AfterSuite.class) != null) {
                if (++afterAnnotationQuantity > 1) {
                    throw new RuntimeException("Найдено более одной аннотации @AfterSuite");
                } else {
                    afterMethod = method;
                }
            }
        }
        return afterMethod;
    }

    private static Method defineBeforeMethod(Class testClass) {
        Method beforeMethod = null;
        int beforeAnnotationQuantity = 0;

        for (Method method : testClass.getDeclaredMethods()) {
            if (method.getAnnotation(BeforeSuite.class) != null) {
                if (++beforeAnnotationQuantity > 1) {
                    throw new RuntimeException("Найдено более одной аннотации @BeforeSuite");
                } else {
                    beforeMethod = method;
                }
            }
        }
        return beforeMethod;
    }

    private static Object executeTests(Class testClass, Object instance)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        List<Method> tests = new ArrayList<>();

        for (Method method : testClass.getDeclaredMethods()) {
            if (method.getAnnotation(Test.class) != null) {
                tests.add(method);
            }
        }

        tests.sort(new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                return Integer.compare(o2.getAnnotation(Test.class).priority(), o1.getAnnotation(Test.class).priority());
            }
        });

        for (Method test : tests) {
            instance = executeMethod(testClass, test, instance);
        }

        return instance;
    }

}
