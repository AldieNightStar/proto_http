package haxidenti.httpproto;

public interface Coder {
    String toJson(Object o);

    <T> T toObject(String json, Class<T> type);
}
