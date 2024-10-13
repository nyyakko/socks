package dev.enche.application.web;

import dev.enche.application.web.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HttpRequest {
    final private Map<String, String> requestLine;
    private Map<String, String> headers;
    private Map<String, String> pathParams = new HashMap<>();
    private Map<String, String> queryParams = new HashMap<>();
    private Object body;

    public HttpRequest(BufferedReader reader) throws IOException {
        this.requestLine = Utils.parseRequestLine(reader);
        this.headers = Utils.parseRequestHeaders(reader);
    }

    public String getMethod() { return requestLine.get("Method"); }
    public void setMethod(String method) { this.requestLine.put("Method", method); }

    public String getUri() { return requestLine.get("Path"); }
    public void setUri(String uri) { this.requestLine.put("Path", uri); }

    public String getVersion() { return requestLine.get("Version"); }
    public void setVersion(String version) { this.requestLine.put("Version", version); }

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
