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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        Map<String, Object> parameters = new HashMap<>();

        Runnable loginRequest = () -> {
            try {
                loggedIn[0] = (requests.sendPostRequest("http://localhost:8080/temp/login", properties) == 200);
            } catch (IOException ignored) {
            }
        };

        Runnable getUserData = () -> {
            if (loggedIn[0]) {
                try {
                    String data = requests.getResponseBody("http://localhost:8080/user");
                    parameters.put("username", getUsername(data));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };

        Runnable onTaskComplete = () -> {
            if (loggedIn[0]) {
                Platform.runLater(() -> {
                    try {
                        SceneController.startScene("userPane", parameters);
                    } catch (IOException e) {
                        System.out.printf("Failed to log in, reason: %s", e.getMessage());
                    }
                });
            } else {
                System.out.println("Failed to log in");
            }
        };
        TaskRunner loginTask = new TaskRunner(Arrays.asList(loginRequest, getUserData), onTaskComplete, true);
        loginTask.run();
    }

    private String getUsername(String data) {
        String regex = "\\\"username\\\"\\:\\\"(\\w+)\\\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;
        for (String s : data.split(",")) {
            matcher = pattern.matcher(s);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        return "";
    }

}
