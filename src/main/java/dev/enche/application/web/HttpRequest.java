package dev.enche.application.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HttpRequest {

    String method = "";
    String uri = "";
    String version = "";
    Map<String, String> headers = new HashMap<>();
    Map<String, String> pathParams = new HashMap<>();
    Map<String, String> queryParams = new HashMap<>();
    Object body;

    public HttpRequest(BufferedReader reader) throws IOException {
        processRequestLine(reader);
        processRequestHeaders(reader);
    }

    private void processRequestLine(BufferedReader reader) throws IOException {
        final var regex = "^(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS|TRACE|CONNECT) (\\S+) (\\S+)";
        final var pattern = Pattern.compile(regex);
        final var line = reader.readLine();
        final var matcher = pattern.matcher(line);
        if (matcher.find()) {
            this.method = matcher.group(1);
            this.uri = matcher.group(2);
            this.version = matcher.group(3);
        }
    }

    private void processRequestHeaders(BufferedReader reader) throws IOException {
        var line = "";
        final var pattern = Pattern.compile("^(.*?):\\S*(.*)$");
        for (line = reader.readLine(); line != null && !line.isEmpty(); line = reader.readLine()) {
            final var matcher = pattern.matcher(line);
            if (matcher.find()) {
                this.headers.put(matcher.group(1), matcher.group(2).strip());
            }
        }
    }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getHeader(String key) { return headers.get(key); }
    public void setHeader(String key, String value) { headers.put(key, value); }

    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }

    public String getPathParam(String key) { return pathParams.get(key); }
    public void setPathParam(String key, String value) { pathParams.put(key, value); }

    public Map<String, String> getPathParams() { return pathParams; }
    public void setPathParams(Map<String, String> pathParams) {this.pathParams = pathParams; }

    public String getQueryParam(String key) { return queryParams.get(key); }
    public void setQueryParam(String key, String value) { queryParams.put(key, value); }

    public Map<String, String> getQueryParams() { return queryParams; }
    public void setQueryParams(Map<String, String> queryParams) { this.queryParams = queryParams; }

    public Object getBody() { return body; }
    public void setBody(Object body) { this.body = body; }

}
