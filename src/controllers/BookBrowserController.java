package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import models.BookModel;
import models.UserModel;
import models.entities.Book;
import tasks.BookDataRetrievalTask;
import updating.Updater;
import utils.fxUtils.AlertUtils;
import utils.fxUtils.SceneController;
import web.TaskRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class BookBrowserController extends Controller {

    @FXML
    private Pagination booksOrAuthorsPagination;
    @FXML
    private CheckBox authorsCheckBox;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField titleTextField;
    private ListView<Book> currentPage;
    @FXML
    private BorderPane browserPane;

    private BookModel bookModel;
    private UserModel userModel;

    public void initialize() {
       setBackground("file:src/resources/images/mainBg2.jpg", browserPane, 1200, 685);
        booksOrAuthorsPagination.setPageFactory(param -> {
            currentPage = new ListView<>();
            currentPage.getItems().addAll(new ArrayList<>());
            Platform.runLater(() -> booksOrAuthorsPagination.setPageCount(1));
            return currentPage;
        });
        bookModel = BookModel.getInstance();
        userModel = UserModel.getInstance();
        initializeManually();
    }

    public void onReserve() {
        userModel.updateUser(() -> {
            Book selectedBook = getSelectedBook();
            if (userModel.getCurrentUser().getReservedBooks().contains(selectedBook))
                Platform.runLater(() -> AlertUtils.showAlert("You already own this book"));
            else {
                Runnable reserveBook = () -> {
                    String response = "";
                    try {
                        response = requests.sendRequest(String.format("%s/rental/reserve/%d", appURL, selectedBook.getId()), "POST");
                    } catch (IOException e) {
                        Platform.runLater(() -> AlertUtils.showAlert("Error occurred while reserving book"));
                    }
                    if (response.equals("false"))
                        Platform.runLater(() -> AlertUtils.showAlert("Book reservation failed"));
                };
                TaskRunner taskRunner = new TaskRunner(reserveBook, () -> {
                    Platform.runLater(() -> AlertUtils.showAlert("Book reserved successfully"));
                    userModel.updateUser();
                });
                taskRunner.run();
            }
        });
        return;
    }

    private Book getSelectedBook() {
        return currentPage.getSelectionModel().getSelectedItem();
    }

    public void onBoxChecked() {
        boolean checked = authorsCheckBox.isSelected();
        lastNameTextField.setDisable(!checked);
        firstNameTextField.setDisable(!checked);
        titleTextField.setDisable(checked);
    }

    private String getInputTitle() {
        return titleTextField.getText();
    }

    public void onSearch() {
        new Thread(() -> {
            titleTextField.setDisable(true);
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {

            }
            titleTextField.setDisable(false);
        }).start();
        if (titleTextField.getText().isEmpty()) return;
        booksOrAuthorsPagination.setPageFactory(param -> {
            currentPage = new ListView<>();
            Runnable getBooks = new BookDataRetrievalTask(BookBrowserController.this.getInputTitle(), param + 1);

            Runnable setBooks = () -> {

                Platform.runLater(() -> {
                    currentPage.getItems().addAll(bookModel.getLastSearchedBooks());
                    booksOrAuthorsPagination.setPageCount(bookModel.getSearchedPages());
                });
            };
            TaskRunner taskRunner = new TaskRunner(getBooks, setBooks);
            taskRunner.run();
            return currentPage;
        });
    }

    public void onReturn() {
        try {
            SceneController.startScene("userPane");
        } catch (IOException e) {
            AlertUtils.showAlert("Failure showing user pane");
        }
    }
}
