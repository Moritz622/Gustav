package gustav.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.FileInputStream;
import java.io.InputStream;

public class StaticFileHandler implements HttpHandler {

    private final String rootDirectory;

    public StaticFileHandler(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();

        if ("GET".equalsIgnoreCase(method)) {
            handleGetRequest(exchange);
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        String uriPath = exchange.getRequestURI().getPath();
        
        if (uriPath.equals("/")) {
            uriPath = "/index.html";
        }
        
        File file = new File(rootDirectory + uriPath);
        
        if (!file.exists() || file.isDirectory()) {
            byte[] response = "404 (Not Found)\n".getBytes();
            exchange.sendResponseHeaders(404, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
            return;
        }
        
        String mimeType = Files.probeContentType(file.toPath());
        
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        
        exchange.getResponseHeaders().set("Content-Type", mimeType);
        exchange.getResponseHeaders().set("Cache-Control", "public, max-age=3600");
        exchange.sendResponseHeaders(200, file.length());
        
        try (InputStream is = new FileInputStream(file);
             OutputStream os = exchange.getResponseBody()) {
            is.transferTo(os);
        }
    }
}