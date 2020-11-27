package controllers;

import web.Requests;
import models.UserModel;
import utils.Properties;
import utils.fxUtils.AlertUtils;
import utils.fxUtils.SceneController;
import web.TaskRunner;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;


public class UserPaneController {


    private AnchorPane currentPane = null;
    @FXML
    private AnchorPane booksPane;

    @FXML
    private AnchorPane transactionsPane;

    @FXML
    private AnchorPane donationPane;

    @FXML
    private AnchorPane refundsPane;

    private String appUrl;

    private Requests requests;

    public void initialize() {
        try {
            appUrl = Properties.getProperty("site.url");
        } catch (IOException e) {
            AlertUtils.showAlert("Could not find site address");
            System.exit(0);
        }
        requests = Requests.getInstance();
        ControllerAccess.getInstance().put(this.getClass().getName(), this);
    }


    private void showPane(Pane pane) {
        if (currentPane != null) {
            currentPane.setVisible(false);
            currentPane = (AnchorPane) pane;
            currentPane.setVisible(true);
        } else {
            currentPane = (AnchorPane) pane;
            currentPane.setVisible(true);
        }
    }

    public void showDonationPane() {
        showPane(donationPane);
    }

    private void hideCurrentPane() {
        currentPane.setVisible(false);
    }

    public void showRefunds() {
        showPane(refundsPane);
    }

    public void showTransactions() {
        showPane(transactionsPane);
    }

    public void showBooks() {
        showPane(booksPane);
    }

    public void logout() {
        Runnable logoutRequest = () -> {
            try {
                requests.sendRequest(String.format("%s/logout", appUrl), "POST");
            } catch (IOException ignored) {
            }
        };

        Runnable onTaskComplete = () -> {
            Platform.runLater(() -> {
                try {
                    UserModel.getInstance().setCurrentUser(null);
                    SceneController.startScene("login");
                } catch (IOException e) {
                    AlertUtils.showAlert("Failed to log out");
                }
            });


        };
        TaskRunner taskRunner = new TaskRunner(logoutRequest, onTaskComplete);
        taskRunner.run();
    }
}
