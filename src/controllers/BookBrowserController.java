package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import models.BookModel;
import models.UserModel;
import models.entities.Book;
import tasks.BookDataRetrievalTask;
import utils.Resources;
import utils.fxUtils.AlertUtils;
import web.TaskRunner;

import java.io.IOException;
import java.util.ArrayList;


public class BookBrowserController extends Controller {

    @FXML
    protected Pagination booksOrAuthorsPagination;
    @FXML
    protected CheckBox authorsCheckBox;
    @FXML
    protected TextField firstNameTextField;
    @FXML
    protected TextField lastNameTextField;
    @FXML
    protected TextField titleTextField;
    protected ListView<Book> currentPage;
    @FXML
    protected BorderPane browserPane;
    @FXML
    protected Button searchButton;
    @FXML
    protected Button reserveButton;

    protected BookModel bookModel;
    protected UserModel userModel;

    public void initialize() {
        booksOrAuthorsPagination.setPageFactory(param -> {
            currentPage = new ListView<>();
            currentPage.getItems().addAll(new ArrayList<>());
            Platform.runLater(() -> booksOrAuthorsPagination.setPageCount(1));
            return currentPage;
        });
        bookModel = BookModel.getInstance();
        userModel = UserModel.getInstance();
        initializeManually();
        setKeyPresses(browserPane, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                onSearch();
            }
        });
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
                    } catch (Exception e) {
                        Platform.runLater(() -> AlertUtils.showAlert("Error occurred while reserving book"));
                        return;
                    }
                    if (response.equals("false")) {
                        Platform.runLater(() -> AlertUtils.showAlert("Book reservation failed"));
                    } else {
                        Platform.runLater(() -> AlertUtils.showAlert("Book reserved successfully"));
                    }

                };
                TaskRunner taskRunner = new TaskRunner(reserveBook, () -> {
                    userModel.updateUser();
                });
                taskRunner.run();
            }
        });
    }

    protected Book getSelectedBook() {
        return currentPage.getSelectionModel().getSelectedItem();
    }

    public void onBoxChecked() {
        boolean checked = authorsCheckBox.isSelected();
        lastNameTextField.setDisable(!checked);
        firstNameTextField.setDisable(!checked);
        titleTextField.setDisable(checked);
    }

    protected String getInputTitle() {
        return titleTextField.getText();
    }

    public void onSearch() {
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

   /* public void onReturn() {
        try {
            SceneController.startScene("userPane");
        } catch (IOException e) {
            AlertUtils.showAlert("Failure showing user pane");
        }
    }*/
}
