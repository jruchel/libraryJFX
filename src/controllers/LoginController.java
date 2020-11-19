package controllers;

import connection.Requests;
import utils.SceneController;
import utils.TaskRunner;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import tasks.UserDataRetrievalTask;

import java.io.IOException;
import java.util.*;

public class LoginController {

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
        this.requests = Requests.getInstance();
        usernameField.setText("user");
        passwordField.setText("admin1");
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
                getUserData.setSuccess(loggedIn[0] = (requests.sendPostRequest("http://localhost:8080/temp/login", properties) == 200));
            } catch (IOException ignored) {
            }
        };
        int startTime = (int) System.nanoTime();

        //To jest wywolywane po zakonczeniu poprzednich zadan
        Runnable onTaskComplete = () -> {
            if (loggedIn[0]) {
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
