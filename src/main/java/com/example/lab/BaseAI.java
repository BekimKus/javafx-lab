package com.example.lab;

public abstract class BaseAI extends Thread {

    private double speed;

    public BaseAI(int speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    public void setLowPriority() {
        setPriority(2);
        System.out.println(getName() + " " + getPriority());
    }

    public void setMediumPriority() {
        setPriority(5);
        System.out.println(getName() + " " + getPriority());
    }

    public void setHighPriority() {
        setPriority(8);
        System.out.println(getName() + " " + getPriority());
    }
}
