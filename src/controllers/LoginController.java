package controllers;

import connection.Requests;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import tasks.UserDataRetrievalTask;

import utils.Properties;
import utils.fxUtils.SceneController;
import utils.TaskRunner;

import java.io.IOException;
import java.util.*;

public class LoginController {

    private String appUrl;
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

    private Requests requests;

    public void initialize() {
        try {
            appUrl = Properties.getProperty("site.url");
            String username = Properties.getProperty("username");
            if (!username.isEmpty()) {
                usernameField.setText(username);
                String password = Properties.getProperty("password");
                if (!password.isEmpty()) passwordField.setText(password);
            }
        } catch (IOException e) {
            System.out.println("Could not find site address");
            System.exit(0);
        }
        this.requests = Requests.getInstance();
    }

    public void register() {

    }

    public void login() {
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
                getUserData.setSuccess(loggedIn[0] = (requests.sendRequest(String.format("%s/temp/login", appUrl), properties, "POST").equals("true")));
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
                        System.out.println("Failed to save username");
                    }
                }
                Platform.runLater(() -> {
                    try {
                        //Jesli zalogowano pomyslnie, wyswietlanie sceny z panelem uzytkownika
                        SceneController.startScene("userPane", parameters);
                        int endTime = (int) System.nanoTime();
                        System.out.printf("Time to load: %.2fs\n", (endTime - startTime) / Math.pow(10, 9));
                    } catch (IOException e) {
                        System.out.printf("Failed to log in, reason: %s", e.getMessage());
                    }
                });
            } else {
                System.out.println("Failed to log in");
            }
            progressIndicator.setVisible(false);
        };

        TaskRunner taskRunner = new TaskRunner(Arrays.asList(loginRequest, getUserData), onTaskComplete, true);
        taskRunner.run();
    }

}
