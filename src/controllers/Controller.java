package controllers;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import updating.ControllerAccess;
import utils.Properties;
import utils.Resources;
import utils.fxUtils.SceneController;
import web.Requests;

import javafx.event.EventHandler;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


public abstract class Controller {
    protected String appURL;
    protected Requests requests;
    protected static String defaultButtonStyle;
    protected static String clickedButtonStyle;
    protected static String globalFontFamily;

    public static void setGlobalFontFamily(String globalFontFamily) {
        Controller.globalFontFamily = globalFontFamily;
    }

    public static void setClickedButtonStyle(String clickedButtonStyle) {
        Controller.clickedButtonStyle = clickedButtonStyle;
    }

    public static void setDefaultButtonStyle(String defaultButtonStyle) {
        Controller.defaultButtonStyle = defaultButtonStyle;
    }

    public static String getClickedButtonStyle() {
        return clickedButtonStyle;
    }

    public static String getDefaultButtonStyle() {
        return defaultButtonStyle;
    }

    public void initializeManually() {
        try {
            appURL = Properties.getProperty("site.url");
        } catch (IOException ignored) {
        }
        requests = Requests.getInstance();
        ControllerAccess.getInstance().put(this.getClass().getName(), this);
        try {
            if (!defaultButtonStyle.isEmpty()) setButtonsStyle(defaultButtonStyle);
            if (!clickedButtonStyle.isEmpty()) setButtonAnimation(clickedButtonStyle, 1000);
            setNodeStyle(TableView.class, Resources.getStyle("table"));
        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    private void setFont(List<? extends Labeled> nodes, Font font) {
        for (Labeled n : nodes) {
            if (n != null) {
                n.setFont(font);
            }
        }
    }

    public void setFont(String partialID, Font font) throws IllegalAccessException {
        List<Node> nodes = findNodes(partialID);
        setFont(nodes.stream().map(node -> (Labeled) node).collect(Collectors.toList()), font);
    }

    public void setFont(Class<? extends Labeled> cls, Font font) throws IllegalAccessException {
        List<Node> buttons = findNodes(cls);
        setFont(buttons.stream().map(node -> (Labeled) node).collect(Collectors.toList()), font);
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

    protected Background getBackground(Paint paint) {
        return new Background(new BackgroundFill(paint, new CornerRadii(5.0), new Insets(-5.0)));
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

    protected void setButtonsStyle(String style) throws IllegalAccessException {
        setNodeStyle(Button.class, style);
    }

    protected void setNodeStyle(Class<? extends Node> c, String style) throws IllegalAccessException {
        findNodes(c).forEach(cls -> cls.setStyle(style));
    }

    protected void setButtonAnimation(String style, int time) throws IllegalAccessException {
        findNodes(Button.class).forEach(b -> b.setOnMouseClicked(event -> {
            new Thread(() -> {
                String defaultStyle = b.getStyle();
                b.setStyle(style);
                try {
                    b.setDisable(true);
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                b.setDisable(false);
                b.setStyle(defaultStyle);
            }).start();
        }));
    }

    protected void setKeyPresses(Pane mainPane, EventHandler<KeyEvent> onEnter) {
        mainPane.setOnKeyPressed(onEnter);
    }

    protected Node findNode(String id) throws IllegalAccessException {
        for (Field f : this.getClass().getDeclaredFields()) {
            Node node = (Node) f.get(this);
            if (node.getId().equals(id)) return node;
        }
        return null;
    }

    protected List<Node> findNodes(String partialID) throws IllegalAccessException {
        List<Node> nodes = new ArrayList<>();
        for (Field f : this.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if(f.get(this) instanceof Node) {
                Node node = (Node) f.get(this);
                if (node.getId().contains(partialID)) nodes.add(node);

            }
            f.setAccessible(false);
        }
        return nodes;
    }

    protected List<Node> findNodes(Class<? extends Node> c) throws IllegalAccessException {
        List<Node> nodes = new ArrayList<>();
        for (Field f : this.getClass().getDeclaredFields()) {
            if (f.getType().getName().equals(c.getName())) {
                f.setAccessible(true);
                nodes.add((Node) f.get(this));
                f.setAccessible(false);
            }
        }
        return nodes;
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
