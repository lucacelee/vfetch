// Gracefully taken from https://www.geeksforgeeks.org/java/how-to-set-up-a-basic-http-server-in-java/

// Java Program to Set up a Basic HTTP Server
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

// Driver Class
public class WebServer {
    private HttpServer server;
    private int port;

    public WebServer(int port) {
        this.port = port;
    }

    public void setResponse(String html) {
        MyHandler.response = html;
    }

    public void create() {
        // Create an HttpServer instance

        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);

            // Create a context for a specific path and set the handler
            server.createContext("/", new MyHandler());

        } catch (IOException e) {
            System.out.println("Error starting the server: " + e.getMessage());
        }

    }

    public void start() {
        // Start the server
        server.setExecutor(null); // Use the default executor
        server.start();

        System.out.println("Server is running at http://localhost: " + port);
        // System.out.printf("RESPONSE:\n%s", MyHandler.response);
    }

    // Define a custom HttpHandler
    static class MyHandler implements HttpHandler {
        public static String response;

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Handle the request
            // String response = "Hello, this is a simple HTTP server response!";
            byte[] responseBytes = response.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }
}