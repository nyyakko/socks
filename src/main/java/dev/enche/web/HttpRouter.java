package dev.enche.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.enche.web.core.HttpRequest;
import dev.enche.web.core.enums.HttpMethod;
import dev.enche.web.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class HttpRouter {

    private record RouterHandler(String uri, HttpMethod method, Class<?> type, Function<HttpRequest, ?> handler) {}

    final private Integer MAX_THREAD_POOL_SIZE = 16;
    final private ExecutorService executors = Executors.newFixedThreadPool(MAX_THREAD_POOL_SIZE);
    final private Map<String, RouterHandler> routers = new ConcurrentHashMap<>();

    public <R> void registerRouter(Class<R> routerType, String uri, HttpMethod method, Class<?> bodyType, BiFunction<R, HttpRequest, ?> callable) {
        routers.put(String.format("%s %s", method, uri), new RouterHandler(
            uri, method, bodyType, (request) -> {
                try {
                    return callable.apply(routerType.getDeclaredConstructor().newInstance(), request);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        ));
    }

    public <R> void registerRouter(Class<R> routerType, String uri, HttpMethod method, BiFunction<R, HttpRequest, ?> callable) {
        routers.put(String.format("%s %s", method, uri), new RouterHandler(
            uri, method, null, (request) -> {
                try {
                    return callable.apply(routerType.getDeclaredConstructor().newInstance(), request);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        ));
    }

    public void routeAll() {
        try (
            final var server = new ServerSocket(8000)
        ) {
            while (true) {
                final var client = server.accept();
                executors.execute(() -> route(client));
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private Optional<String> findHandlerKey(HttpRequest request) {
        return
            routers
                .keySet()
                .stream()
                .filter(key -> key.startsWith(request.getMethod()))
                .map(key -> key.split(request.getMethod())[1].strip())
                .filter(key -> {
                    final var pattern = key.split("/");
                    final var uri = request.getUri().split("/");
                    if (pattern.length != uri.length) return false;
                    for (var index = 0; index != uri.length; index += 1) {
                        final var isSame = pattern[index].equals(uri[index]);
                        final var isWildcard = pattern[index].startsWith("{") && pattern[index].endsWith("}");
                        if (!(isSame || isWildcard)) {
                            return false;
                        }
                    }
                    return true;
                })
                .map(key -> request.getMethod() + " " + key)
                .findFirst();
    }

    private void handleHttpPost(HttpRequest request, PrintWriter writer, BufferedReader reader) {
        final var header = new StringBuilder();
        header.append("HTTP/1.1 200 OK\n");
        header.append("Content-Length: 0\n");
        header.append("Access-Control-Allow-Origin: *\n");
        header.append("\n");

        writer.println(header);

        findHandlerKey(request)
            .map(routerKey -> {
                final var router = routers.get(routerKey);
                final var mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                if (routerKey.contains("{") && routerKey.contains("}")) {
                    request.setPathParams(Utils.parsePathParams(request.getUri().split("/"), router.uri().split("/")));
                }
                try {
                    request.setBody(mapper.readValue(reader.readLine(), router.type()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                router.handler().apply(request);
                return routerKey;
            })
            .orElseThrow(() ->
                new RuntimeException("UNMAPED RESOURCE ENDPOINT")
            );
    }

    private void handleHttpGet(HttpRequest request, PrintWriter writer) {
        final var header = new StringBuilder();
        header.append("HTTP/1.1 200 OK\n");
        header.append("Access-Control-Allow-Origin: *\n");
        header.append("Content-Type: application/json\n");

        findHandlerKey(request)
            .map(routerKey -> {
                final var router = routers.get(routerKey);
                if (routerKey.contains("{") && routerKey.contains("}")) {
                    request.setPathParams(Utils.parsePathParams(request.getUri().split("/"), router.uri().split("/")));
                }
                try {
                    final var result = router.handler().apply(request);
                    if (result != null) {
                        final var response = Utils.objectAsJson(result);
                        header.append("Content-Length: ").append(response.length()).append("\n");
                        header.append("\n");
                        header.append(response);
                    } else {
                        header.append("Content-Length: 0\n");
                        header.append("\n");
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                return routerKey;
            })
            .orElseThrow(() ->
                new RuntimeException("UNMAPPED RESOURCE ENDPOINT")
            );

        writer.println(header);
    }

    private void handleHttpDelete(HttpRequest request, PrintWriter writer) {
        final var header = new StringBuilder();
        header.append("HTTP/1.1 200 OK\n");
        header.append("Content-Length: 0\n");
        header.append("Access-Control-Allow-Origin: *\n");
        header.append("\n");

        findHandlerKey(request)
            .map(routerKey -> {
                final var router = routers.get(routerKey);
                if (routerKey.contains("{") && routerKey.contains("}")) {
                    request.setPathParams(Utils.parsePathParams(request.getUri().split("/"), router.uri().split("/")));
                }
                router.handler().apply(request);
                return routerKey;
            })
            .orElseThrow(() ->
                new RuntimeException("UNMAPPED RESOURCE ENDPOINT")
            );

        writer.println(header);
    }

    private static void handleHttpException(Exception exception, PrintWriter writer) {
        final var header = new StringBuilder();
        header.append("HTTP/1.1 500 Internal Server Error\n");
        header.append("Access-Control-Allow-Origin: *\n");
        final var traceElements = exception.getStackTrace();
        final var response =
            "<!DOCTYPE html>\n" +
            "<html>\n" +
                "<head>\n" +
                    "<title>An error occurred</title>\n" +
                "</head>\n" +
                "<style>\n" +
                    "p {\n" +
                        "font-size: 20px\n" +
                    "}\n" +
                    ".trace-line-a {\n" +
                        "background: rgb(255, 0, 0, 0.05);\n" +
                        "padding-left: 50px;\n" +
                    "}\n" +
                    ".trace-line-b {\n" +
                        "background: rgb(255, 0, 0, 0.15);\n" +
                        "padding-left: 50px;\n" +
                    "}\n" +
                "</style>\n" +
                "<root>\n" +
                    "<body>\n" +
                        "<div>\n" +
                            "<p>An error occurred: " + exception.getMessage() + "</p>\n" +
                                LongStream.range(0, traceElements.length)
                                    .mapToObj(index -> {
                                        final var traceElement = traceElements[(int)index];
                                        final var isUserCode = !traceElement.getClassName().startsWith("java");
                                        return "<p class=\"trace-line-" + (isUserCode ? "b" :"a") +"\">at " + traceElement + "</p>\n";
                                    })
                                    .collect(Collectors.joining()) +
                        "<div>\n" +
                    "<body>\n" +
                "<root>\n" +
            "</html>";
        header.append("Content-Length: ").append(response.length()).append("\n");
        header.append("\n");
        header.append(response);
        writer.println(header);
    }

    private void route(Socket client) {
        try (final var writer = new PrintWriter(client.getOutputStream(), true);
             final var reader = new BufferedReader(new InputStreamReader(client.getInputStream()))
        ) {
            final var request = new HttpRequest();
            request.setRequestLine(Utils.parseRequestLine(reader));
            request.setHeaders(Utils.parseRequestHeaders(reader));

            try {
                switch (request.getMethod()) {
                    case "DELETE" -> handleHttpDelete(request, writer);
                    case "GET" -> handleHttpGet(request, writer);
                    case "POST" -> handleHttpPost(request, writer, reader);
                    default -> throw new RuntimeException("UNKNOWN REQUEST METHOD");
                }
            } catch (Exception exception) {
                handleHttpException(exception, writer);
            }

            System.out.printf(Thread.currentThread() + "[" + LocalDateTime.now() + "]: Handled request: %s%n%n", Utils.objectAsPrettyJson(request));
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
}
