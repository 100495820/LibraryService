// LibraryServlet.java
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;

public class LibraryServlet extends HttpServlet {
    static final long serialVersionUID = 1L;
    private Library library;

    public static String base_url = "http://localhost:8080/library";

    @Override
    public void init() {
        synchronized(this) {
            // Initialize the library
            this.library = new Library();
            this.library.setServletContext(this.getServletContext());
        }
    }

    // Send response to the client
    private void sendResponse(HttpServletResponse response, String payload) {
        try {
            OutputStream out = response.getOutputStream();
            out.write(payload.getBytes());
            out.flush();
        } catch(Exception e) {
            // Handle exceptions
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        boolean json = false;
        String accept = request.getHeader("accept");
        if (accept != null && accept.contains("json")) json = true;

        HashMap<String, Book> map = this.library.getMap();
        String isbn = request.getParameter("isbn");
        String author = request.getParameter("author");
        String title = request.getParameter("title");

        Object result = null;
        synchronized(map) {
            if (isbn != null) {
                // Get book by ISBN
                result = map.get(isbn);
                if (result == null) result = "Book with ISBN " + isbn + " not found.";
            } else if (author != null) {
                // Get books by author
                List<Book> booksByAuthor = new ArrayList<>();
                for (Book book : map.values()) {
                    if (author.equalsIgnoreCase(book.getAuthor())) booksByAuthor.add(book);
                }
                result = booksByAuthor;
            } else if (title != null) {
                // Get books by title
                List<Book> booksByTitle = new ArrayList<>();
                for (Book book : map.values()) {
                    if (title.equalsIgnoreCase(book.getTitle())) booksByTitle.add(book);
                }
                result = booksByTitle;
            } else {
                // Get all books
                result = map.values().toArray();
            }
        }
        if (json) {
            // Send JSON response
            sendResponse(response, library.toJson(library.toXml(result)));
        } else {
            // Send XML response
            sendResponse(response, library.toXml(result));
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String isbn = request.getParameter("isbn");
        String author = request.getParameter("author");
        String title = request.getParameter("title");
        String total = request.getParameter("total");
        String available = request.getParameter("available");

        if (isbn == null || author == null || title == null || total == null || available == null)
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_BAD_REQUEST));

        int totalCopies = 0;
        try {
            totalCopies = Integer.parseInt(total);
        } catch (NumberFormatException e) {
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_BAD_REQUEST));
        }
        int availableCopies = 0;
        try {
            availableCopies = Integer.parseInt(available);
        } catch (NumberFormatException e) {
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_BAD_REQUEST));
        }

        try {
            // Create a new book and add it to the library
            Book book = new Book(isbn, author, title, totalCopies, availableCopies);
            synchronized(library.getMap()) {
                library.getMap().put(isbn, book);
            }
            sendResponse(response, "Book " + isbn + " added.");
        } catch (NumberFormatException e) {
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_BAD_REQUEST));
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        String isbn = null;
        String available = null;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            String data = br.readLine();
            String[] args = data.split("&");
            for (String arg : args) {
                String[] pair = arg.split("=", 2);
                if (pair[0].equals("isbn")) isbn = pair[1];
                else if (pair[0].equals("available")) available = pair[1];
            }
        } catch (Exception e) {
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
        }

        if (isbn == null || available == null)
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_BAD_REQUEST));

        HashMap<String, Book> map = library.getMap();
        Book book = map.get(isbn);
        if (book == null) {
            sendResponse(response, "Book not found.");
            return;
        }

        try {
            int availableCopies = Integer.parseInt(available);
            synchronized(book) {
                // Update available copies
                book.setAvailableCopies(availableCopies);
            }
            sendResponse(response, "Available copies updated for ISBN " + isbn);
        } catch (NumberFormatException e) {
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_BAD_REQUEST));
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        String isbn = request.getParameter("isbn");
        if (isbn == null) throw new RuntimeException(Integer.toString(HttpServletResponse.SC_BAD_REQUEST));

        synchronized(library.getMap()) {
            // Remove the book from the library
            library.getMap().remove(isbn);
        }
        sendResponse(response, "Book " + isbn + " deleted.");
    }

    // Disallow other HTTP methods
    @Override
    public void doTrace(HttpServletRequest req, HttpServletResponse res) { /* ... */ }
    @Override
    public void doHead(HttpServletRequest req, HttpServletResponse res) { /* ... */ }
    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse res) { /* ... */ }
}