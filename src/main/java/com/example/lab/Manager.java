package com.example.lab;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;

public class Manager extends Employee implements  IBehaviour, Serializable {

    @Serial
    private static final long serialVersionUID = 109580409452272812L;

    public Manager(double x, double y, double height, double width, int timeToLive) {
        super(x, y, height, width, timeToLive);
        File file = Path.of("src", "main", "resources", "com", "example", "lab", "photo", "manager.jpg")
                .toFile();
        ImageView imageView = new ImageView();

        imageView.setImage(new Image(file.toURI().toString()));
        imageView.setX(x);
        imageView.setY(y);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        imageView.setPreserveRatio(false);

        setImageView(imageView);
    }

    @Override
    public void Transparency() {
        System.out.println("Overrided interface method in Manager class");
    }

    @Override
    public String toString() {
        return "Manager{" + getImageView().toString() + ", " +
                getAppearanceTime() + "}";
    }
}
