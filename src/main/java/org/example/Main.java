package org.example;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        final var server = new Server();

        server.addHandler("GET", "/messages", (request, responseStream) -> {
            // Handler code for GET /messages
            String response = "Handler for GET /messages";
            sendResponse(response, responseStream);
        });

        server.addHandler("POST", "/messages", (request, responseStream) -> {
            // Handler code for POST /messages
            String response = "Handler for POST /messages";
            sendResponse(response, responseStream);
        });

        server.addHandler("GET", "/time", (request, responseStream) -> {
            // Handler code for GET /time
            String response = "Current time is: " + LocalDateTime.now();
            sendResponse(response, responseStream);
        });

        server.listen(9999);
    }

    private static void sendResponse(String response, BufferedOutputStream responseStream) throws IOException {
        byte[] responseData = response.getBytes();
        responseStream.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "Content-Length: " + responseData.length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        responseStream.write(responseData);
        responseStream.flush();
    }
}