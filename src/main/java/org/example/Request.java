package org.example;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request implements AutoCloseable {
    private final String method;
    private final String path;
    private final Map<String, String> headers = new HashMap<>();
    private final String queryString;
    private final List<NameValuePair> queryParams;

    private Request(String method, String path, String queryString) {
        this.method = method;
        this.path = path;
        this.queryString = queryString;
        this.queryParams = parseQueryParams(queryString);
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
            String[] pathAndQuery = parts[1].split("\\?");
            String path = pathAndQuery[0];
            String queryString = (pathAndQuery.length > 1) ? pathAndQuery[1] : "";

            // Read headers
            Map<String, String> headers = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                String[] headerParts = line.split(": ", 2);
                if (headerParts.length == 2) {
                    String headerName = headerParts[0];
                    String headerValue = headerParts[1];
                    headers.put(headerName, headerValue);
                }
            }

            return new Request(method, path, queryString);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParam(String name) {
        for (NameValuePair param : queryParams) {
            if (param.getName().equals(name)) {
                return param.getValue();
            }
        }
        return null;
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    @Override
    public void close() throws IOException {
        // No resources to close
    }

    private List<NameValuePair> parseQueryParams(String queryString) {
        return URLEncodedUtils.parse(queryString, StandardCharsets.UTF_8);
    }
}
