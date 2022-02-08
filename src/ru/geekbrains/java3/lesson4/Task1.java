package ru.geekbrains.java3.lesson4;

public class Task1 {

    private static final int SEQUENCE_LENGTH = 5;
    private volatile char activeSign = 'A';

    public static void main(String[] args) {

        Task1 instance = new Task1();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    instance.displayA();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    instance.displayB();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    instance.displayC();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private synchronized void displayA() throws InterruptedException {
        for (int i = 0; i < SEQUENCE_LENGTH; i++) {
            while (activeSign != 'A') wait();
            System.out.print("A");
            activeSign = 'B';
            notifyAll();
        }
    }

    private synchronized void displayB() throws InterruptedException {
        for (int i = 0; i < SEQUENCE_LENGTH; i++) {
            while (activeSign != 'B') wait();
            System.out.print("B");
            activeSign = 'C';
            notifyAll();
        }
    }

    private synchronized void displayC() throws InterruptedException {
        for (int i = 0; i < SEQUENCE_LENGTH; i++) {
            while (activeSign != 'C') wait();
            System.out.print("C");
            activeSign = 'A';
            notifyAll();
        }
    }

}
