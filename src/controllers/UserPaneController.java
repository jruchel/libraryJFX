package controllers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import tasks.ModeratorRefundDataRetrievalTask;
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


public class UserPaneController extends Controller {


    private AnchorPane currentPane = null;
    @FXML
    private Label usernameLabel;
    @FXML
    private AnchorPane booksPane;

    @FXML
    private AnchorPane transactionsPane;

    @FXML
    private AnchorPane donationPane;

    @FXML
    private AnchorPane refundsPane;

    @FXML
    private Button moderatorPaneButton;

    private Requests requests;

    public void initialize() {
        if (UserModel.getInstance().getCurrentUser().hasRole("moderator")) moderatorPaneButton.setVisible(true);
        requests = Requests.getInstance();
        initializeManually();
        String username = UserModel.getInstance().getCurrentUser().getUsername();
        if (!username.isEmpty())
            usernameLabel.setText(String.format("Welcome %s!", username));
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

    public void showModeratorPane() {
        TaskRunner taskRunner = new TaskRunner(new ModeratorRefundDataRetrievalTask(), () -> {
            Platform.runLater(() -> {
                try {
                    SceneController.startScene("moderatorPane");
                } catch (IOException e) {
                    AlertUtils.showAlert("Error while showing moderator pane");
                }
            });

        });
        taskRunner.run();
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
                requests.sendRequest(String.format("%s/logout", appURL), "POST");
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

    @Override
    protected void onInit() {

    }
}
