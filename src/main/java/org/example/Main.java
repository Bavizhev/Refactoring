package org.example;

public class Main {
    public static void main(String[] args) {
        final var server = new Server();

        server.addHandler("GET", "/messages", (request, responseStream) -> {
            // Handler code for GET /messages
        });

        server.addHandler("POST", "/messages", (request, responseStream) -> {
            // Handler code for POST /messages
        });

        server.listen(9999);
    }
}