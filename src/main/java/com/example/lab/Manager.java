package com.example.lab;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.nio.file.Path;

public class Manager extends Employee implements  IBehaviour{

    public Manager(double x, double y, double height, double width) {
        super(x, y, height, width);
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

}
