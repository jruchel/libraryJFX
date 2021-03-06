package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import models.UserModel;
import models.entities.CreditCard;
import services.PaymentService;
import utils.Properties;
import utils.Resources;
import utils.fxUtils.AlertUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;

public class SubscriptionPaneController extends Controller {

    @FXML
    private TextField cardNumberTextField;
    @FXML
    private TextField monthTextField;
    @FXML
    private TextField yearTextField;
    @FXML
    private PasswordField cvcTextField;
    @FXML
    protected Button acceptButton;
    @FXML
    protected AnchorPane cardDetailsPane;

    private UserModel userModel;

    public void initialize() {
        try {
            setButtonsStyle(Resources.getStyle("button1"));
        } catch (IllegalAccessException | IOException ignored) {
        }
        userModel = UserModel.getInstance();
        userModel.updateUser();
        initializeManually();
        setKeyPresses(cardDetailsPane, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                onAccept();
            }
        });
        try {
            setFont(Button.class, Font.font (globalFontFamily, 14));
            setFont(Label.class, Font.font (globalFontFamily, 14));
        } catch (Exception ignored) {
        }
    }

    public void onCreditCardInput() {
        limitTextFieldSize(cardNumberTextField, 16);
    }

    public void onCVCInput() {
        limitTextFieldSize(cvcTextField, 3);
    }

    public void onYearInput() {
        limitTextFieldSize(yearTextField, 2);
    }

    public void onMonthInput() {
        limitTextFieldSize(monthTextField, 2);
    }

    private void limitTextFieldSize(TextField textField, int limit) {
        String text = textField.getText();
        if (text.length() > limit) {
            textField.setText(text.substring(0, limit));
            textField.positionCaret(limit);
        }
    }

    public void onAccept() {
        if (userModel.getCurrentUser().getRoles().contains("ROLE_SUBSCRIBER")) {
            AlertUtils.showAlert("You are already subscribed");
            return;
        }
        final String[] message = {"Payment booked"};
        Runnable onComplete = () -> {
            userModel.updateUser();
            switch (message[0]) {
                case "success":
                    message[0] = "You are now subscribed";
                    break;
                case "failure":
                    message[0] = "Failed to process payment";
            }
            Platform.runLater(() -> AlertUtils.showAlert(message[0]));
        };
        String error = checkInput();
        if (error.length() > 0) {
            AlertUtils.showAlert(error);
        } else {
            try {
                double price = Double.parseDouble(Properties.getProperty("subscription.price"));
                String currency = Properties.getProperty("subscription.currency");
                PaymentService.makeSubscriptionPayment(getCreditCard(), price, currency, "Subscription payment", onComplete, message);
            } catch (IOException e) {
                message[0] = "Failed to process payment";
            }
        }
    }

    private CreditCard getCreditCard() {
        long number = Long.parseLong(cardNumberTextField.getText());
        int month = Integer.parseInt(monthTextField.getText());
        int year = Integer.parseInt(yearTextField.getText());
        int cvc = Integer.parseInt(cvcTextField.getText());
        return new CreditCard(number, month, year, cvc);
    }

    private String checkInput() {
        StringBuilder error = new StringBuilder();
        if (!isCreditCardNumber(cardNumberTextField.getText())) error.append("Incorrect credit card number\n");
        if (!isCorrectDate(monthTextField.getText(), yearTextField.getText())) error.append("Incorrect date\n");
        if (!isCorrectCVC((cvcTextField.getText()))) error.append("Incorrect CVC code\n");
        return error.toString();
    }

    private boolean isCreditCardNumber(String number) {
        try {
            if (number.length() != 16) return false;
            Long.parseLong(number);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isCorrectDate(String month, String year) {
        try {
            int monthInt = Integer.parseInt(month);
            int yearInt = Integer.parseInt(year);
            if (monthInt < 1 || monthInt > 12) return false;
            if (yearInt < 0) return false;
            yearInt += 2000;
            return YearMonth.from(LocalDate.of(yearInt, monthInt, 1)).compareTo(YearMonth.now()) > 0;

        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isCorrectCVC(String cvc) {
        return cvc.length() == 3;
    }


}
