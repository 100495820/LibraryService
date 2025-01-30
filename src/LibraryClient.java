import java.net.*;
import java.io.*;

public class LibraryClient {
    public static void main(String[] args) throws Exception {
        // Example 1: Get all books
        sendRequest("GET", baseUrl(), null);

        // Example 2: Get books by author
        sendRequest("GET", baseUrl() + "?author=Adam%20Smith", null);

        // Example 3: Get book by ISBN
        sendRequest("GET", baseUrl() + "?isbn=457", null);

        // Example 4: Add a new book
        String params = "isbn=457&author=Adam Smith&title=Harry Potter&total=10&available=8";
        sendRequest("POST", baseUrl(), params);
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