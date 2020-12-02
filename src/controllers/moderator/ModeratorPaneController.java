package controllers.moderator;

import controllers.Controller;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import utils.fxUtils.AlertUtils;
import utils.fxUtils.SceneController;

import java.io.IOException;

public class ModeratorPaneController extends Controller {

    @FXML
    private BorderPane moderatorPane;

    public void initialize() {
        setBackground("file:src/resources/images/mainBg2.jpg", moderatorPane, 1200, 685);
        initializeManually();
    }

    public void onReturn() {
        try {
            SceneController.startScene("userPane");
        } catch (IOException e) {
            AlertUtils.showAlert("Failure showing user pane");
        }
    }
}
