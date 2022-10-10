package haxidenti.httpproto;

import java.lang.reflect.Method;

public class Proto {

    private final Coder coder;
    private final Logger logger;
    Server server;

    public Proto(Coder dataCoder, Server server, Logger logger) {
        this.server = server;
        this.coder = dataCoder;
        this.logger = logger;
    }

    public void register(String name, Object service) {
        for (Method method : service.getClass().getDeclaredMethods()) {
            if (method.getParameterTypes().length != 1) continue;
            Class<?> argType = method.getParameterTypes()[0];
            Class<?> retType = method.getReturnType();
            String path = "/" + name + "/" + method.getName();
            server.register(path, (req, res) -> {
                try {
                    String response = coder.toJson(
                            method.invoke(service, coder.toObject(req.body(), argType))
                    );
                    res.send(200, response);
                } catch (Exception e) {
                    res.send(500, e.getMessage());
                    logger.log("ERR: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            });
            logger.log("Registered path: " + path);
        }
    }

    public void start() {
        server.serve();
    }

    public void stop() {
        server.stop();
    }
}
