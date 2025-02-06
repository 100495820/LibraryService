import jakarta.servlet.ServletContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.json.JSONArray;
import java.beans.XMLEncoder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        try {
            // Read JSON file
            String path = sctx.getRealPath("/WEB-INF/data/books.json");
            String content = new String(Files.readAllBytes(Paths.get(path)));

            // Parse JSON array
            JSONArray jsonArray = new JSONArray(content);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String isbn = jsonObject.getString("isbn");
                String author = jsonObject.getString("author");
                String title = jsonObject.getString("title");
                int totalCopies = jsonObject.getInt("totalCopies");
                int availableCopies = jsonObject.getInt("availableCopies");

                // Add to HashMap
                books.put(isbn, new Book(isbn, author, title, totalCopies, availableCopies));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading books from JSON: " + e.getMessage());
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