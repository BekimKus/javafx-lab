package com.example.lab;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.ToggleSwitch;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Habitat {
    @FXML
    public Button buttonSwitchMainToMenuScene;
    @FXML
    public Button buttonSwitchMainToSettingsScene;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label labelClock = new Label();
    @FXML
    private Label labelSimulation;
    @FXML
    private Label labelProgrammer;
    @FXML
    private Label labelManager;
    @FXML
    private ToggleSwitch toggleSwitchShowClock;
    @FXML
    private ToggleSwitch toggleSwitchHideClock;
    @FXML
    public Button buttonGetInformation;

    private Stage stage;

    private static long timeStart;
    private Timeline clock;
    private TextArea textArea;

    private Timer timer;
    private int developerCount = 0;
    private int managerCount = 0;

    private static double N1 = 4;
    private static double N2 = 5;
    private static final double K = 0.5;
    private static final double P1 = 0.9;
    private boolean isEnd = true;
    private ArrayList <Employee> employees = new ArrayList<>();


    public void switchToMenuScene(Event event) throws IOException {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        if (stage.getScene().equals(ApplicationRunner.sceneSimulation)) {
            end();
        }
        stage.setScene(ApplicationRunner.sceneMenu);
        stage.show();
    }

    public void switchToMainScene(Event event) throws IOException {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(ApplicationRunner.sceneSimulation);
        stage.show();
    }

    public void switchToSettingsScene(Event event) throws IOException {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        if (stage.getScene().equals(ApplicationRunner.sceneSimulation)) {
            end();
        }
        stage.setScene(ApplicationRunner.sceneSettings);
        stage.show();
    }

    public void start()  {

        if (!isEnd){
            return;
        }

        timeStart = System.currentTimeMillis();
        initClock();
        isEnd = false;
        labelSimulation.setText("Simulation enabled");

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        update();
                        labelProgrammer.setText("Developers: " + developerCount);
                        labelManager.setText("Managers: " + managerCount);
                    }
                    catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                });
            }
        },1000,1000);

    }

    public void end(){
        if (isEnd){
            return;
        }

        isEnd = true;
        labelSimulation.setText("Simulation disabled");
        timer.cancel();
        timer = null;
        clock.stop();
        showResultAlert();

        for (Employee worker : employees){
            anchorPane.getChildren().remove(worker.getImageView());
        }

        developerCount = 0;
        managerCount = 0;
        employees.clear();
    }

    public void update() throws URISyntaxException {
        if ((getSecond() % N1 == 0) && (Math.random() <= P1)) {
            Employee programmer = new Developer(getRandom(), getRandom(), 50, 50);
            employees.add(programmer);
            anchorPane.getChildren().add(programmer.getImageView());
            developerCount++;
        }
        if ((getSecond() % N2 == 0) && ((managerCount + 1) / (double) (developerCount)) < K && developerCount != 0){
            Employee manager = new Manager(getRandom(), getRandom(), 50, 50);
            employees.add(manager);
            anchorPane.getChildren().add(manager.getImageView());
            managerCount++;
        }

        if (textArea != null) {
            textArea.setText(labelProgrammer.getText() + "\n" + labelManager.getText() + "\nTime: " +
                    labelClock.getText());
        }

        showOrHideClock();
    }

    public void showResultAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Result");
        alert.setHeaderText(null);
        textArea = new TextArea(labelProgrammer.getText() + "\n" + labelManager.getText() + "\nTime: " +
                labelClock.getText());
        textArea.setEditable(false);
        textArea.setFont(new Font("Calibri", 20));
        textArea.setPrefSize(400, 200);

        alert.getDialogPane().setContent(textArea);
        alert.getButtonTypes();

        Optional<ButtonType> optional = alert.showAndWait();
        if (optional.get() == ButtonType.CANCEL) {
            end();
        }
    }

    public void showTime(){
        toggleSwitchShowClock.setSelected(!toggleSwitchShowClock.isSelected());
        toggleSwitchHideClock.setSelected(!toggleSwitchShowClock.isSelected());
    }

    private void showOrHideClock() {
        toggleSwitchShowClock.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            labelClock.setVisible(toggleSwitchShowClock.isSelected());
            toggleSwitchHideClock.setSelected(!labelClock.isVisible());
        });

        toggleSwitchHideClock.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            labelClock.setVisible(!toggleSwitchHideClock.isSelected());
            toggleSwitchShowClock.setSelected(labelClock.isVisible());
        });
    }

    private void initClock() {
        clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss");
            labelClock.setText(LocalDateTime.now().minusSeconds(timeStart / 1000).format(formatter));
            labelClock.setBackground(new Background(new BackgroundFill(Color.rgb(0, 220, 220, 0.85),
                    new CornerRadii(0),
                    new Insets(-5, 0, 0, 0))));
        }), new KeyFrame(Duration.seconds(0.5)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private int getSecond() {
        return LocalDateTime.now().minusSeconds(timeStart / 1000).getSecond();
    }

    private double getRandom(){
        return new Random().nextDouble(1, anchorPane.getPrefWidth() - 50);
    }

    private class SwitchFromMainSceneButtonHandler implements EventHandler<Event> {
        @Override
        public void handle(Event evt) {
            if (evt.getSource().equals(buttonSwitchMainToMenuScene)) {
                try {
                    end();
                    switchToMenuScene(evt);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (evt.getSource().equals(buttonSwitchMainToSettingsScene)) {
                try {
                    end();
                    switchToSettingsScene(evt);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

