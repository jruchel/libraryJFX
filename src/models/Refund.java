package models;

import utils.TableField;

public class Refund {

    @TableField
    private String description;
    @TableField
    private double amount;
    @TableField
    private String currency;
    private int id;
    @TableField
    private String status;
    @TableField
    private String message;
    @TableField
    private String reason;


    public Refund(int id, String description, double amount, String currency, String status, String message, String reason) {
        this.description = description;
        this.amount = amount;
        this.currency = currency;
        this.id = id;
        this.status = status;
        this.message = message;
        this.reason = reason;
        if (this.reason == null || this.reason.equals("null")) this.reason = "";
    }

    public Refund() {

    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
