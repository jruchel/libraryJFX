package controllers;

import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import utils.Properties;
import utils.fxUtils.SceneController;

import java.io.IOException;


public abstract class Controller {
    protected String appURL;

    public void initializeManually() {
        onInit();
        try {
            appURL = Properties.getProperty("site.url");
        } catch (IOException ignored) {
        }
        ControllerAccess.getInstance().add(this.getClass().getName(), this);
    }

    protected abstract void onInit();

    public static <E> void setTableMeasurements(TableView<E> tableView) {
        double ratio = 1 / (double) tableView.getColumns().size();
        setTableMeasurements(tableView, ratio);
    }

    public static <E> void setTableMeasurements(TableView<E> tableView, double ratio) {
        double[] widthRatios = new double[tableView.getColumns().size()];
        for (int i = 0; i < widthRatios.length; i++) {
            widthRatios[i] = ratio;
        }
        setTableMeasurements(tableView, widthRatios);
    }

    public static <E> void setTableMeasurements(TableView<E> tableView, double[] widthRatios) {
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
}
