package models;

public class UserModel {

    private static UserModel instance;

    public static UserModel getInstance() {
        if (instance == null) instance = new UserModel();
        return instance;
    }

    private String username;

    public String getUsername() {
        if (username == null) {
        }
        return username;
    }
}
