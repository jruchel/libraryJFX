package tasks;

import models.ModeratorDataModel;
import models.tableRepresentations.ModeratorRefundTableRepresentation;
import models.entities.Transaction;
import utils.Properties;
import utils.parsing.JsonReader;
import web.Requests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModeratorRefundDataRetrievalTask implements Runnable {
    private Requests requests;

    private List<Integer> getRefundIDs(String data) {
        List<Integer> result = new ArrayList<>();
        String[] datas = JsonReader.readFromArray(data);
        for (String s : datas) {
            result.add(Integer.valueOf(JsonReader.readFromJson("id", s)));
        }
        return result;
    }

    private Transaction getTransactionData(String data) {
        int transactionID = Integer.parseInt(JsonReader.readFromJson("id", data.replaceAll("card.*", "")));
        double amount = Double.parseDouble(JsonReader.readFromJson("amount", data));
        String chargeID = JsonReader.readFromJson("chargeID", data);
        String currency = JsonReader.readFromJson("currency", data);
        int time = Integer.parseInt(JsonReader.readFromJson("time", data));
        boolean refunded = Boolean.parseBoolean(JsonReader.readFromJson("refunded", data));
        String description = JsonReader.readFromJson("description", data);

        return new Transaction(transactionID, amount, currency, chargeID, time, refunded, description);
    }

    private String getUsername(String data) {
        return JsonReader.readFromJson("username", data);
    }

    @Override
    public void run() {
        try {
            requests = Requests.getInstance();
            String refundsData;
            List<ModeratorRefundTableRepresentation> resultData = new ArrayList<>();
            refundsData = requests.sendRequest(String.format("%s/payments/moderator/refunds", Properties.getSiteURL()), "GET");
            List<Integer> refundIDs = getRefundIDs(refundsData);

            for (Integer i : refundIDs) {
                Transaction t = getTransactionData(requests.sendRequest(String.format("%s/payments/moderator/transaction/%d", Properties.getSiteURL(), i), "GET"));
                String usernameData = requests.sendRequest(String.format("%s/user/moderator/byRefund/%d", Properties.getSiteURL(), i), "GET");
                resultData.add(new ModeratorRefundTableRepresentation(i, getUsername(usernameData), t.getId(), t.getAmount(), t.getCurrency(), t.getTime(), t.getDescription()));
            }

            ModeratorDataModel.getInstance().setRefunds(resultData);
        } catch (Exception ignored) {

        }
    }
}
