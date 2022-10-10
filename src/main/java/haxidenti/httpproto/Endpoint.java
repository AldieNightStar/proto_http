package haxidenti.httpproto;

import java.util.List;
import java.util.Map;

public interface Endpoint {
    interface Request {
        String body();
        Map<String, List<String>> headers();
        String uri();

        String method();
    }

    interface Response {
        void send(int status, String body);
    }

    void call(Request req, Response res);
}
