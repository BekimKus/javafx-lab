package com.example.lab;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
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
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class Habitat implements Initializable {
    /**
     * Settings elements
     */
    @FXML
    public ComboBox<String> comboBoxAppearanceProbability = new ComboBox<>();
    @FXML
    public TextArea textAreaDeveloperAppearancePeriod;
    @FXML
    public TextArea textAreaManagerAppearancePeriod;

    /**
     * Simulation elements and parameters
     */
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

    private static int DevProb = 4;
    private static int ManagerProb = 5;
    private static final double K = 0.5;
    private static double appearanceProb = 0.9;
    private boolean isEnd = true;
    private ArrayList <Employee> employees = new ArrayList<>();

//    private ObservableList<String> observableList = FXCollections.observableList(List.of(
//            "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"
//            ));

    /**
     * Method switch current scene to menu scene
     * @param event
     * @throws IOException
     */
    public void switchToMenuScene(Event event) throws IOException {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        if (stage.getScene().equals(ApplicationRunner.sceneSimulation)) {
            end();
        }
        stage.setScene(ApplicationRunner.sceneMenu);
        stage.show();
    }

    /**
     * Method switch current scene to Simulation or Main scene
     * @param event
     * @throws IOException
     */
    public void switchToMainScene(Event event) throws IOException {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(ApplicationRunner.sceneSimulation);
        stage.show();
    }

    /**
     * Method switch current scene to settings scene if simulation is going
     * then it's ending
     * @param event
     * @throws IOException
     */
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
        if ((getSecond() % DevProb == 0) && (Math.random() <= appearanceProb)) {
            Employee programmer = new Developer(getRandom(), getRandom(), 50, 50);
            employees.add(programmer);
            anchorPane.getChildren().add(programmer.getImageView());
            developerCount++;
        }
        if ((getSecond() % ManagerProb == 0) && ((managerCount + 1) / (double) (developerCount)) < K && developerCount != 0){
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

    public void applySettings() {
        String text = textAreaDeveloperAppearancePeriod.getText();
        String errorMessage = "";
        boolean isError = false;

//        for spawn no longer then one time per minute
//        Pattern.matches("^[0-9]|([0-5][0-9])$", text)

        if (Pattern.matches("^[0-9]{1,3}$", text)) {
            DevProb = Integer.parseInt(text);
        } else {
            isError = true;
            errorMessage += "Incorrect Dev prob\n";
        }

        text = textAreaManagerAppearancePeriod.getText();
        if (Pattern.matches("^[0-9]{1,3}$", text)) {
            ManagerProb = Integer.parseInt(text);
        } else {
            isError = true;
            errorMessage += "Incorrect Manager prob\n";
        }

        comboBoxAppearanceProbability.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, s, t1) -> {
                    String value = observableValue.getValue();
                    comboBoxAppearanceProbability.setPromptText(value);
                    System.out.print(value + " ");
                    value = value.substring(0, value.indexOf("%"));
                    appearanceProb = Double.parseDouble(value) / 100;
                    System.out.println(appearanceProb);
                });

        if (isError) {
            showErrorDialogAlert("Settings", errorMessage);
        }
    }

    private int getSecond() {
        return LocalDateTime.now().minusSeconds(timeStart / 1000).getSecond();
    }

    private double getRandom(){
        return new Random().nextDouble(1, anchorPane.getPrefWidth() - 50);
    }

    private void showErrorDialogAlert(String place, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Place: " + place);
        alert.setContentText("Message:\n" + message);

        Optional<ButtonType> optional = alert.showAndWait();
        if (optional.get() == ButtonType.OK) {
            alert.close();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assert comboBoxAppearanceProbability != null : "fx:id=\"comboBoxAppearanceProbability\" " +
                "was not injected: check your FXML file 'comboBoxAppearanceProbability.fxml'.";
        comboBoxAppearanceProbability.getItems().setAll(
                FXCollections.observableList(List.of(
                        "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"
                ))
        );
    }

    /**
     * This inner class is existing to set action to UI elements by overriding handle method for event
     */
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

