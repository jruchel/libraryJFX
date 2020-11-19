package models;

public class RefundTableRepresentation {
    private String description;
    private double amount;
    private String currency;
    private String status;
    private String message;
    private String reason;

    public RefundTableRepresentation(String description, double amount, String currency, String status, String message, String reason) {
        this.description = description;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.message = message;
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
