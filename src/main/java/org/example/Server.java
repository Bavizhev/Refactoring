package org.example;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int THREAD_POOL_SIZE = 64;
    private final Map<String, Map<String, Handler>> handlers = new HashMap<>();

    public void addHandler(String method, String path, Handler handler) {
        handlers.computeIfAbsent(method, k -> new HashMap<>()).put(path, handler);
    }

    public void listen(int port) {
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    threadPool.execute(new ConnectionHandler(socket));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    private class ConnectionHandler implements Runnable {
        private final Socket socket;

        public ConnectionHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    Request request = Request.parse(socket.getInputStream());
                    BufferedOutputStream responseStream = new BufferedOutputStream(socket.getOutputStream())
            ) {
                String method = request.getMethod();
                String path = request.getPath().split("\\?")[0]; // Extract path without query string
                Handler handler = handlers.getOrDefault(method, new HashMap<>()).get(path);
                if (handler != null) {
                    handler.handle(request, responseStream);
                } else {
                    // Default handler for 404 Not Found
                    responseStream.write((
                            "HTTP/1.1 404 Not Found\r\n" +
                                    "Content-Length: 0\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    responseStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}