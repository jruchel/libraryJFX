package services;

import web.Requests;
import models.entities.CreditCard;
import utils.Properties;
import web.TaskRunner;

import java.io.IOException;

public class PaymentService {

    private static String siteURL;

    public static void makePayment(CreditCard creditCard, double amount, String currency, String description, Runnable onComplete) throws IOException {
        if (siteURL == null || siteURL.isEmpty()) {
            siteURL = Properties.getProperty("site.url");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(String.format("\"card\":{\"number\":%d, \"expirationMonth\":%d, \"expirationYear\":%d, \"cvc\":%d},",
                creditCard.getNumber(),
                creditCard.getExpirationMonth(),
                creditCard.getExpirationYear(),
                creditCard.getCvc()));
        sb.append("\"amount\":").append(amount).append(",");
        sb.append("\"currency\":").append("\"").append(currency).append("\",");
        sb.append("\"description\":").append("\"").append(description).append("\"").append("}");
        String[] response = {""};
        TaskRunner taskRunner = new TaskRunner(() -> {
            try {
                response[0] = Requests.getInstance().sendRequest(String.format("%s/payments/user/card", siteURL), sb.toString(), "POST");
            } catch (IOException e) {
                response[0] = "failure";
            }
        }, onComplete);
        taskRunner.run();
    }

}