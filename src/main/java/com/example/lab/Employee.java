package com.example.lab;

import javafx.scene.image.ImageView;

public abstract class Employee {

    private double x,y;
    private double height,width;
    private ImageView imageView;
    private static int count = 0;

    public Employee(double x, double y, double height, double weight){
        this.x=x;
        this.y=y;
        this.height=height;
        this.width=width;
        imageView=new ImageView();
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
}
