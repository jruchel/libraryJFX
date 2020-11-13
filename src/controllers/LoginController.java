package controllers;

import connection.Requests;
import fxutils.SceneController;
import fxutils.TaskRunner;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    private Requests requests;

    public void initialize() {
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

        Runnable loginRequest = () -> {
            try {
                loggedIn[0] = (requests.sendPostRequest("http://localhost:8080/temp/login", properties) == 200);
            } catch (IOException ignored) {
            }
        };

        Runnable onTaskComplete = () -> {
            if (loggedIn[0]) {
                Platform.runLater(() -> {
                    try {
                        SceneController.startScene("userPane");
                    } catch (IOException e) {
                        System.out.println("Failed to log in");
                    }
                });
            } else {
                System.out.println("Failed to log in");
            }
        };
        TaskRunner taskRunner = new TaskRunner(loginRequest, onTaskComplete);
        taskRunner.run();
    }

}
