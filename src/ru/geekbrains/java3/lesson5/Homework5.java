package ru.geekbrains.java3.lesson5;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Homework5 {

    public static final int CAR_QUANTITY = 4;
    public static Runnable barrierAction;
    private static boolean raceFinish;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(CAR_QUANTITY);
        barrierAction = Homework5::printStartFinishMessage;
        Race race = new Race(new Road(120), new Tunnel(), new Road(40));
        printInfo(race);
        Car[] cars = new Car[CAR_QUANTITY];
        for (int i = 0; i < cars.length; i++) cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        for (Car car : cars) executorService.execute(car);
        executorService.shutdown();
    }

    private static void printStartFinishMessage() {
        if (!raceFinish) {
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
            raceFinish = true;
        } else {
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
        }
    }

    private static void printInfo(Race race) {
        System.out.println("** ЭТАПЫ ГОНКИ **");
        for (Stage stage : race.getStages()) {
            System.out.println(stage.getDescription());
        }
    }
}
