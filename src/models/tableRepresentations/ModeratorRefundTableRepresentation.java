package models.tableRepresentations;

import utils.tableUtils.TableField;

public class ModeratorRefundTableRepresentation {
    @TableField
    private int id;
    @TableField
    private String username;
    @TableField
    private int transactionID;
    @TableField
    private double amount;
    @TableField
    private String currency;
    @TableField
    private boolean expired;
    @TableField
    private String description;

    public ModeratorRefundTableRepresentation(int id, String username, int transactionID, double amount, String currency, long time, String description) {
        this.id = id;
        this.username = username;
        this.transactionID = transactionID;
        this.amount = amount;
        this.currency = currency;
        this.expired = isExpired(time);
        this.description = description;
    }

    private boolean isExpired(long time) {
        long now = System.currentTimeMillis() / 1000;
        return now - time > 604800;// longer than 7 days
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

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
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

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
