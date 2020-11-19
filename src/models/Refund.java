package models;

public class Refund {
    private int id;
    private String status;
    private String message;
    private String reason;

    public Refund(int id, String status, String message, String reason) {
        this.id = id;
        this.status = status;
        this.message = message;
        this.reason = reason;
        if (this.reason == null) this.reason = "";
    }

    public Refund() {

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
