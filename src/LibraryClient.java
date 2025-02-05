import java.net.*;
import java.io.*;

public class LibraryClient {
    public static void main(String[] args) throws Exception {
        // Example 1: Get all books
        System.out.println("Example 1: Get all books");
        sendRequest("GET", baseUrl(), null);
        System.out.println("\n\n");
        System.out.println("Example 2: Get books by author");
        // Example 2: Get books by author
        sendRequest("GET", baseUrl() + "?author=Adam%20Smith", null);
        System.out.println("\n\n");
        System.out.println("Example 3: Get books by ISBN");
        // Example 3: Get book by ISBN
        sendRequest("GET", baseUrl() + "?isbn=978-0316769174", null);
        System.out.println("\n\n");
        System.out.println("Example 4: Get book by title");
        // Example 4: Get book by title
        sendRequest("GET", baseUrl() + "?title=The%20Wealth%20of%20Nations", null);
        System.out.println("\n\n");
        System.out.println("Example 5: Add a new book");
        // Example 5: Add a new book
        String params = "isbn=457&author=Adam Smith&title=Harry Potter&total=10&available=8";
        sendRequest("POST", baseUrl(), params);
        System.out.println("\n\n");
        System.out.println("Example 6: Update a book");
        // Example 6: Update a book
        params = "isbn=457&author=Adam Smith&title=Harry Potter&total=10&available=9";
        sendRequest("PUT", baseUrl(), params);
        System.out.println("\n\n");
        System.out.println("Example 7: Delete a book");
        // Example 7: Delete a book
        sendRequest("DELETE", baseUrl() + "?isbn=457", null);

    }

    private static String baseUrl() { return "http://localhost:8080/library"; }

    private static void sendRequest(String method, String urlString, String params) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);

        if (method.equals("POST") || method.equals("PUT")) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(params.getBytes());
                os.flush();
            }
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) System.out.println(line);
        }
    }
}