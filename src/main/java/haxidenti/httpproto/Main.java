package haxidenti.httpproto;

import haxidenti.httpproto.server.GsonCoder;
import haxidenti.httpproto.server.InternalLogger;
import haxidenti.httpproto.server.InternalServer;

import java.awt.*;

class Main {
    public static void main(String[] args) {
        Proto proto = new Proto(
                new GsonCoder(),
                new InternalServer(8080),
                new InternalLogger()
        );
        proto.register("api", new Main());
        proto.start();
    }

    public Point test1(Point req) {
        return new Point(req.x * 2, req.y * 2);
    }
}
