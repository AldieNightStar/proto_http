package haxidenti.httpproto;

public interface Server {
    void register(String path, Endpoint endpoint);

    void serve();

    void stop();
}
