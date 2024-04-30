package com.kaua.ecommerce.infrastructure.idempotency;

import java.io.Serializable;
import java.util.Map;

public class IdempotencyKeyOutput implements Serializable {

    private final int statusCode;
    private final String body;
    private Map<String, String> headers;

    public IdempotencyKeyOutput(final int statusCode, final String body, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "IdempotencyKeyOutput{" +
                "statusCode=" + statusCode +
                ", body='" + body + '\'' +
                ", headers=" + headers.size() +
                '}';
    }
}
