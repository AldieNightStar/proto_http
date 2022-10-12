package haxidenti.httpproto.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import haxidenti.httpproto.Endpoint;
import haxidenti.httpproto.Server;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class InternalServer implements Server, Closeable {

    HttpServer server;

    private int port;

    private Map<String, Endpoint> endpoints = new HashMap<>(32);

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private boolean isServing;

    public InternalServer(int port) {
        this.port = port;
    }

    @Override
    public void register(String path, Endpoint endpoint) {
        if (isServing) throw new RuntimeException("Server is already running. Need to stop it to make register work ok");
        endpoints.put(path, endpoint);
    }

    @Override
    public void serve() {
        try {
            if (isServing) return;
            isServing = true;
            server = HttpServer.create(new InetSocketAddress(port), 0);
            endpoints.forEach((path, endpoint) -> server.createContext(path, new Handler(endpoint)));
            server.createContext("/proto.js", exchange -> {
                try {
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Access-Control-Allow-Origin", "*");
                    headers.add("Content-Type", "application/json");
                    try (InputStream resourceAsStream = Server.class.getResourceAsStream( "proto.js")) {
                        byte[] data = resourceAsStream.readAllBytes();
                        exchange.sendResponseHeaders(200, data.length);
                        OutputStream out = exchange.getResponseBody();
                        out.write(data);
                        out.close();
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            });
            server.setExecutor(executor);
            server.start();
        } catch (Exception e) {
            isServing = false;
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        isServing = false;
        server.stop(1000);
    }

    @Override
    public void close() throws IOException {
        stop();
    }
}
