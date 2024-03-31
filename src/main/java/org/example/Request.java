package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Request implements AutoCloseable {
    private final String method;
    private final String path;
    private static final Map<String, String> headers = new HashMap<>();

    private Request(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public static Request parse(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            // Read request line
            String requestLine = reader.readLine();
            String[] parts = requestLine.split(" ");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid request line: " + requestLine);
            }
            String method = parts[0];
            String path = parts[1];

            // Read headers
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                String[] headerParts = line.split(": ");
                if (headerParts.length == 2) {
                    String headerName = headerParts[0];
                    String headerValue = headerParts[1];
                    headers.put(headerName, headerValue);
                }
            }

            return new Request(method, path);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public void close() throws IOException {
        // No resources to close
    }
}
