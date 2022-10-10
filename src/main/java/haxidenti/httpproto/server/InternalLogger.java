package haxidenti.httpproto.server;

import haxidenti.httpproto.Logger;

import java.time.LocalDateTime;

public class InternalLogger implements Logger {
    @Override
    public void log(String message) {
        String time = LocalDateTime.now().toString();
        System.out.println(time + " :: " + message);
    }
}
