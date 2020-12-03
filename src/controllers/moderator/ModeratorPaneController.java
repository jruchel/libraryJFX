package controllers.moderator;

import controllers.Controller;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class ModeratorPaneController extends Controller {

    @FXML
    private BorderPane moderatorPane;

    public void initialize() {
        initializeManually();
    }

}
