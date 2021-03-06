package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.text.Font;
import models.entities.Refund;
import models.UserModel;
import updating.OnUpdate;
import utils.tableUtils.JavaFXTableUtils;
import java.util.Arrays;
import java.util.List;


public class RefundsController extends Controller {
    @FXML
    protected TableView<Refund> refundsTableView;
    protected List<Refund> refunds;
    protected UserModel userModel;

    public void initialize() {
        userModel = UserModel.getInstance();
        refunds = userModel.getCurrentUser().getRefunds();
        initializeRefundsTable();
        initializeManually();
        try {
            setFont(Button.class, Font.font (globalFontFamily, 14));
            setFont(Label.class, Font.font (globalFontFamily, 14));
        } catch (Exception ignored) {
        }
    }

    @OnUpdate(updatedBy = UserModel.class)
    public void updateTableElements() {
        refunds = userModel.getCurrentUser().getRefunds();
        refundsTableView.getItems().removeAll(refundsTableView.getItems());
        refundsTableView.getItems().addAll(refunds);
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

    protected void updateRefunds() {
        refunds = userModel.getCurrentUser().getRefunds();
    }

}
