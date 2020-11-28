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
    }

    public void updateUser(Runnable onTaskComplete) {
        TaskRunner taskRunner1 = new TaskRunner(() -> {
            try {
                Updater.update();
            } catch (ClassNotFoundException | IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }, onTaskComplete);

        TaskRunner taskRunner = new TaskRunner(new UserDataRetrievalTask(), taskRunner1);

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
