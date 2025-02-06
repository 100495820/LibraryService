public class Book {
    static final long serialVersionUID = 1L;
    private String isbn; // Unique identifier (cannot be changed)
    private String author;
    private String title;
    private int totalCopies;
    private int availableCopies;
    private String link;
    private String modifiable;
    private String modify_mode;

    // Default constructor for XMLEncoder
    public Book() {
        this.isbn = null;
        this.author = null;
        this.title = null;
        this.totalCopies = -1;
        this.availableCopies = -1;
        this.link = null;
        this.modify_mode = null;
        this.modifiable = null;
    }

    // Constructor with parameters
    public Book(String isbn, String author, String title, int totalCopies, int availableCopies) {
        this.isbn = isbn;
        this.author = author;
        this.title = title;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.link = LibraryServlet.base_url + "?isbn=" + this.isbn;
        this.modify_mode = "application/x-www-form-urlencoded";
        this.modifiable = "availableCopies (identified by ISBN)";
    }

    // Getters and Setters
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
        this.link = LibraryServlet.base_url + "?isbn=" + this.isbn;
    }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
    public String getLink() { return link; }
    public String getModifiable() { return modifiable; }
    public void setModifiable(String modifiable) { this.modifiable = modifiable; }
    public String getModify_mode() { return modify_mode; }
    public void setModify_mode(String modify_mode) { this.modify_mode = modify_mode; }
}