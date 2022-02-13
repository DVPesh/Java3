package ru.geekbrains.java3.lesson5;

import java.util.concurrent.Semaphore;

public class Tunnel extends Stage {

    private final Semaphore semaphore;
    private final int carLimit;

    public Tunnel(int carLimit) {
        this.carLimit = carLimit;
        this.length = 80;
        this.description = "Тоннель " + length + " метров";
        this.semaphore = new Semaphore(carLimit, true);
    }

    public Tunnel() {
        this(Homework5.CAR_QUANTITY / 2);
    }

    @Override
    public void go(Car c) {
        try {
            System.out.println(c.getName() + " готовится к этапу(ждет): " + description);
            semaphore.acquire();
            System.out.println(c.getName() + " начал этап: " + description);
            Thread.sleep((long) length / c.getSpeed() * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println(c.getName() + " закончил этап: " + description);
            semaphore.release();
        }
    }

    @Override
    public String getDescription() {
        return description + ". В тоннель не может одновременно заехать больше " + carLimit + " участников";
    }
}
