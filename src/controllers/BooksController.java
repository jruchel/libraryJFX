package controllers;

import web.Requests;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import models.entities.Book;
import models.UserModel;
import web.TaskRunner;
import utils.fxUtils.AlertUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class BooksController extends Controller {

    @FXML
    private ListView<Book> rentedListView;
    @FXML
    private ListView<Book> reservedListView;

    private List<Book> reservedBookList;
    private List<Book> rentedBookList;

    private UserModel userModel;
    private Requests requests;

    public void initialize() {
        userModel = UserModel.getInstance();
        reservedBookList = userModel.getCurrentUser().getReservedBooks();
        rentedBookList = userModel.getCurrentUser().getRentedBooks();
        requests = Requests.getInstance();
        rentedListView.getItems().addAll(rentedBookList);
        reservedListView.getItems().addAll(reservedBookList);
        initializeManually();
    }

    public void reserve() {
    }

    public void cancelReserved() {
        final int[] id = {reservedListView.getSelectionModel().getSelectedItem().getId()};
        Runnable cancelReservationTask = () -> {
            Platform.runLater(() -> {
                try {
                    requests.sendRequest(String.format("%s/rental/reserve/%d", appURL, id[0]), "POST");
                } catch (IOException e) {
                    AlertUtils.showAlert("Canceling reservation failed, please try again later");
                }
            });


        };
        Runnable onTaskComplete = () -> {
            reservedBookList = reservedBookList.stream().filter(b -> b.getId() != id[0]).collect(Collectors.toList());
            Platform.runLater(() -> {
                reservedListView.getItems().clear();
                reservedListView.getItems().addAll(reservedBookList);
                reservedListView.getSelectionModel().clearSelection();
            });

        };
        TaskRunner taskRunner = new TaskRunner(cancelReservationTask, onTaskComplete);
        taskRunner.run();
    }

    @Override
    public void onInit() {

    }
}
