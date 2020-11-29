package models;

import models.entities.Book;
import updating.Updater;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class BookModel {
    private static BookModel instance;
    private List<Book> lastSearchedBooks;
    private int searchedPages;
    private List<Book> userBooks;
    private int userBooksPages;

    public static BookModel getInstance() {
        if (instance == null) instance = new BookModel();
        return instance;
    }

    public List<Book> getLastSearchedBooks() {
        return lastSearchedBooks;
    }

    public void setLastSearchedBooks(List<Book> lastSearchedBooks) {
        this.lastSearchedBooks = lastSearchedBooks;
        try {
            Updater.update();
        } catch (ClassNotFoundException | IOException | URISyntaxException ignored) {
        }
    }

    public int getSearchedPages() {
        return searchedPages;
    }

    public void setSearchedPages(int searchedPages) {
        this.searchedPages = searchedPages;
    }

    public int getUserBooksPages() {
        return userBooksPages;
    }

    public void setUserBooksPages(int userBooksPages) {
        this.userBooksPages = userBooksPages;
    }

    public List<Book> getUserBooks() {
        return userBooks;
    }

    public void setUserBooks(List<Book> userBooks) {
        this.userBooks = userBooks;
        try {
            Updater.update();
        } catch (ClassNotFoundException | IOException | URISyntaxException ignored) {
        }
    }

    private BookModel() {
        lastSearchedBooks = new ArrayList<>();
        userBooks = new ArrayList<>();
        searchedPages = 0;
        userBooksPages = 0;
    }

}
