package dev.enche.web.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public class HttpRequest {
    @JsonIgnore
    private Map<String, String> requestLine;
    private Map<String, String> headers;
    private Map<String, String> pathParams;
    private Map<String, HttpQueryParam> queryParams;
    private Object body;

    public Map<String, String> getRequestLine() { return requestLine; }
    public void setRequestLine(Map<String, String> requestLine) { this.requestLine = requestLine; }

    public String getMethod() { return requestLine.get("Method"); }
    public void setMethod(String method) { this.requestLine.put("Method", method); }

    public String getUri() { return requestLine.get("Uri"); }
    public void getUri(String uri) { this.requestLine.put("Uri", uri); }

    public String getPath() { return requestLine.get("Path"); }
    public void getPath(String path) { this.requestLine.put("Path", path); }

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

    public HttpQueryParam getQueryParam(String key) { return queryParams.get(key); }
    public void setQueryParam(String key, HttpQueryParam value) { queryParams.put(key, value); }

    public Map<String, HttpQueryParam> getQueryParams() { return queryParams; }
    public void setQueryParams(Map<String, HttpQueryParam> queryParams) { this.queryParams = queryParams; }

    public Object getBody() { return body; }
    public void setBody(Object body) { this.body = body; }
}
