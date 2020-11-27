package controllers;

import web.Requests;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import models.Refund;
import models.Transaction;
import models.UserModel;
import updating.OnUpdate;
import utils.Properties;
import web.TaskRunner;
import utils.fxUtils.AlertUtils;
import utils.tableUtils.JavaFXTableUtils;

import java.io.IOException;
import java.util.*;

public class TransactionsPaneController {

    @FXML
    private TableView<Transaction> transactionsTableView;
    private String appUrl;
    private List<Transaction> transactions;
    private List<Refund> refunds;
    private Requests requests;
    private UserModel userModel;

    public void initialize() {
        transactions = new ArrayList<>();
        refunds = new ArrayList<>();
        userModel = UserModel.getInstance();
        userModel.getCurrentUser().getTransactionList().forEach(t -> refunds.addAll(t.getRefundList()));
        requests = Requests.getInstance();
        initializeTransactions();
        try {
            appUrl = Properties.getProperty("site.url");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ControllerAccess.getInstance().put(this.getClass().getName(), this);
    }

    @OnUpdate
    public void updateTableElements() {
        transactions = userModel.getCurrentUser().getTransactionList();
        transactionsTableView.getItems().clear();
        transactionsTableView.getItems().addAll(transactions);
        transactionsTableView.refresh();
    }

    public void initializeTransactions() {
        transactions = userModel.getCurrentUser().getTransactionList();
        if (transactions.size() == 0) {
            JavaFXTableUtils.toJavaFXTableView(
                    Arrays.asList(new Transaction(-1, -1, "", "", -1, false, "")),
                    transactionsTableView);
            transactionsTableView.getItems().clear();
        } else {
            JavaFXTableUtils.toJavaFXTableView(transactions, transactionsTableView);
        }

        double[] widths = {0.333333333333, 0.333333333333, 0.1666666666666, 0.1666666666666};
        Controller.setTableMeasurements(transactionsTableView, widths);

        transactionsTableView.getColumns().forEach(c -> c.setResizable(false));
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
                Platform.runLater(() -> {
                    if (success[0]) {
                        AlertUtils.showAlert("Refund request sent, view your pending refunds under Refunds button in the user pane");
                        userModel.updateUser();
                    } else {
                        AlertUtils.showAlert("Refund request was not submitted due to a server error");
                    }
                });

            }).run();
        } catch (Exception ex) {
            AlertUtils.showAlert("Refund failed to process, please try again");
        }
    }

}
