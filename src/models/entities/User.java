package models.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class User {
    private int id;
    private String username;
    private List<Transaction> transactionList;
    private List<Book> reservedBooks;
    private Set<Role> roles;

    public User(int id, String username, List<Transaction> transactionList, List<Book> reservedBooks, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.transactionList = transactionList;
        this.reservedBooks = reservedBooks;
        this.roles = roles;
    }

    public List<Refund> getRefunds() {
        List<Refund> refunds = new ArrayList<>();
        transactionList.forEach(t -> refunds.addAll(t.getRefundList()));
        return refunds;
    }

    public boolean hasRole(String role) {
        for (Role r : roles) {
            if (r.getName().toLowerCase().contains(role.toLowerCase())) return true;
        }
        return false;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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

    public List<Book> getReservedBooks() {
        return reservedBooks;
    }

    public void setReservedBooks(List<Book> reservedBooks) {
        this.reservedBooks = reservedBooks;
    }
}
