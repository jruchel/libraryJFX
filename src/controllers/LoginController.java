package controllers;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import web.Requests;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import tasks.UserDataRetrievalTask;

import utils.Properties;
import utils.fxUtils.AlertUtils;
import utils.fxUtils.SceneController;
import web.TaskRunner;

import java.io.IOException;
import java.util.*;

public class LoginController extends Controller {

    private static String backgroundColor = "#221C35";
    @FXML
    private CheckBox rememberCheckBox;
    @FXML
    private TextField usernameField;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private AnchorPane loginPane;

    private Requests requests;

    public void initialize() {
        Background background = getBackground(backgroundColor);
        loginPane.setBackground(background);
        rememberCheckBox.setTextFill(Color.WHITE);
        try {
            String username = Properties.getProperty("username");
            if (!username.isEmpty()) {
                usernameField.setText(username);
                String password = Properties.getProperty("password");
                if (!password.isEmpty()) passwordField.setText(password);
            }
        } catch (IOException e) {
            AlertUtils.showAlert("Could not find site address");
            System.exit(0);
        }
        this.requests = Requests.getInstance();
        initializeManually();
    }

    public void register() {
        performLogin(true);
    }

    private void performLogin(boolean register) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Map<String, String> properties = new HashMap<>();

        properties.put("username", username);
        properties.put("password", password);
        properties.put("passwordConfirm", password);

        final boolean[] loggedIn = {false};

        progressIndicator.setVisible(true);

        Map<String, Object> parameters = new HashMap<>();

        //Pobieranie danych uzytkownika z api
        UserDataRetrievalTask getUserData = new UserDataRetrievalTask(parameters);

        //Logowanie uzytkownika z podanymi w aplikacji danymi
        Runnable loginRequest = () -> {
            try {
                loggedIn[0] = (requests.sendRequest(String.format("%s%s", appURL, register ? "/registration" : "/temp/login"), properties, "POST").equals("true"));
            } catch (IOException ignored) {
            }
        };
        int startTime = (int) System.nanoTime();

        //To jest wywolywane po zakonczeniu poprzednich zadan
        Runnable onTaskComplete = () -> {
            if (loggedIn[0]) {
                if (rememberCheckBox.isSelected()) {
                    try {
                        Properties.editProperty("username", properties.get("username"));
                        Properties.editProperty("password", properties.get("password"));
                    } catch (IOException e) {
                        AlertUtils.showAlert("Failed to save username");
                    }
                }
                Platform.runLater(() -> {
                    try {
                        //Jesli zalogowano pomyslnie, wyswietlanie sceny z panelem uzytkownika
                        SceneController.startScene("userPane");
                        int endTime = (int) System.nanoTime();
                        System.out.printf("Time to load: %.2fs\n", (endTime - startTime) / Math.pow(10, 9));
                    } catch (IOException e) {
                        System.out.printf("Failed to log in, reason: %s", e.getMessage());
                    }
                });
            } else {
                Platform.runLater(() -> AlertUtils.showAlert("Failed to log in"));

            }
            progressIndicator.setVisible(false);
        };

        TaskRunner taskRunner = new TaskRunner(Arrays.asList(loginRequest, getUserData), onTaskComplete, true);
        taskRunner.run();
    }

    public void login() {
        performLogin(false);
    }

}
