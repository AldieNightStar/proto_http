package haxidenti.httpproto.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import haxidenti.httpproto.Endpoint;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Handler implements HttpHandler {

    private final Endpoint endpoint;

    public Handler(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        endpoint.call(new Endpoint.Request() {
            @Override
            public String body() {
                try {
                    return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Map<String, List<String>> headers() {
                return exchange.getRequestHeaders();
            }

            @Override
            public String uri() {
                return exchange.getRequestURI().toString();
            }

            @Override
            public String method() {
                return exchange.getRequestMethod();
            }
        }, (status, body) -> {
            try {
                byte[] data = body.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(status, data.length);
                OutputStream out = exchange.getResponseBody();
                out.write(data);
                out.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
