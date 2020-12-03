package controllers;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import tasks.ModeratorRefundDataRetrievalTask;
import updating.OnUpdate;
import web.Requests;
import models.UserModel;
import utils.fxUtils.AlertUtils;
import utils.fxUtils.SceneController;
import web.TaskRunner;
import javafx.application.Platform;
import javafx.fxml.FXML;

import java.io.IOException;


public class UserPaneController extends Controller {


    private static String clickedColor = "#2F4BA1";
    private static String hoverColor = "#59809E";
    private static String backgroundColor = "#221C35";

    private Pane currentPane = null;

    @FXML
    private AnchorPane booksPane;
    @FXML
    private SplitPane splitPaneGlobal;

    @FXML
    private AnchorPane transactionsPane;

    @FXML
    private AnchorPane donationPane;

    @FXML
    private AnchorPane refundsPane;
    @FXML
    private AnchorPane cardDetailsPane;
    @FXML
    private BorderPane browserPane;
    @FXML
    private BorderPane moderatorPane;

    @FXML
    private VBox userPane;

    @FXML
    private Label booksButton;
    @FXML
    private Label browseBooksButton;
    @FXML
    private Label subscribeButton;
    @FXML
    private Label transactionsButton;
    @FXML
    private Label refundsButton;
    @FXML
    private Label donateButton;
    @FXML
    private Label logoutButton;
    @FXML
    private Label adminPaneButton;
    @FXML
    private SplitPane moderatorSplitPane;
    @FXML
    private AnchorPane menuPane;
    @FXML
    private AnchorPane viewPane;


    private Label currentLabel;

    private Requests requests;

    public void initialize() {
        Background background = getBackground(backgroundColor);
        viewPane.setBackground(background);
        menuPane.setBackground(background);
        for (Node node : splitPaneGlobal.lookupAll(".split-pane-divider")) {
            node.setVisible(false);
        }
        if (!UserModel.getInstance().getCurrentUser().hasRole("moderator")) moderatorSplitPane.setVisible(false);
        requests = Requests.getInstance();
        setBackground("file:src/resources/images/mainBg2.jpg", userPane, 1200, 685);
        initializeManually();
        String username = UserModel.getInstance().getCurrentUser().getUsername();
        setStatusLabel();
        initButtons();
        // if (!username.isEmpty())
        // usernameLabel.setText(String.format("Welcome %s!", username));
    }


    @OnUpdate(updatedBy = UserModel.class)
    public void setStatusLabel() {
        String status = "Not subscribed";
        if (UserModel.getInstance().getCurrentUser().hasRole("ROLE_SUBSCRIBER"))
            status = "Subscribed";
        //statusLabel.setText(String.format("Status: %s", status));
    }

    private void changeLabelBackground(Label label) {
        if (currentLabel != null)
            currentLabel.setBackground(getDefaultBackground());
        currentLabel = label;
        currentLabel.setBackground(getClickedOnBackground());
    }

    private void showPane(Pane pane) {
        if (currentPane != null)
            currentPane.setVisible(false);
        currentPane = pane;
        currentPane.setVisible(true);
    }

    public void showModeratorPane() {
        changeLabelBackground(adminPaneButton);
        showPane(moderatorPane);
    }


    public void showSubscribePane() {
        changeLabelBackground(subscribeButton);
        showPane(cardDetailsPane);
    }

    public void showBookBrowser() {
        changeLabelBackground(browseBooksButton);
        showPane(browserPane);
    }

    public void showDonationPane() {
        changeLabelBackground(donateButton);
        showPane(donationPane);
    }

    private void hideCurrentPane() {
        currentPane.setVisible(false);
    }

    public void showRefunds() {
        changeLabelBackground(refundsButton);
        showPane(refundsPane);
    }

    public void showTransactions() {
        changeLabelBackground(transactionsButton);
        showPane(transactionsPane);
    }

    public void showBooks() {
        changeLabelBackground(booksButton);
        showPane(booksPane);
    }

    public void logout() {
        changeLabelBackground(logoutButton);
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
    private void initButtons(Label... labels) {
        for (Label l : labels) {
            l.setBackground(getDefaultBackground());
            l.setOnMouseEntered(event -> {
                if (!l.getBackground().getFills().get(0).getFill().equals(Paint.valueOf(clickedColor)))
                    l.setBackground(getHoverBackground());
            });
            l.setOnMouseExited(event -> {
                        if (!l.getBackground().getFills().get(0).getFill().equals(Paint.valueOf(clickedColor)))
                            l.setBackground(getDefaultBackground());
                    }
            );

        }
    }

    private void initButtons() {
        initButtons(booksButton, browseBooksButton, subscribeButton, transactionsButton, refundsButton, donateButton, logoutButton, adminPaneButton);
    }

    private Background getDefaultBackground() {
        return getBackground("#63666A");
    }

    private Background getHoverBackground() {
        return getBackground(hoverColor);
    }

    private Background getClickedOnBackground() {
        return getBackground(clickedColor);
    }
}
