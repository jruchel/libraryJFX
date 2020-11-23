package models;

import utils.tableUtils.TableField;

import java.util.ArrayList;
import java.util.List;

public class Transaction {

    private int id;
    @TableField
    private String description;
    @TableField
    private double amount;
    @TableField
    private String currency;
    private String chargeID;
    private int time;
    @TableField
    private boolean refunded;
    private List<Refund> refundList;

    public Transaction(int id, double amount, String currency, String chargeID, int time, boolean refunded, String description, List<Refund> refunds) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.chargeID = chargeID;
        this.time = time;
        this.refunded = refunded;
        this.description = description;
        this.refundList = refunds;
    }

    public List<Refund> getRefundList() {
        return refundList;
    }

    public void setRefundList(List<Refund> refundList) {
        this.refundList = refundList;
    }

    public Transaction(int id, double amount, String currency, String chargeID, int time, boolean refunded, String description) {
        this(id, amount, currency, chargeID, time, refunded, description, new ArrayList<>());
    }

    public boolean hasPendingRefunds() {
        return refundList.stream().anyMatch(r -> r.getStatus().equals("Pending"));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getChargeID() {
        return chargeID;
    }

    public void setChargeID(String chargeID) {
        this.chargeID = chargeID;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isRefunded() {
        return refunded;
    }

    public void setRefunded(boolean refunded) {
        this.refunded = refunded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%s, %.2f %s, %s", description, amount, currency, refunded);
    }
}
