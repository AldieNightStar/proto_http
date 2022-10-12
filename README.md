# HTTP Proto

# Import
[![](https://jitpack.io/v/AldieNightStar/proto_http.svg)](https://jitpack.io/#AldieNightStar/proto_http)

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
    @ApiMethod("test1") // Will serve as "api/test1"
    public Point test1(Point req) {
        return new Point(req.x * 2, req.y * 2);
    }
}
```

# Usage from JavaScript
```javascript
// Define host
let host = "http://localhost:8080";

// Import script API from server
document.body.appendChild((() => {
        let s = document.createElement("script");
        s.src = host + "/proto.js";
        return s;
})());

// Define API from imported script and defined host
let proto = new Proto(host);

// Call method on remote
await proto.call("api/abc", {x:10, y:20}); 
```