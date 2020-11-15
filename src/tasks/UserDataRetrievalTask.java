package tasks;

import models.Book;
import connection.Requests;
import javafx.util.Pair;
import models.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDataRetrievalTask implements Runnable {

    private boolean success;
    private Map<String, Object> parameters;
    private Requests requests;

    public UserDataRetrievalTask(Map<String, Object> parameters) {
        this.success = false;
        this.parameters = parameters;
        this.requests = Requests.getInstance();
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    private List<Book> getRentedBooksList(String data) throws IOException {
        List<Pair<Integer, String>> books = getBooksIdsAndTitles(getRentedBooksString(data));
        List<Book> bookList = new ArrayList<>();
        for (Pair<Integer, String> p : books) {
            int authorID = Integer.parseInt(requests.getResponseBody(String.format("http://localhost:8080/books/author/%d", p.getKey())));
            String authorName = requests.getResponseBody(String.format("http://localhost:8080/authors/%d", authorID));
            bookList.add(new Book(p.getValue(), getAuthorName(authorName), p.getKey()));
        }
        return bookList;
    }

    private List<Book> getReservedBooksList(String data) throws IOException {
        List<Pair<Integer, String>> books = getBooksIdsAndTitles(getReservedBooksString(data));
        List<Book> bookList = new ArrayList<>();
        for (Pair<Integer, String> p : books) {
            int authorID = Integer.parseInt(requests.getResponseBody(String.format("http://localhost:8080/books/author/%d", p.getKey())));
            String authorName = requests.getResponseBody(String.format("http://localhost:8080/authors/%d", authorID));
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

    private String getReservedBooksString(String data) {
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
        String regex = "\\{\\\"id\\\"\\:\\d+\\,\\\"name\\\"\\:\\\"(.+)\\,";
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
        String regex = "\\\"username\\\"\\:\\\"(\\w+)\\\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;
        for (String s : data.split(",")) {
            matcher = pattern.matcher(s);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        return "";
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
        for (String s : transactionsStrings) {
            transactions.add(getTransaction(s));
        }
        return transactions;
    }

    private Transaction getTransaction(String data) {
        String idRegex = ".+\\\"id\\\"\\:(\\d+).+";
        String amountRegex = ".+\\\"amount\\\"\\:([0-9.]+).+";
        String currencyRegex = ".+\\\"currency\\\"\\:\\\"(\\w+)\\\".+";
        String chargeIDRegex = ".+\\\"chargeID\\\"\\:\\\"(\\w+)\\\".+";
        String timeRegex = ".+\\\"time\\\"\\:(\\d+).+";
        String refundedRegex = ".+\\\"refunded\\\"\\:(\\w+).+";
        String descriptionRegex = ".+\\\"description\\\"\\:\\\"([a-zA-Z0-9 ]+)\\\".+";
        int id = 0;
        Pattern pattern = Pattern.compile(idRegex);
        Matcher matcher = pattern.matcher(data);
        if (matcher.matches()) id = Integer.parseInt(matcher.group(1));
        double amount = 0;
        pattern = Pattern.compile(amountRegex);
        matcher = pattern.matcher(data);
        if (matcher.matches()) amount = Double.parseDouble(matcher.group(1));
        String currency = "";
        pattern = Pattern.compile(currencyRegex);
        matcher = pattern.matcher(data);
        if (matcher.matches()) currency = matcher.group(1);
        String chargeID = "";
        pattern = Pattern.compile(chargeIDRegex);
        matcher = pattern.matcher(data);
        if (matcher.matches()) chargeID = matcher.group(1);
        int time = 0;
        pattern = Pattern.compile(timeRegex);
        matcher = pattern.matcher(data);
        if (matcher.matches()) time = Integer.parseInt(matcher.group(1));
        boolean refunded = false;
        pattern = Pattern.compile(refundedRegex);
        matcher = pattern.matcher(data);
        if (matcher.matches()) refunded = Boolean.parseBoolean(matcher.group(1));
        String description = null;
        pattern = Pattern.compile(descriptionRegex);
        matcher = pattern.matcher(data);
        if (matcher.matches()) description = matcher.group(1);
        return new Transaction(id, amount, currency, chargeID, time, refunded, description);
    }

    @Override
    public void run() {
        if (success) {
            try {
                String data = requests.getResponseBody("http://localhost:8080/user");
                parameters.put("username", getUsername(data));
                parameters.put("reservedBooks", getReservedBooksList(data));
                parameters.put("rentedBooks", getRentedBooksList(data));
                parameters.put("transactions", getTransactionsList(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
