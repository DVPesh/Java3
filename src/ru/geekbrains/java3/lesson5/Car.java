package ru.geekbrains.java3.lesson5;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public class Car implements Runnable {

    private static int carsCount;
    private Race race;
    private int speed;
    private String name;

    private final static ReentrantLock lock = new ReentrantLock();

    private final static CyclicBarrier barrier = new CyclicBarrier(Homework5.CAR_QUANTITY, Homework5.barrierAction);

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public Car(Race race, int speed) {
        this.race = race;
        this.speed = speed;
        carsCount++;
        this.name = "Участник #" + carsCount;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int) (Math.random() * 800));
            System.out.println(this.name + " готов");
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        for (Stage stage : race.getStages()) stage.go(this);
        if (lock.tryLock()) System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> " + this.name + " - ПОБЕДИТЕЛЬ!!!");
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        if (lock.isHeldByCurrentThread()) lock.unlock();
    }

}
