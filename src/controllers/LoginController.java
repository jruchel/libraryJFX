package controllers;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import tasks.UserDataRetrievalTask;
import utils.Properties;
import utils.fxUtils.AlertUtils;
import utils.fxUtils.SceneController;
import web.Requests;
import web.TaskRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginController extends Controller {

    protected static String backgroundColor = "#221C35";
    @FXML
    protected CheckBox rememberCheckBox;
    @FXML
    protected TextField usernameField;

    @FXML
    protected ProgressIndicator progressIndicator;

    @FXML
    protected PasswordField passwordField;

    @FXML
    protected Button loginButton;

    @FXML
    protected Button registerButton;

    @FXML
    protected AnchorPane loginPane;

    protected Requests requests;

    public void initialize() {
        setBackground("file:src/resources/images/background.png", loginPane, 1500, 685);
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
        setKeyPresses(loginPane, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                login();
            }
        });
        try {
            setFont(Button.class, Font.font (globalFontFamily, 14));
            setFont(Label.class, Font.font (globalFontFamily, 14));
        } catch (Exception ignored) {
        }
    }


    public void register() {
        performLogin(true);
    }

    protected void performLogin(boolean register) {
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
