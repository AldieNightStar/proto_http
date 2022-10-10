package haxidenti.httpproto.server;

import com.sun.net.httpserver.HttpServer;
import haxidenti.httpproto.Endpoint;
import haxidenti.httpproto.Server;

import java.net.InetSocketAddress;

public class InternalServer implements Server {

    HttpServer server;

    private int port;

    private boolean isServing;

    public InternalServer(int port) {
        this.port = port;
    }

    @Override
    public void register(String path, Endpoint endpoint) {
        server.createContext(path, new Handler(endpoint));
    }

    @Override
    public void serve() {
        try {
            if (isServing) return;
            isServing = true;
            server = HttpServer.create(new InetSocketAddress(port), 0);
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
}
