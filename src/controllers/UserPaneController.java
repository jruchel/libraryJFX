package controllers;

import connection.Requests;
import fxutils.SceneController;
import fxutils.TaskRunner;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class UserPaneController {
    @FXML
    private TextArea userInfoArea;

    private String userdata;

    private Requests requests;

    public void initialize() {
        requests = Requests.getInstance();
        try {
            userdata = requests.getResponseBody("http://localhost:8080/user");
        } catch (IOException ignored) {
        }
        userdata = userdata.replace(",", ",\n");
        userInfoArea.setText(userdata);
    }

    public void logout() {
        Runnable logoutRequest = () -> {
            try {
                requests.sendPostRequest("http://localhost:8080/logout");
            } catch (IOException ignored) {
            }
        };

        Runnable onTaskComplete = () -> {
            Platform.runLater(() -> {
                try {
                    SceneController.startScene("login");
                } catch (IOException e) {
                    System.out.println("Failed to log out");
                }
            });


        };
        TaskRunner taskRunner = new TaskRunner(logoutRequest, onTaskComplete);
        taskRunner.run();
    }
}
