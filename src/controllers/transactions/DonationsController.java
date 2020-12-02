package controllers.transactions;

import controllers.Controller;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import models.entities.CreditCard;
import models.UserModel;
import services.PaymentService;
import utils.fxUtils.AlertUtils;
import web.Requests;
import web.TaskRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DonationsController extends Controller {
    @FXML
    private ChoiceBox<Double> amountChoiceBox;
    @FXML
    private CheckBox otherCheckBox;
    @FXML
    private TextField otherAmountTextField;
    @FXML
    private ChoiceBox<String> currencyChoiceBox;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private Label amountLabel;
    @FXML
    private Label currencyLabel;
    @FXML
    private Label descriptionLabel;

    private List<Double> amounts;
    private List<String> currencies;

    private void getAvailableCurrencies() {
        String[] response = {""};
        Runnable onTaskComplete = () -> {
            currencies = parseCurrencies(response[0]);
            currencyChoiceBox.getItems().removeAll(currencyChoiceBox.getItems());
            currencyChoiceBox.getItems().addAll(currencies);
        };
        TaskRunner taskRunner = new TaskRunner(() -> {
            try {
                response[0] = Requests.getInstance().sendRequest(String.format("%s/payments/currencies", appURL), "GET");
            } catch (IOException ignored) {
            }
        }, onTaskComplete
        );
        taskRunner.run();
    }

    private List<String> parseCurrencies(String response) {
        response = response.replaceAll("[\\[\\]\"]", "");
        return Arrays.asList(response.split(","));
    }

    public void initialize() {
        getAvailableCurrencies();
        amounts = new ArrayList<>();
        currencies = new ArrayList<>();
        amounts.addAll(Arrays.asList(5.0, 10.0, 15.0, 20.0, 50.0));
        amountChoiceBox.getItems().clear();
        amountChoiceBox.getItems().addAll(amounts);
        currencyChoiceBox.getItems().clear();
        currencyChoiceBox.getItems().addAll(currencies);
        initializeManually();

    }

    public void otherPicked() {
        boolean isSelected = otherCheckBox.isSelected();
        otherAmountTextField.setDisable(!isSelected);
        amountChoiceBox.setDisable(isSelected);
    }

    public void reset() {
        amountChoiceBox.setValue(null);
        amountChoiceBox.setDisable(false);
        currencyChoiceBox.setValue(null);
        otherAmountTextField.setDisable(true);
        otherCheckBox.setSelected(false);
    }


    public void sendDonation() {
        StringBuilder error = new StringBuilder();

        double amount = 0;
        String currency, description;
        //Amount validation
        try {
            if (amountChoiceBox.isDisabled()) {
                amount = Double.parseDouble(otherAmountTextField.getText());
            } else {
                amount = amountChoiceBox.getValue();
            }
            if (amount <= 0) error.append(String.format("Amount cannot be %f\n", amount));
        } catch (Exception ex) {
            error.append("Amount must be a number\n");
        }
        currency = currencyChoiceBox.getValue();
        if (currency == null) error.append("Currency must be picked");
        description = descriptionTextArea.getText();
        if (description.isEmpty()) description = "Donation";
        if (error.length() > 0) AlertUtils.showAlert(error.toString());
        else {
            try {
                PaymentService.makePayment(getDefaultCreditCard(), amount, currency, description, () -> {
                    Platform.runLater(() -> {
                        AlertUtils.showAlert("Transaction complete");
                        reset();
                    });

                    UserModel.getInstance().updateUser();
                });
            } catch (IOException e) {
                AlertUtils.showAlert("Transaction failure");
            }
        }
    }

    private CreditCard getDefaultCreditCard() {
        return new CreditCard(4242424242424242L, 5, 2022, 333);
    }

}
