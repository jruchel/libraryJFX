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
        this.currentUser = currentUser;
        try {
            Updater.update(this.getClass());
        } catch (ClassNotFoundException | IOException | URISyntaxException ignored) {
        }
    }

    public void updateUser(Runnable onTaskComplete) {
        TaskRunner taskRunner = new TaskRunner(new UserDataRetrievalTask(), onTaskComplete);
        TaskRunner taskRunner2 = new TaskRunner(taskRunner, () -> {
            try {
                Updater.update(this.getClass());
            } catch (ClassNotFoundException | IOException | URISyntaxException ignored) {
            }
        });
        taskRunner2.run();
    }

    public void updateUser() {
        updateUser(null);
    }

    public static UserModel getInstance() {
        if (instance == null) instance = new UserModel();
        return instance;
    }
}
