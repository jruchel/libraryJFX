package utils.fxUtils;

import javafx.scene.control.Alert;

public class AlertUtils {
    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message + ".");
        alert.showAndWait();
    }
}
