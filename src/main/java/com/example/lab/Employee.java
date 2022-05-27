package com.example.lab;

import javafx.scene.image.ImageView;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public abstract class Employee implements Serializable {

    @Serial
    private static final long serialVersionUID = 5351382337742196675L;

    private double x,y;
    private double height,width;
    private ImageView imageView;
    private static int count = 0;
    private int timeToLive;
    private int appearanceTime;
    private boolean isLive;

    public Employee(double x, double y, double height, double width, int timeToLive){
        this.x = x;
        this.y = y;
        this.height = height;
        this.timeToLive = timeToLive;
        this.width = width;
        imageView = new ImageView();
        count++;
    }


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public static int getCount() {
        return count;
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public int getAppearanceTime() {
        return appearanceTime;
    }

    public void setX(double x) {
        this.x=x;
    }

    public void setY(double y)  {
        this.y=y;
    }

    public void setHeight(double height){
        this.height=height;
    }

    public void setWidth(double width){
        this.width=width;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public void setAppearanceTime(int appearanceTime) {
        this.appearanceTime = appearanceTime;
    }

    public void setVisible(boolean visible) {
        imageView.setVisible(visible);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Double.compare(employee.x, x) == 0 &&
                Double.compare(employee.y, y) == 0 &&
                Double.compare(employee.height, height) == 0 &&
                Double.compare(employee.width, width) == 0 &&
                timeToLive == employee.timeToLive &&
                appearanceTime == employee.appearanceTime &&
                imageView.equals(employee.imageView);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, height, width, imageView, timeToLive, appearanceTime);
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }
}
