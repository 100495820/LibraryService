import jakarta.servlet.ServletContext;
import org.json.JSONObject;
import org.json.XML;
import org.json.JSONArray;
import java.beans.XMLEncoder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;


public class Library {
    private HashMap<String, Book> books; // HashMap to store books with ISBN as key
    private ServletContext sctx; // ServletContext to get the real path of the JSON file

    // Constructor to initialize the books HashMap
    public Library() {
        this.books = new HashMap<>();
        // Initialization will be done through init() after servlet context is set
    }

    // Method to initialize the library by loading books from the JSON file
    public void initialize() {
        if (this.sctx != null) {
            loadBooksFromFile();
        } else {
            throw new RuntimeException("ServletContext is null");
        }
    }

    // Method to load books from the JSON file
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

    // Method to get the map of books
    public HashMap<String, Book> getMap() {
        if (this.sctx == null) {
            return null;
        }
        return this.books;
    }

    // Method to set the servlet context and initialize the library
    public void setServletContext(ServletContext sctx) {
        this.sctx = sctx;
        initialize();
    }

    // Method to get the servlet context
    public ServletContext getServletContext() {
        return this.sctx;
    }

    // Method to convert an object to XML
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

    // Method to convert XML to JSON
    public String toJson(String xml) {
        try {
            JSONObject jsonObj = XML.toJSONObject(xml);
            return jsonObj.toString(3);
        } catch (Exception e) {}
        return null;
    }
}