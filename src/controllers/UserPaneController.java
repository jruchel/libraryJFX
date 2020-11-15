package controllers;

import javafx.scene.control.cell.PropertyValueFactory;
import models.Book;
import connection.Requests;
import fxutils.SceneController;
import fxutils.TaskRunner;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import models.Transaction;

import java.io.IOException;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

public class UserPaneController extends Controller {

    private AnchorPane currentPane = null;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label motdLabel;

    @FXML
    private Button changeUsernameButton;

    @FXML
    private Button changePasswordButton;

    @FXML
    private Button showBooksButton;

    @FXML
    private Button showTransactionButton;

    //Change username subwindow
    @FXML
    private AnchorPane usernameChangePane;
    @FXML
    private TextField newUsernameField;

    @FXML
    private Button acceptButton;

    //Change password subwindow
    @FXML
    private AnchorPane passwordChangePane;

    @FXML
    private Button acceptPasswordButton;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;


    //Books pane
    @FXML
    private AnchorPane booksPane;

    @FXML
    private ListView<Book> rentedListView;
    @FXML
    private ListView<Book> reservedListView;
    @FXML
    private Button cancelReservedButton;
    @FXML
    private Button reserveButton;

    @FXML
    private ListView<Transaction> transactionListView;

    @FXML
    private TableView<Transaction> transactionsTableView;

    @FXML
    private AnchorPane transactionsPane;

    private String userdata;

    private List<Book> reservedBookList;
    private List<Book> rentedBookList;
    private List<Transaction> transactions;

    private Requests requests;

    public void initialize() {
        requests = Requests.getInstance();
    }

    public void initializeManually() {
        try {
            reservedBookList = (List<Book>) parameters.get("reservedBooks");
            rentedBookList = (List<Book>) parameters.get("rentedBooks");
            usernameLabel.setText(String.format("Username: %s", parameters.get("username").toString()));
            reservedListView.getItems().addAll(reservedBookList);
            rentedListView.getItems().addAll(rentedBookList);
            initializeTransactions();
        } catch (Exception ignored) {
            System.out.println();
        }
    }

    private void initializeTransactions() {
        transactions = (List<Transaction>) parameters.get("transactions");
        TableColumn<Transaction, String> column1 = new TableColumn<>("Description");
        column1.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<Transaction, Double> column2 = new TableColumn<>("Amount");
        column2.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<Transaction, Currency> column3 = new TableColumn<>("Currency");
        column3.setCellValueFactory(new PropertyValueFactory<>("currency"));
        TableColumn<Transaction, Boolean> column4 = new TableColumn<>("Refunded");
        column4.setCellValueFactory(new PropertyValueFactory<>("refunded"));
        transactionsTableView.getColumns().clear();
        transactionsTableView.getColumns().addAll(Arrays.asList(column1, column2, column3, column4));
        double width = 400;
        column1.setMinWidth(width / 3);
        column1.setMaxWidth(width / 3);
        column2.setMinWidth(width / 3);
        column2.setMaxWidth(width / 3);
        column3.setMaxWidth(width / 6);
        column3.setMinWidth(width / 6);
        column4.setMaxWidth(width / 6);
        column4.setMinWidth(width / 6);
        transactionsTableView.getColumns().forEach(c -> c.setResizable(false));
        transactionsTableView.getItems().addAll(transactions);
    }

    public void showUsernameChange() {
        showPane(usernameChangePane);
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

    public void reserve() {

    }

    public void cancelReserved() {
        final int[] id = {reservedListView.getSelectionModel().getSelectedItem().getId()};
        Runnable cancelReservationTask = () -> {
            try {
                requests.sendDeleteRequest(String.format("http://localhost:8080/rental/reserve/%d", id[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Runnable onTaskComplete = () -> {
            reservedBookList = reservedBookList.stream().filter(b -> b.getId() != id[0]).collect(Collectors.toList());
            Platform.runLater(() -> {
                reservedListView.getItems().clear();
                reservedListView.getItems().addAll(reservedBookList);
                reservedListView.getSelectionModel().clearSelection();
            });

        };
        TaskRunner taskRunner = new TaskRunner(cancelReservationTask, onTaskComplete);
        taskRunner.run();
    }

    private void hideCurrentPane() {
        currentPane.setVisible(false);
    }

    public void showPasswordChange() {
        showPane(passwordChangePane);
    }

    public void changeUsername() {

    }

    public void changePassword() {

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
