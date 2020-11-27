package controllers;

import javafx.scene.Parent;
import models.Book;
import connection.Requests;
import models.Refund;
import tasks.UserDataRetrievalTask;
import utils.Properties;
import utils.fxUtils.AlertUtils;
import utils.fxUtils.SceneController;
import utils.tableUtils.JavaFXTableUtils;
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

    private String appUrl;

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
    private TableView<Refund> refundsTableView;

    private List<Refund> refunds;


    private List<Book> reservedBookList;
    private List<Book> rentedBookList;
    private List<Transaction> transactions;

    private Requests requests;

    public void initialize() {
        try {
            appUrl = Properties.getProperty("site.url");
        } catch (IOException e) {
            AlertUtils.showAlert("Could not find site address");
            System.exit(0);
        }
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
            initializeRefundsTable();
        } catch (Exception ignored) {
            AlertUtils.showAlert(ignored.getMessage());
        }
        startAutoUpdateTask(3000);
    }

    private static <E> void setTableMeasurements(TableView<E> tableView, double[] widthRatios) {
        double stageWidth = SceneController.getPrimaryStage().getWidth();
        Parent parent = tableView.getParent();
        AnchorPane.setLeftAnchor(parent, stageWidth / 10);
        AnchorPane.setRightAnchor(parent, stageWidth / 10);
        AnchorPane.setTopAnchor(parent, stageWidth / 10);
        AnchorPane.setBottomAnchor(parent, stageWidth / 10);
        double width = stageWidth - (AnchorPane.getLeftAnchor(parent) + AnchorPane.getRightAnchor(parent));

        for (int i = 0; i < tableView.getColumns().size(); i++) {
            double size = widthRatios[i] * width;
            size = size * 0.981322;
            tableView.getColumns().get(i).setMaxWidth(size);
            tableView.getColumns().get(i).setMinWidth(size);
        }
    }

    private void initializeRefundsTable() {
        updateRefunds();
        if (refunds.size() == 0) {
            JavaFXTableUtils.toJavaFXTableView(
                    Arrays.asList(new Refund(-1, -1, "", -1, "", "", "", "")),
                    refundsTableView);
            refundsTableView.getItems().clear();
        } else {
            JavaFXTableUtils.toJavaFXTableView(refunds, refundsTableView);
        }

        double[] widths = {0.246154, 0.109402, 0.064957, 0.091453, 0.176923, 0.311111};
        setTableMeasurements(refundsTableView, widths);

        refundsTableView.getColumns().forEach(c -> c.setResizable(false));
    }

    private void initializeTransactions() {
        transactions = (List<Transaction>) parameters.get("transactions");
        if (transactions.size() == 0) {
            JavaFXTableUtils.toJavaFXTableView(
                    Arrays.asList(new Transaction(-1, -1, "", "", -1, false, "")),
                    transactionsTableView);
            transactionsTableView.getItems().clear();
        } else {
            JavaFXTableUtils.toJavaFXTableView(transactions, transactionsTableView);
        }

        double[] widths = {0.333333333333, 0.333333333333, 0.1666666666666, 0.1666666666666};
        setTableMeasurements(transactionsTableView, widths);

        transactionsTableView.getColumns().forEach(c -> c.setResizable(false));
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
        try {
            Transaction toRefund = transactionsTableView.getSelectionModel().getSelectedItem();
            if (toRefund.isRefunded() || refunds.stream().anyMatch(r -> r.getTransactionID() == toRefund.getId())) {
                AlertUtils.showAlert("Refund has already been submitted or transaction has already been refunded");
                return;
            }
            Map<String, String> params = new TreeMap<>();
            params.put("chargeid", toRefund.getChargeID());
            params.put("message", "Refund request");
            boolean[] success = {true};
            new TaskRunner(() -> {
                try {
                    success[0] = requests.sendRequest(String.format("%s/payments/user/refund", appUrl), params, "POST").equals("success");
                } catch (IOException e) {
                    success[0] = false;
                }
            }, () -> {
                if (success[0]) {
                    AlertUtils.showAlert("Refund request sent, view your pending refunds under Refunds button in the user pane");
                } else {
                    AlertUtils.showAlert("Refund request was not submitted due to a server error");
                }
            }).run();
        } catch (Exception ex) {
            AlertUtils.showAlert("Refund failed to process, please try again");
        }
    }

    public void cancelReserved() {
        final int[] id = {reservedListView.getSelectionModel().getSelectedItem().getId()};
        Runnable cancelReservationTask = () -> {
            try {
                requests.sendRequest(String.format("%s/rental/reserve/%d", appUrl, id[0]), "POST");
            } catch (IOException e) {
                AlertUtils.showAlert("Canceling reservation failed, please try again later");
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
                requests.sendRequest(String.format("%s/logout", appUrl), "POST");
            } catch (IOException ignored) {
            }
        };

        Runnable onTaskComplete = () -> {
            Platform.runLater(() -> {
                try {
                    SceneController.startScene("login");
                } catch (IOException e) {
                    AlertUtils.showAlert("Failed to log out");
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
            refunds.addAll(t.getRefundList());
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
