package com.example.lab;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.nio.file.Path;

public class Developer extends Employee implements IBehaviour {

    public Developer(double x, double y, double height, double width) {
        super(x, y, height, width);
        File file = Path.of("src", "main", "resources", "com", "example", "lab", "photo", "developer.png")
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
        System.out.println("Overrided interface method in Developer class");
    }

}
