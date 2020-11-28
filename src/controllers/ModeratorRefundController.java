package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import models.ModeratorDataModel;
import models.tableRepresentations.ModeratorRefundTableRepresentation;
import updating.OnUpdate;
import utils.tableUtils.JavaFXTableUtils;
import web.Requests;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModeratorRefundController extends Controller {

    @FXML
    private TableView<ModeratorRefundTableRepresentation> moderatorRefundTable;
    private List<ModeratorRefundTableRepresentation> refunds;
    private Requests requests;
    private TextInputDialog inputDialog;

    public void initialize() {
        inputDialog = new TextInputDialog();
        inputDialog.setTitle("Confirmation");
        inputDialog.setHeaderText("Write your reason");
        inputDialog.setContentText("Reason: ");
        requests = Requests.getInstance();
        initializeRefundsTable();
        initializeManually();
    }

    @OnUpdate
    public void updateTableElements() {
        refunds = ModeratorDataModel.getInstance().getRefunds();
        moderatorRefundTable.getItems().removeAll(moderatorRefundTable.getItems());
        moderatorRefundTable.getItems().addAll(refunds);
    }

    private void updateRefunds() {
        refunds = ModeratorDataModel.getInstance().getRefunds();
    }

    public void initializeRefundsTable() {
        updateRefunds();
        if (refunds.size() == 0) {
            JavaFXTableUtils.toJavaFXTableView(
                    Arrays.asList(new ModeratorRefundTableRepresentation(-1, "", -1, -1, "", -1, "")),
                    moderatorRefundTable);
            moderatorRefundTable.getItems().clear();
        } else {
            JavaFXTableUtils.toJavaFXTableView(refunds, moderatorRefundTable);
        }

        setTableMeasurements(moderatorRefundTable);

        moderatorRefundTable.getColumns().forEach(c -> c.setResizable(false));
    }


    private void sendDecision(int rid, String reason, boolean decision) {
        Map<String, String> data = new HashMap<>();
        data.put("rid", String.valueOf(rid));
        data.put("reason", reason);
        data.put("decision", String.valueOf(decision));
        try {
            requests.sendRequest(String.format("%s/payments/moderator/refund", appURL), data, "POST");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onAccept() {
        int id = moderatorRefundTable.getSelectionModel().getSelectedItem().getId();
        String reason = inputDialog.showAndWait().orElse("Accepted");
        ModeratorDataModel.getInstance().updateData();
        sendDecision(id, reason, true);
    }

    public void onReject() {
        int id = moderatorRefundTable.getSelectionModel().getSelectedItem().getId();
        String reason = inputDialog.showAndWait().orElse("Rejected");
        ModeratorDataModel.getInstance().updateData();
        sendDecision(id, reason, false);
    }

    @Override
    protected void onInit() {

    }
}
