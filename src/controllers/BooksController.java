package controllers;

import javafx.scene.control.Button;
import updating.OnUpdate;
import web.Requests;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import models.entities.Book;
import models.UserModel;
import web.TaskRunner;
import utils.fxUtils.AlertUtils;
import java.util.List;

public class BooksController extends Controller {

    @FXML
    protected ListView<Book> booksListView;
    @FXML
    protected Button returnButton;

    protected List<Book> userBooks;
    protected UserModel userModel;

    public void initialize() {
        userModel = UserModel.getInstance();
        userBooks = userModel.getCurrentUser().getReservedBooks();
        requests = Requests.getInstance();
        booksListView.getItems().addAll(userBooks);
        initializeManually();
    }

    @OnUpdate(updatedBy = {UserModel.class})
    public void onUpdate() {
        userBooks = userModel.getCurrentUser().getReservedBooks();
        booksListView.getItems().removeAll(booksListView.getItems());
        booksListView.getItems().addAll(userBooks);
    }

    public int getSelectedBookID() {
        return booksListView.getSelectionModel().getSelectedItem().getId();
    }

    public void onBookReturn() {
        if (userBooks.size() == 0) return;
        String[] error = {""};
        Runnable cancelReservationTask = () -> {
            try {
                requests.sendRequest(String.format("%s/rental/reserve/%d", appURL, getSelectedBookID()), "DELETE");
            } catch (Exception e) {
                if (e instanceof NullPointerException)
                    error[0] = "No book selected";
                else
                    error[0] = e.getMessage();
            }
        };
        Runnable onTaskComplete = () -> {
            userModel.updateUser();
            Platform.runLater(() -> {
                if (!error[0].isEmpty())
                    AlertUtils.showAlert(error[0]);
            });

        };
        TaskRunner taskRunner = new TaskRunner(cancelReservationTask, onTaskComplete);
        taskRunner.run();
    }
}
