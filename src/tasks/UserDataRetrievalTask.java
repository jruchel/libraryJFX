package tasks;

import models.*;
import models.entities.*;
import web.Requests;
import javafx.util.Pair;
import utils.parsing.JsonReader;
import utils.Properties;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDataRetrievalTask implements Runnable {

    private String siteUrl;
    private Map<String, Object> parameters;
    private Requests requests;

    public UserDataRetrievalTask(Map<String, Object> parameters) {
        this.parameters = parameters;
        this.requests = Requests.getInstance();
        try {
            this.siteUrl = Properties.getProperty("site.url");
        } catch (IOException ignored) {
        }
    }

    public UserDataRetrievalTask() {
        this(new HashMap<>());
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    private List<Book> getBooks(String data) throws IOException {
        List<Pair<Integer, String>> books = getBooksIdsAndTitles(getBooksString(data));
        List<Book> bookList = new ArrayList<>();
        for (Pair<Integer, String> p : books) {
            int authorID = Integer.parseInt(requests.sendRequest(String.format("%s/books/author/%d", siteUrl, p.getKey()), "GET"));
            String authorName = requests.sendRequest(String.format("%s/authors/%d", siteUrl, authorID), "GET");
            bookList.add(new Book(p.getValue(), getAuthorName(authorName), p.getKey()));
        }
        return bookList;
    }

    private String getRentedBooksString(String data) {
        data = data.replaceAll("\\{\\\"id.+\\\"refunds\\\"", "");
        data = data.replaceAll("\\\"roles\\\".+\\}\\]\\}", "");
        data = data.replaceAll("", "");
        data = data.replaceAll(".+reservedBooks.+\\}\\]\\,\\\"", "");
        String regex = ".*rentedBooks\":\\[\\{(.+)}],";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        if (matcher.matches()) return matcher.group(1);
        return "";
    }

    private String getBooksString(String data) {
        data = data.replaceAll("\\{\\\"id.+\\\"refunds\\\"", "");
        data = data.replaceAll("\\\"roles\\\".+\\}\\]\\}", "");
        String regex = ".+\\\"reservedBooks\\\"\\:\\[\\{(.+)\\}\\]\\,\\\"rentedBooks\\\".+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        if (matcher.matches()) return matcher.group(1);
        return "";
    }

    private String getAuthorName(String data) {
        data = data.replaceAll("\\\"bibliography\\\".+", "");
        String regex = "\\{\\\"id\\\"\\:\\d+\\,\\\"name\\\"\\:\\\"(.+)\\\"\\,";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        if (matcher.matches()) return matcher.group(1);
        return "";
    }

    private List<Pair<Integer, String>> getBooksIdsAndTitles(String data) {
        String[] temp = data.split("},");
        for (int i = 0; i < temp.length; i++) {
            temp[i] = temp[i].replace("{", "");
        }
        List<Pair<Integer, String>> results = new ArrayList<>();
        Pattern pattern = Pattern.compile(".*\\\"id\\\"\\:(\\d+)\\,\\\"title\\\"\\:\\\"([A-Z0-9a-z!?.,() ]+)\\\".+");
        Matcher matcher;
        for (String s : temp) {
            matcher = pattern.matcher(s);
            if (matcher.matches()) {
                results.add(new Pair<>(Integer.parseInt(matcher.group(1)), matcher.group(2)));
            }
        }
        return results;
    }

    private String getUsername(String data) {
        return JsonReader.readFromJson("username", data);
    }

    private int getID(String data) {
        data = data.replaceAll("\\\"roles\\\".*", "");
        String result = JsonReader.readFromJson("id", data);
        return Integer.parseInt(result);
    }

    private String getTransactionString(String data) {
        data = data.replaceAll("\\\"id\\\".+\\\"passwordConfirm\\\"\\:\\w+\\,", "");
        data = data.replaceAll("\\\"reservedBooks\\\".+", "");
        String regex = ".*\\\"transactions\\\"\\:\\[(.+)\\]\\,.*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return "";
    }

    private String[] getTransactions(String data) {
        try {
            return data.split("},");
        } catch (Exception ex) {
            return new String[]{data};
        }
    }

    private List<Transaction> getTransactionsList(String data) {
        List<Transaction> transactions = new ArrayList<>();
        String[] transactionsStrings = getTransactions(getTransactionString(data));
        if (transactionsStrings[0].isEmpty()) return transactions;
        for (String s : transactionsStrings) {
            transactions.add(getTransaction(s));
        }
        return transactions;
    }

    private List<Refund> getRefunds(String data, int transactionID, String description, double amount, String currency) {
        String refundsJSON = JsonReader.readFromJson("refunds", data);
        String[] refunds = JsonReader.readFromArray(refundsJSON);

        List<Refund> refundList = new ArrayList<>();
        for (String s : refunds) {
            if (s.isEmpty()) continue;
            int id = Integer.parseInt(JsonReader.readFromJson("id", s));
            String status = JsonReader.readFromJson("status", s);
            String message = JsonReader.readFromJson("message", s);
            String reason = JsonReader.readFromJson("reason", s);
            refundList.add(new Refund(id, transactionID, description, amount, currency, status, message, reason));
        }
        return refundList;
    }

    private Transaction getTransaction(String data) {
        int id = -1;
        double amount;
        String currency;
        String chargeID;
        int time;
        boolean refunded;
        String description;
        List<Refund> refundList;
        try {
            id = Integer.parseInt(JsonReader.readFromJson("id", data.replaceAll("\\[\\{\\\"id.+", "")));
            amount = Double.parseDouble(JsonReader.readFromJson("amount", data));
            currency = JsonReader.readFromJson("currency", data);
            chargeID = JsonReader.readFromJson("chargeID", data);
            time = Integer.parseInt(JsonReader.readFromJson("time", data));
            refunded = Boolean.parseBoolean(JsonReader.readFromJson("refunded", data));
            description = JsonReader.readFromJson("description", data);
            refundList = getRefunds(data, id, description, amount, currency);
            return new Transaction(id, amount, currency, chargeID, time, refunded, description, refundList);
        } catch (Exception ex) {
            return new Transaction(id, -1, "", "", -1, false, "");
        }
    }

    private Set<Role> getRoles(String data) {
        Set<Role> roles = new HashSet<>();
        String[] rolesAsArray = JsonReader.readFromArray(data);

        for (String s : rolesAsArray) {
            int id = Integer.parseInt(JsonReader.readFromJson("id", s));
            String name = JsonReader.readFromJson("name", s);
            roles.add(new Role(id, name));
        }
        return roles;
    }

    @Override
    public void run() {
        try {
            String rolesData = requests.sendRequest(String.format("%s/user/roles", siteUrl), "GET");
            Set<Role> roles = getRoles(rolesData);
            String data = requests.sendRequest(String.format("%s/user", siteUrl), "GET");
            User user = new User(getID(data), getUsername(data), getTransactionsList(data), getBooks(data), roles);
            UserModel.getInstance().setCurrentUser(user);
        } catch (Exception ignored) {
        }
    }

}
