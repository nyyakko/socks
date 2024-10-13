package dev.enche.web.core;

public class HttpQueryParam {

    final private String field;
    final private String operator;
    final private String value;

    public HttpQueryParam(String field, String operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public String getField() { return field; }
    public String getOperator() { return operator; }
    public String getValue() { return value; }

}
