package tasks;

import models.BookModel;
import models.entities.Book;
import utils.parsing.JsonReader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookDataRetrievalTask extends Task {

    private int page;
    private String title;
    private String firstName;
    private String lastName;
    private boolean byAuthors;

    public BookDataRetrievalTask(String title, int page) {
        super();
        this.title = title;
        this.page = page;
        this.byAuthors = false;
    }

    public BookDataRetrievalTask(String title, String firstName, String lastName, int page) {
        super();
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.page = page;
        this.byAuthors = true;
    }

    public BookDataRetrievalTask(String title, String firstName, String lastName) {
        this(title, firstName, lastName, 1);
    }

    public BookDataRetrievalTask(String title) {
        this(title, 1);
    }

    private String getBooksData(String data) {
        Pattern pattern = Pattern.compile(".*content\\\"\\:\\[(.+)\\]\\,\\\"pageable.+");
        Matcher matcher = pattern.matcher(data);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return "";
    }

    private String[] getBookStrings(String data) {
        return JsonReader.readFromArray(data);
    }

    private Book getBookFromString(String data) {
        int id = Integer.parseInt(JsonReader.readFromJson("id", data));
        String title = JsonReader.readFromJson("title", data);
        String author = "";
        return new Book(title, author, id);
    }

    private List<Book> getBooks(String data) {
        String[] booksStrings = getBookStrings(getBooksData(data));
        List<Book> bookList = new ArrayList<>();
        for (String s : booksStrings) {
            bookList.add(getBookFromString(s));
        }
        return bookList;
    }

    private String getAuthorName(String data) {
        return JsonReader.readFromJson("name", data);
    }

    @Override
    public void run() {
        String booksData = "";
        String authorData = "";
        int searchPages = 0;
        List<Book> bookList = new ArrayList<>();
        try {
            booksData = requests.sendRequest(String.format("%s/books/search?title=%s&page=%d", appURL, title, page).replaceAll(" ", "%20"), "GET");
            searchPages = Integer.parseInt(JsonReader.readFromJson("totalPages", booksData));
            bookList.addAll(getBooks(booksData));
            for (Book b : bookList) {
                authorData = requests.sendRequest(String.format("%s/authors/author/book/%d", appURL, b.getId()), "GET");
                b.setAuthor(getAuthorName(authorData));
            }
        } catch (Exception e) {
            bookList = new ArrayList<>();
        }
        BookModel.getInstance().setLastSearchedBooks(bookList);
        BookModel.getInstance().setSearchedPages(searchPages);
    }
}
