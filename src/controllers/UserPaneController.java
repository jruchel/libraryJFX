package controllers;

import javafx.scene.control.cell.PropertyValueFactory;
import models.Book;
import connection.Requests;
import models.Refund;
import models.RefundTableRepresentation;
import tasks.UserDataRetrievalTask;
import utils.SceneController;
import utils.TaskRunner;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import models.Transaction;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class UserPaneController extends Controller {

    private String appAdress = "http://localhost:8080";

    private AnchorPane currentPane = null;

    private boolean autoUpdate = false;

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
    private TableView<Transaction> transactionsTableView;

    @FXML
    private AnchorPane transactionsPane;

    @FXML
    private AnchorPane refundsPane;

    @FXML
    private TableView<RefundTableRepresentation> refundsTableView;

    private List<RefundTableRepresentation> refunds;


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
            initializeRefunds();
        } catch (Exception ignored) {
            System.out.println();
        }
        startAutoUpdateTask(30000);
    }

    private void initializeRefunds() {
        updateRefunds();

        TableColumn<RefundTableRepresentation, String> column1 = new TableColumn<>("Transaction description");
        column1.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<RefundTableRepresentation, Double> column2 = new TableColumn<>("Amount");
        column2.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<RefundTableRepresentation, Currency> column3 = new TableColumn<>("Currency");
        column3.setCellValueFactory(new PropertyValueFactory<>("currency"));

        TableColumn<RefundTableRepresentation, String> column4 = new TableColumn<>("Status");
        column4.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<RefundTableRepresentation, String> column5 = new TableColumn<>("Message");
        column5.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<RefundTableRepresentation, String> column6 = new TableColumn<>("Reason");
        column6.setCellValueFactory(new PropertyValueFactory<>("reason"));

        refundsTableView.getColumns().clear();
        refundsTableView.getColumns().addAll(Arrays.asList(column1, column2, column3, column4, column5, column6));
        refundsTableView.getColumns().forEach(c -> c.setResizable(false));
        refundsTableView.getItems().addAll(refunds);
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

    public void requestRefund() {
        Transaction toRefund = transactionsTableView.getSelectionModel().getSelectedItem();
        if (toRefund.isRefunded()) return;
        Map<String, String> params = new TreeMap<>();
        params.put("chargeid", toRefund.getChargeID());
        params.put("message", "Refund request");
        boolean[] success = {true};
        new TaskRunner(() -> {
            try {
                success[0] = requests.sendPostRequest(String.format("%s/payments/user/refund", appAdress), params) == 200;
            } catch (IOException e) {
                success[0] = false;
            }
        }, () -> {
            if (success[0]) {
                System.out.println("Refund request sent, view your pending refunds under Refunds button in the user pane");
            } else {
                System.out.println("Refund request was not submitted due to a server error");
            }
        }).run();

    }

    public void cancelReserved() {
        final int[] id = {reservedListView.getSelectionModel().getSelectedItem().getId()};
        Runnable cancelReservationTask = () -> {
            try {
                requests.sendDeleteRequest(String.format("%s/rental/reserve/%d", appAdress, id[0]));
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

    public void cancelRefund() {

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

    public void showRefunds() {
        updateData();
        showPane(refundsPane);
    }

    public void showTransactions() {
        updateData();
        showPane(transactionsPane);
    }

    public void showBooks() {
        updateData();
        showPane(booksPane);
    }

    public void logout() {
        Runnable logoutRequest = () -> {
            try {
                requests.sendPostRequest(String.format("%s/logout", appAdress));
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

    /**
     * Updates the data automatically in the background for this controller
     *
     * @param milis to wait between each update
     */
    private void startAutoUpdateTask(int milis) {
        this.autoUpdate = true;
        Runnable autoUpdate = () -> {
            while (this.autoUpdate) {
                updateData();
                try {
                    Thread.sleep(milis);
                } catch (InterruptedException ignored) {

                }
            }
        };
        TaskRunner taskRunner = new TaskRunner(autoUpdate);
        taskRunner.run();
    }

    private void updateRefunds() {
        transactions = (List<Transaction>) parameters.get("transactions");
        refunds = new ArrayList<>();

        for (Transaction t : transactions) {
            for (Refund r : t.getRefundList()) {
                refunds.add(new RefundTableRepresentation(t.getDescription(), t.getAmount(), t.getCurrency(), r.getStatus(), r.getMessage(), r.getReason()));
            }
        }
    }

    private void updateData() {
        Runnable onTaskComplete = () -> {
            reservedBookList = (List<Book>) parameters.get("reservedBooks");
            rentedBookList = (List<Book>) parameters.get("rentedBooks");
            usernameLabel.setText(String.format("Username: %s", parameters.get("username").toString()));
        };
        TaskRunner taskRunner = new TaskRunner(new UserDataRetrievalTask(parameters), onTaskComplete);
        taskRunner.run();
    }
}
