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
            if (method.getAnnotation(ApiMethod.class) == null) continue;
            String apiName = method.getAnnotation(ApiMethod.class).value();
            Class<?> argType = method.getParameterTypes()[0];
            String path = "/" + name + "/" + (apiName.isBlank() ? method.getName() : apiName);
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
