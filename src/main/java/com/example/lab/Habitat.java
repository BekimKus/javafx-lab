package com.example.lab;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.Event;
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
    public TextField textFieldDeveloperAppearancePeriod;
    @FXML
    public TextField textFieldManagerAppearancePeriod;
    @FXML
    public TextField textFieldManagerToDevRatio;
    @FXML
    public TextField textFieldDeveloperTimeToLive;
    @FXML
    public TextField textFieldManagerTimeToLive;


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
    private Label labelDeveloper;
    @FXML
    private Label labelManager;
    @FXML
    private ToggleSwitch toggleSwitchShowClock;
    @FXML
    private ToggleSwitch toggleSwitchHideClock;
    @FXML
    public Button buttonGetInformation;
    @FXML
    public MenuBar menuBar = new MenuBar();
    @FXML
    public CheckMenuItem checkMenuDevelopersAnimation;
    @FXML
    public CheckMenuItem checkMenuManagersAnimation;
    @FXML
    public CheckMenuItem checkDevelopersPriorityLow;
    @FXML
    public CheckMenuItem checkDevelopersPriorityMedium;
    @FXML
    public CheckMenuItem checkDevelopersPriorityHigh;
    @FXML
    public CheckMenuItem checkManagersPriorityLow;
    @FXML
    public CheckMenuItem checkManagersPriorityMedium;
    @FXML
    public CheckMenuItem checkManagersPriorityHigh;

    private Stage stage;

    private static long timeStart;
    private Timeline clock;
    private TextArea textArea;
    private Timer timer;

    private int developerCountFull = 0;
    private int managerCountFull = 0;
    private int developerCountCurrent = 0;
    private int managerCountCurrent = 0;

    private static int DevProb = 4;
    private static int ManagerProb = 5;
    private static double K = 0.5;
    private static double appearanceProb = 0.9;
    private static int developerTimeToLive = 15;
    private static int managerTimeToLive = 15;
    private boolean isEnd = true;

    private List <Employee> employees = new LinkedList<>();
    private Map<Integer, Integer> employeesAppearanceTime = new HashMap<>();
    private Set<Integer> employeeID = new TreeSet<>();

    private List<DeveloperThread> developerThreads = new LinkedList<>();
    private List<ManagerThread> managerThreads = new LinkedList<>();

    /**
     * Метод переключает текущую сцену на сцену с главным менюю.
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
     * Метод переключает текущую сцену на сцену с симуляцией.
     * @param event
     * @throws IOException
     */
    public void switchToMainScene(Event event) throws IOException {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(ApplicationRunner.sceneSimulation);
        stage.show();
    }

    /**
     * Метод переключает текущую сцену на сцену с настройками. Если симуляция запущена, то она останавливается
     * и показывает информацию об объектах.
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

    /**
     * Метод для запуска симуляции.
     */
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
                        labelDeveloper.setText("Developers: " + developerCountCurrent);
                        labelManager.setText("Managers: " + managerCountCurrent);
                    }
                    catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                });
            }
        },1000,1000);
    }

    /**
     * Метод завершения работы симуляции.
     */
    public void end(){
        if (isEnd){
            return;
        }

        isEnd = true;
        labelSimulation.setText("Simulation disabled");
        timer.cancel();
        timer = null;
        clock.stop();
        deleteEmployee(true);

        showResultAlert();

        developerCountFull = 0;
        managerCountFull = 0;
        developerCountCurrent = 0;
        managerCountCurrent = 0;
    }

    /**
     * Метод, привязанный ко времени для создания и отображения объектов в симуляции.
     * @throws URISyntaxException
     */
    public void update() throws URISyntaxException {
        if ((getSecond() % DevProb == 0) && (Math.random() <= appearanceProb)) {
            Employee developer = new Developer(getRandom(1), getRandom(30), 50,
                    50, developerTimeToLive);
            developer.setAppearanceTime(getTimeInSeconds());
            employees.add(developer);
            anchorPane.getChildren().add(developer.getImageView());
            developerCountFull++;
            developerCountCurrent++;

            employeeID.add(developer.hashCode());
            employeesAppearanceTime.put(developer.hashCode(), developer.getAppearanceTime());


            DeveloperThread developerThread = new DeveloperThread((Developer) developer, 5, 5);
            developerThread.start();
            developerThreads.add(developerThread);
        }
        if ((getSecond() % ManagerProb == 0) && ((managerCountFull + 1) / (double) (developerCountFull)) < K
                && developerCountFull != 0){
            Employee manager = new Manager(getRandom(80), getRandom(50), 50,
                    50, managerTimeToLive);
            manager.setAppearanceTime(getTimeInSeconds());
            employees.add(manager);
            anchorPane.getChildren().add(manager.getImageView());
            managerCountFull++;
            managerCountCurrent++;

            employeeID.add(manager.hashCode());
            employeesAppearanceTime.put(manager.hashCode(), manager.getAppearanceTime());

            ManagerThread managerThread = new ManagerThread((Manager) manager, 5);
            managerThread.start();
            managerThreads.add(managerThread);
        }

        if (textArea != null) {
            textArea.setText(getInformation());
        }

        showOrHideClock();

        setDeveloperThreadsAnimationRunning();
        setManagersThreadsAnimationRunning();

        deleteEmployee(false);
    }

    /**
     * Вывод диалогового окна с информацией об объектах и времени.
     */
    public void showResultAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Result");
        alert.setHeaderText(null);
        textArea = new TextArea(getInformation());
        textArea.setEditable(false);
        textArea.setFont(new Font("Calibri", 20));
        textArea.setPrefSize(400, 300);

        alert.getDialogPane().setContent(textArea);
        alert.getButtonTypes();

        Optional<ButtonType> optional = alert.showAndWait();
        if (optional.get() == ButtonType.CANCEL) {
            end();
        }
    }

    /**
     * Метод который необходим для принятия настроек.
     */
    public void applySettings() {
        String text = textFieldDeveloperAppearancePeriod.getText();
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

        text = textFieldManagerAppearancePeriod.getText();
        if (Pattern.matches("^[0-9]{1,3}$", text)) {
            ManagerProb = Integer.parseInt(text);
        } else {
            isError = true;
            errorMessage += "Incorrect Manager prob\n";
        }

        text = textFieldManagerToDevRatio.getText();
        //^([1-9]\d*\.\d*|0\.\d*[1-9]\d*)|0?\.0+|0$
        if (Pattern.matches("^(0\\.\\d+)|(0?\\.0+)|0|1|(1\\.0+)$", text)) {
            K = Double.parseDouble(text);
        } else {
            isError = true;
            errorMessage += "Incorrect Manager to developer ratio\n";
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

        text = textFieldDeveloperTimeToLive.getText();
        if (Pattern.matches("^[0-9]{1,3}$", text)) {
            developerTimeToLive = Integer.parseInt(text);
        } else {
            isError = true;
            errorMessage += "Incorrect Developer Time To Live\n";
        }

        text = textFieldManagerTimeToLive.getText();
        if (Pattern.matches("^[0-9]{1,3}$", text)) {
            managerTimeToLive = Integer.parseInt(text);
        } else {
            isError = true;
            errorMessage += "Incorrect Manager Time To Live\n";
        }

        if (isError) {
            showErrorDialogAlert("Settings", errorMessage);
        }
    }

    public void setDeveloperThreadsAnimationRunning() {
        if (checkMenuDevelopersAnimation.isSelected()) {
            developerThreads
                    .forEach(DeveloperThread::setPlay);
        } else {
            developerThreads
                    .forEach(DeveloperThread::setPause);
        }
    }

    public void setManagersThreadsAnimationRunning() {
        if (checkMenuManagersAnimation.isSelected()) {
            managerThreads
                    .forEach(ManagerThread::setPlay);
        } else {
            managerThreads
                    .forEach(ManagerThread::setPause);
        }
    }

    public void setDevelopersPriorityLow() {
        developerThreads.forEach(DeveloperThread::setLowPriority);
        checkDevelopersPriorityMedium.setSelected(false);
        checkDevelopersPriorityHigh.setSelected(false);
    }

    public void setDevelopersPriorityMedium() {
        developerThreads.forEach(DeveloperThread::setMediumPriority);
        checkDevelopersPriorityLow.setSelected(false);
        checkDevelopersPriorityHigh.setSelected(false);
    }

    public void setDevelopersPriorityHigh() {
        developerThreads.forEach(DeveloperThread::setHighPriority);
        checkDevelopersPriorityLow.setSelected(false);
        checkDevelopersPriorityMedium.setSelected(false);
    }

    public void setManagersPriorityLow() {
        managerThreads.forEach(ManagerThread::setLowPriority);
        checkManagersPriorityMedium.setSelected(false);
        checkManagersPriorityHigh.setSelected(false);
    }

    public void setManagersPriorityMedium() {
        managerThreads.forEach(ManagerThread::setMediumPriority);
        checkManagersPriorityLow.setSelected(false);
        checkManagersPriorityHigh.setSelected(false);
    }

    public void setManagersPriorityHigh() {
        managerThreads.forEach(ManagerThread::setHighPriority);
        checkManagersPriorityLow.setSelected(false);
        checkManagersPriorityMedium.setSelected(false);
    }

    /**
     * Метод инициализации, необходимый для создания комбобокса, в котором можно выбрать вероятности появления
     * обхектов.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assert comboBoxAppearanceProbability != null : "fx:id=\"comboBoxAppearanceProbability\" " +
                "was not injected: check your FXML file 'comboBoxAppearanceProbability.fxml'.";
        comboBoxAppearanceProbability.getItems().setAll(
                FXCollections.observableList(List.of(
                        "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"
                ))
        );

        menuBar.setFocusTraversable(true);
    }


    /**
     * Метод переключащий объекты ToggleSwitch в противоположные друг от друга значения.
     */
    public void showTime(){
        toggleSwitchShowClock.setSelected(!toggleSwitchShowClock.isSelected());
        toggleSwitchHideClock.setSelected(!toggleSwitchShowClock.isSelected());
    }

    /**
     * Метод показывает или скрывает таймер, в зависимости от текущего значения отображения.
     */
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

    /**
     * Инициализация таймера, который показывает время прошедшее с момента начала работы симуляции.
     */
    private void initClock() {
        clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss");
            labelClock.setText(LocalDateTime.now().minusSeconds(timeStart / 1000).format(formatter));
            labelClock.setBackground(new Background(new BackgroundFill(Color.rgb(0, 220, 220, 0.85),
                    new CornerRadii(0),
                    new Insets(-5, 0, 0, 0))));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    /**
     * Выводит диалоговое окно, показывающее ошибки.
     * @param place
     * @param message
     */
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

    /**
     * Удаление объектов симуляции и очистка объектов классов-коллекций.
     * @param deleteAll false - удаляет только те объекты, чье время жизни истекло. True - удаляет все объекты
     */
    private void deleteEmployee(boolean deleteAll) {
        if (!deleteAll) {
            ListIterator<Employee> iterator = employees.listIterator();
            while (iterator.hasNext()) {
                Employee employee = iterator.next();

                if (employee.getTimeToLive() == 0) {
                    continue;
                }

                if (employee.getTimeToLive() + employeesAppearanceTime.get(employee.hashCode()) ==
                        getTimeInSeconds()) {

                    if (employee instanceof Developer) {
                        developerCountCurrent--;
                    } else if (employee instanceof Manager) {
                        managerCountCurrent--;
                    }

                    if (developerThreads.contains(employee)) {
                        developerThreads.get(developerThreads.indexOf(employee)).getTransition().stop();
                        developerThreads.get(developerThreads.indexOf(employee)).interrupt();
                        developerThreads.remove(employee);
                    }
                    if (managerThreads.contains(employee)) {
                        managerThreads.get(managerThreads.indexOf(employee)).getPathTransitionCircle().stop();
                        managerThreads.get(managerThreads.indexOf(employee)).interrupt();
                        managerThreads.remove(employee);
                    }

                    anchorPane.getChildren().remove(employee.getImageView());
                    employeeID.remove(employee.hashCode());
                    employeesAppearanceTime.remove(employee.hashCode());
                    iterator.remove();
                }
            }
        } else {
            for (Employee employee : employees){
                anchorPane.getChildren().remove(employee.getImageView());
            }
            employees.clear();
            employeeID.clear();
            employeesAppearanceTime.clear();

            for (DeveloperThread thread : developerThreads) {
                thread.getTransition().stop();
                    thread.setEnd(true);
            }
            for (ManagerThread thread : managerThreads) {
                thread.getPathTransitionCircle().stop();
                thread.interrupt();
            }
            developerThreads.clear();
            managerThreads.clear();
        }
    }


    /**
     * Возвращает количество секунд, прощедших с момента начала работы симуляции.
     * @return
     */
    private int getSecond() {
        return LocalDateTime.now().minusSeconds(timeStart / 1000).getSecond();
    }

    /**
     *
     * @return Время, прошедшее с момента начала работы симуляции, в секундах.
     */
    public static int getTimeInSeconds() {
        return LocalDateTime.now().minusSeconds(timeStart / 1000).getMinute() * 60 +
                LocalDateTime.now().minusSeconds(timeStart / 1000).getSecond();
    }

    /**
     * Возвращает рандомное число с диапозоном, соответствующему размеру области отображения картинок
     * с объектами.
     * @return
     */
    private double getRandom(int origin){
        return new Random().nextDouble(origin, anchorPane.getPrefWidth() - 50);
    }

    /**
     *
     * @return Строковое представление всей информации о работе симуляции
     */
    private String getInformation() {
        return "Current object information\n" +
                labelDeveloper.getText() + "\n" + labelManager.getText() +
                "\n---------------------------------\n" +
                "Object information for all time\n" +
                "Developers: " + developerCountFull +
                "\nManagers: " + managerCountFull + "\nTime: " + labelClock.getText();
    }
}

