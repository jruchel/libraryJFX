package models;


public class UserModel {

    private static UserModel instance;
    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public static UserModel getInstance() {
        if (instance == null) instance = new UserModel();
        return instance;
    }
}
