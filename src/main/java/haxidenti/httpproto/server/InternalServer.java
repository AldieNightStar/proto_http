package haxidenti.httpproto.server;

import com.sun.net.httpserver.HttpServer;
import haxidenti.httpproto.Endpoint;
import haxidenti.httpproto.Server;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
