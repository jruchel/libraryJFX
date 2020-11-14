package controllers;

import connection.Requests;
import fxutils.SceneController;
import fxutils.TaskRunner;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class UserPaneController extends Controller {

    private AnchorPane currentlyShowing = null;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label motdLabel;

    @FXML
    private Button changeUsername;

    @FXML
    private Button changePassword;

    @FXML
    private Button showBooks;

    @FXML
    private Button showTransactions;

    //Change username subwindow
    @FXML
    private AnchorPane usernameChangePane;
    @FXML
    private TextField currentUsername;

    @FXML
    private Button acceptButton;

    //Change password subwindow
    @FXML
    private AnchorPane passwordChangePane;

    @FXML
    private Button acceptPassword;

    @FXML
    private PasswordField currentPassword;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    private String userdata;

    private Requests requests;

    public void initializeManually() {
        try {
            usernameLabel.setText(parameters.get("username").toString());
        } catch (Exception ignored) {
        }
    }

    public void showUsernameChange() {
        showPane(usernameChangePane);
    }

    private void showPane(Pane pane) {
        if (currentlyShowing != null) {
            currentlyShowing.setVisible(false);
            currentlyShowing = (AnchorPane) pane;
            currentlyShowing.setVisible(true);
        } else {
            currentlyShowing = (AnchorPane) pane;
            currentlyShowing.setVisible(true);
        }
    }

    public void showPasswordChange() {
        showPane(passwordChangePane);
    }

    public void changeUsername() {

    }

    public void changePassword() {

    }

    public void showTransactions() {

    }

    public void showBooks() {

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
