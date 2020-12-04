package controllers;

import controllers.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import utils.Resources;

import java.io.IOException;

public class ModeratorPaneController extends Controller {

    @FXML
    private BorderPane moderatorPane;
    @FXML
    protected Button refundsButton;

    public void initialize() {
        initializeManually();
    }

}
