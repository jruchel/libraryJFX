package services;

import web.Requests;
import models.entities.CreditCard;
import utils.Properties;
import web.TaskRunner;

import java.io.IOException;

public class PaymentService {

    private static String siteURL;

    public static void makePayment(CreditCard creditCard, double amount, String currency, String description, Runnable onComplete) throws IOException {
        makePayment(creditCard, amount, currency, description, onComplete, "/payments/user/card", new String[0]);
    }

    public static void makeSubscriptionPayment(CreditCard creditCard, double amount, String currency, String description, Runnable onComplete) throws IOException {
        makePayment(creditCard, amount, currency, description, onComplete, "/payments/user/subscribe", new String[0]);
    }

    public static void makeSubscriptionPayment(CreditCard creditCard, double amount, String currency, String description, Runnable onComplete, String[] responseCallback) throws IOException {
        makePayment(creditCard, amount, currency, description, onComplete, "/payments/user/subscribe", responseCallback);
    }

    private static void makePayment(CreditCard creditCard, double amount, String currency, String description, Runnable onComplete, String url, String[] responseCallback) throws IOException {
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
                response[0] = Requests.getInstance().sendRequest(String.format("%s%s", siteURL, url), sb.toString(), "POST");
            } catch (IOException e) {
                response[0] = "failure";
            }
            responseCallback[0] = response[0];
        }, onComplete);
        taskRunner.run();
    }

}
