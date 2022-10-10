# HTTP Proto

# Usage
```java

import haxidenti.httpproto.server.GsonCoder;
import haxidenti.httpproto.server.InternalLogger;
import haxidenti.httpproto.server.InternalServer;

import java.awt.*;

class Main {
    public static void main(String[] args) {
        // Create proto API
        Proto proto = new Proto(
                new GsonCoder(), // Uses google GSON (Need to add that dependency to make it work)
                new InternalServer(8080), // Internal server (You can use your own)
                new InternalLogger() // Standard logger (You can implement your own)
        );
        
        // Register current class as API with one method Main
        // It will serve as /api/test1 (POST)
        proto.register("api", new Main());
        
        // Start the server
        // proto.stop() - makes server stop working
        proto.start();
    }

    // Handler
    public Point test1(Point req) {
        return new Point(req.x * 2, req.y * 2);
    }
}

```