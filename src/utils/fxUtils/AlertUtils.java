package utils.fxUtils;

import javafx.scene.control.Alert;

public class AlertUtils {
    public static void showAlert(String message) {
        char endingChar = message.charAt(message.length() - 1);
        if ((endingChar != '.' || endingChar != '?' || endingChar != '!') && !message.contains("\n")) message += ".";
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }
}
