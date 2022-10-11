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
            if (method.getAnnotation(ApiMethod.class) == null) continue;
            String apiName = method.getAnnotation(ApiMethod.class).value();
            String path = "/" + name + "/" + (apiName.isBlank() ? method.getName() : apiName);
            if (method.getParameterTypes().length == 1) {
                Class<?> argType = method.getParameterTypes()[0];
                registerForArgType(argType, path, method, service);

            } else if (method.getParameterTypes().length == 0) {
                registerForNoArg(path, method, service);
            } else {
                logger.log("WARN: Can't register with more than 1 param. Use objects instead");
                continue;
            }
            logger.log("Registered path: " + path);
        }
    }

    void registerForArgType(Class<?> argType, String path, Method method, Object service) {
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
    }

    void registerForNoArg(String path, Method method, Object service) {
        server.register(path, (req, res) -> {
            try {
                String response = coder.toJson(method.invoke(service));
                res.send(200, response);
            } catch (Exception e) {
                res.send(500, e.getMessage());
                logger.log("ERR: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    public void start() {
        server.serve();
    }

    public void stop() {
        server.stop();
    }
}
