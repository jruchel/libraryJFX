package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import models.Refund;
import models.UserModel;
import updating.OnUpdate;
import utils.tableUtils.JavaFXTableUtils;

import java.util.Arrays;
import java.util.List;

import static controllers.Controller.setTableMeasurements;

public class RefundsController {
    @FXML
    private TableView<Refund> refundsTableView;
    private List<Refund> refunds;
    private UserModel userModel;

    public void initialize() {
        userModel = UserModel.getInstance();
        refunds = userModel.getCurrentUser().getRefunds();
        initializeRefundsTable();
        ControllerAccess.getInstance().put(this.getClass().getName(), this);
    }

    @OnUpdate
    public void updateTableElements() {
        refunds = userModel.getCurrentUser().getRefunds();
        refundsTableView.getItems().clear();
        refundsTableView.getItems().addAll(refunds);
        refundsTableView.refresh();
    }

    public void initializeRefundsTable() {
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

    private void updateRefunds() {
        refunds = userModel.getCurrentUser().getRefunds();
    }

}
