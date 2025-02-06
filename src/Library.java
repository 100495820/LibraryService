import jakarta.servlet.ServletContext;
import org.json.JSONObject;
import org.json.XML;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class Library {
    private HashMap<String, Book> books;
    private ServletContext sctx;

    public Library() {
        this.books = new HashMap<>();
        // Initialization will be done through init() after servlet context is set
    }

    public void initialize() {
        if (this.sctx != null) {
            loadBooksFromFile();
        } else {
            throw new RuntimeException("ServletContext is null");
        }
    }

    private void loadBooksFromFile() {
        String relativePath = "/WEB-INF/data/books.csv";
        String absolutePath = sctx.getRealPath(relativePath);

        try (BufferedReader br = new BufferedReader(new FileReader(absolutePath))) {
            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String isbn = parts[0].trim();
                    String author = parts[1].trim();
                    String title = parts[2].trim();
                    int totalCopies = Integer.parseInt(parts[3].trim());
                    int availableCopies = Integer.parseInt(parts[4].trim());

                    books.put(isbn, new Book(
                            isbn,
                            author,
                            title,
                            totalCopies,
                            availableCopies
                    ));
                }
            }
        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException("Error loading books from CSV: " + e.getMessage());
        }
    }

    public HashMap<String, Book> getMap() {
        if (this.sctx == null) {
            return null;
        }
        return this.books;
    }

    public void setServletContext(ServletContext sctx) {
        this.sctx = sctx;
        initialize();
    }

    public ServletContext getServletContext() {
        return this.sctx;
    }

    public String toXml(Object obj) {
        String xml = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            XMLEncoder encoder = new XMLEncoder(out);
            encoder.writeObject(obj);
            encoder.close();
            xml = out.toString();
        } catch (Exception e) {}
            return xml;


    }

    public String toJson(String xml) {
        try {
            JSONObject jsonObj = XML.toJSONObject(xml);
            return jsonObj.toString(3);
        } catch (Exception e) {

        }
        return null;
    }

    // Additional helper methods
    public List<Book> searchByAuthor(String author) {
        List<Book> results = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getAuthor().equalsIgnoreCase(author)) {
                results.add(book);
            }
        }
        return results;
    }

    public List<Book> searchByTitle(String title) {
        List<Book> results = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                results.add(book);
            }
        }
        return results;
    }
}