package controllers;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import updating.ControllerAccess;
import utils.Properties;
import utils.fxUtils.SceneController;
import web.Requests;

import java.io.IOException;


public abstract class Controller {
    protected String appURL;
    protected Requests requests;

    public void initializeManually() {
        try {
            appURL = Properties.getProperty("site.url");
        } catch (IOException ignored) {
        }
        requests = Requests.getInstance();
        ControllerAccess.getInstance().put(this.getClass().getName(), this);
    }

    protected void setBackground(String url, Pane pane, int width, int height) {
        BackgroundImage myBI = new BackgroundImage(new Image(url, width, height, false, true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        pane.setBackground(new Background(myBI));
    }

    public Requests getRequests() {
        return requests;
    }

    public String getAppURL() {
        return appURL;
    }

    protected Background getBackground(String color) {
        return new Background(new BackgroundFill(Paint.valueOf(color), new CornerRadii(5.0), new Insets(-5.0)));
    }


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
