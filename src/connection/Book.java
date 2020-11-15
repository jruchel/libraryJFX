package connection;

public class Book {
    private String author;
    private int id;
    private String title;

    public Book(String title, String author, int id) {
        this.author = author;
        this.id = id;
        this.title = title;
    }

    public Book(String title, String author) {
        this(title, author, 0);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return String.format("%s by %s", title, author);
    }
}
