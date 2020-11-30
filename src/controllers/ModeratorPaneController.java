package controllers;

import utils.fxUtils.AlertUtils;
import utils.fxUtils.SceneController;

import java.io.IOException;

public class ModeratorPaneController extends Controller {
    public void initialize() {
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
