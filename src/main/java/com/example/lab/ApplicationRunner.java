package com.example.lab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Path;

public class ApplicationRunner extends Application {

    public static void main(String[] args) {
        launch();
    }

    public static FXMLLoader fxmlLoader;
    public static Scene sceneSimulation;
    public static Scene sceneMenu;
    public static Scene sceneSettings;

    @Override
    public void start(Stage stage) {
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("application-main-view.fxml"));
            sceneSimulation = new Scene(fxmlLoader.load());
            Habitat habitat = fxmlLoader.getController();

            fxmlLoader = new FXMLLoader(getClass().getResource("application-menu-view.fxml"));
            sceneMenu = new Scene(fxmlLoader.load());

            fxmlLoader = new FXMLLoader(getClass().getResource("application-settings-view.fxml"));

            BorderPane layoutSettings = FXMLLoader.load(
                    new URL(ApplicationRunner.class.getResource("application-settings-view.fxml")
                            .toExternalForm())
            );
            sceneSettings = new Scene(layoutSettings);

            sceneSimulation.setOnKeyPressed(keyEvent -> {
                switch (keyEvent.getCode()) {
                    case B -> habitat.start();
                    case E -> habitat.end();
                    case T -> habitat.showTime();
                }
            });

            stage.setTitle("Program");
            stage.getIcons().add(new Image(Path.of("src", "main", "resources",
                            "com", "example", "lab", "photo", "developer.png")
                    .toFile().toURI().toString()));

//            stage.setScene(sceneMenu);
//            stage.setScene(sceneSettings);
            stage.setScene(sceneSimulation);
            stage.show();
            stage.setResizable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }
}
