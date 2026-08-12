import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Standalone Zero-Dependency Java Server for GitHub Profile Finder.
 * Runs directly with: java App.java
 */
public class App {

    private static final int PORT = 8080;
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // API Endpoint for GitHub User Proxy
        server.createContext("/api/github/user/", new GithubApiHandler());

        // Static Files Handler
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null); // default executor
        System.out.println("=================================================");
        System.out.println("🚀 Java GitHub Profile Finder Server Running!");
        System.out.println("👉 Open your browser at: http://localhost:" + PORT);
        System.out.println("=================================================");
        server.start();
    }

    // Handler for Proxying GitHub REST API
    static class GithubApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String username = path.substring("/api/github/user/".length()).trim();

            if (username.isEmpty()) {
                sendJsonResponse(exchange, 400, "{\"error\": \"Username is required\"}");
                return;
            }

            try {
                // Fetch user data from GitHub REST API
                String userUrl = "https://api.github.com/users/" + username;
                HttpRequest userRequest = HttpRequest.newBuilder()
                        .uri(URI.create(userUrl))
                        .header("User-Agent", "Java-Github-Profile-Finder")
                        .header("Accept", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> userResponse = httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());

                if (userResponse.statusCode() != 200) {
                    sendJsonResponse(exchange, userResponse.statusCode(), userResponse.body());
                    return;
                }

                // Fetch top 4 recent repositories
                String reposUrl = "https://api.github.com/users/" + username + "/repos?sort=updated&per_page=4";
                HttpRequest reposRequest = HttpRequest.newBuilder()
                        .uri(URI.create(reposUrl))
                        .header("User-Agent", "Java-Github-Profile-Finder")
                        .header("Accept", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> reposResponse = httpClient.send(reposRequest, HttpResponse.BodyHandlers.ofString());

                String reposJson = (reposResponse.statusCode() == 200) ? reposResponse.body() : "[]";

                // Combine user profile and repos into single JSON response
                String combinedJson = String.format("{\"profile\": %s, \"repositories\": %s}", userResponse.body(), reposJson);
                sendJsonResponse(exchange, 200, combinedJson);

            } catch (Exception e) {
                sendJsonResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
            }
        }

        private void sendJsonResponse(HttpExchange exchange, int statusCode, String responseJson) throws IOException {
            byte[] responseBytes = responseJson.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }

    // Handler for Static Files (index.html, style.css, script.js)
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            if (requestPath.equals("/")) {
                requestPath = "/index.html";
            }

            Path filePath = Paths.get("src/main/resources/static" + requestPath);
            if (!Files.exists(filePath)) {
                filePath = Paths.get("." + requestPath);
            }

            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                String error404 = "404 File Not Found";
                exchange.sendResponseHeaders(404, error404.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(error404.getBytes(StandardCharsets.UTF_8));
                }
                return;
            }

            String contentType = getContentType(filePath.toString());
            byte[] fileBytes = Files.readAllBytes(filePath);

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, fileBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }
        }

        private String getContentType(String path) {
            if (path.endsWith(".html")) return "text/html; charset=UTF-8";
            if (path.endsWith(".css")) return "text/css; charset=UTF-8";
            if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (path.endsWith(".png")) return "image/png";
            if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
            return "text/plain";
        }
    }
}
