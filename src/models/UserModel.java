package models;


import models.entities.User;
import tasks.UserDataRetrievalTask;
import web.TaskRunner;
import updating.Updater;

import java.io.IOException;
import java.net.URISyntaxException;

public class UserModel {

    private static UserModel instance;
    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        try {
            Updater.update();
        } catch (ClassNotFoundException | IOException | URISyntaxException ignored) {
        }
        this.currentUser = currentUser;
    }

    public void updateUser(Runnable onTaskComplete) {
        TaskRunner taskRunner = new TaskRunner(new UserDataRetrievalTask(), onTaskComplete);

        taskRunner.run();
    }

    public void updateUser() {
        updateUser(null);
    }

    public static UserModel getInstance() {
        if (instance == null) instance = new UserModel();
        return instance;
    }
}
