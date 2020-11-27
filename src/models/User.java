package models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String username;
    private List<Transaction> transactionList;
    private List<Book> rentedBooks;
    private List<Book> reservedBooks;

    public User(int id, String username, List<Transaction> transactionList, List<Book> rentedBooks, List<Book> reservedBooks) {
        this.id = id;
        this.username = username;
        this.transactionList = transactionList;
        this.rentedBooks = rentedBooks;
        this.reservedBooks = reservedBooks;
    }

    public List<Refund> getRefunds() {
        List<Refund> refunds = new ArrayList<>();
        transactionList.stream().forEach(t -> refunds.addAll(t.getRefundList()));
        return refunds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public List<Book> getRentedBooks() {
        return rentedBooks;
    }

    public void setRentedBooks(List<Book> rentedBooks) {
        this.rentedBooks = rentedBooks;
    }

    public List<Book> getReservedBooks() {
        return reservedBooks;
    }

    public void setReservedBooks(List<Book> reservedBooks) {
        this.reservedBooks = reservedBooks;
    }
}
