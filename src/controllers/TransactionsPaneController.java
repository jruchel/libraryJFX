package controllers;

import com.sun.org.glassfish.external.statistics.annotations.Reset;
import controllers.Controller;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import models.ModeratorDataModel;
import utils.Resources;
import web.Requests;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import models.entities.Refund;
import models.entities.Transaction;
import models.UserModel;
import updating.OnUpdate;
import web.TaskRunner;
import utils.fxUtils.AlertUtils;
import utils.tableUtils.JavaFXTableUtils;

import java.io.IOException;
import java.util.*;

public class TransactionsPaneController extends Controller {

    @FXML
    protected TableView<Transaction> transactionsTableView;
    protected List<Transaction> transactions;
    protected List<Refund> refunds;
    protected Requests requests;
    protected UserModel userModel;
    protected ModeratorDataModel moderatorDataModel;
    @FXML
    protected Button requestRefundButton;

    public void initialize() {
        transactions = new ArrayList<>();
        refunds = new ArrayList<>();
        moderatorDataModel = ModeratorDataModel.getInstance();
        userModel = UserModel.getInstance();
        userModel.getCurrentUser().getTransactionList().forEach(t -> refunds.addAll(t.getRefundList()));
        requests = Requests.getInstance();
        initializeTransactions();
        initializeManually();
        try {
            setFont(Button.class, Font.font (globalFontFamily, 14));
            setFont(Label.class, Font.font (globalFontFamily, 14));
        } catch (Exception ignored) {
        }
    }

    @OnUpdate(updatedBy = {UserModel.class})
    public void updateTableElements() {
        transactions = userModel.getCurrentUser().getTransactionList();
        transactionsTableView.getItems().removeAll(transactionsTableView.getItems());
        transactionsTableView.getItems().addAll(transactions);
        try {
            transactionsTableView.setStyle(Resources.getStyle("table"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            if (toRefund.isRefunded() || refunds.stream().anyMatch(r -> r.getTransactionID() == toRefund.getId()/* && r.getStatus().equals("Pending")*/)) {
                AlertUtils.showAlert("Refund has already been submitted or transaction has already been refunded");
                return;
            }
            Map<String, String> params = new TreeMap<>();
            params.put("chargeid", toRefund.getChargeID());
            params.put("message", "Refund request");
            boolean[] success = {true};
            new TaskRunner(() -> {
                try {
                    success[0] = requests.sendRequest(String.format("%s/payments/user/refund", appURL), params, "POST").equals("success");
                } catch (IOException e) {
                    success[0] = false;
                }
            }, () -> {
                Platform.runLater(() -> {
                    if (success[0]) {
                        AlertUtils.showAlert("Refund request sent, view your pending refunds under Refunds button in the user pane");
                        userModel.updateUser();
                        moderatorDataModel.updateData();
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
