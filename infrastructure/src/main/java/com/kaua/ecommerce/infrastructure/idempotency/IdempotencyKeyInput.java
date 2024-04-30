package com.kaua.ecommerce.infrastructure.idempotency;

import java.io.Serializable;
import java.util.Map;

public class IdempotencyKeyInput implements Serializable {

    private final int statusCode;
    private final Object body;
    private Map<String, String> headers;

    public IdempotencyKeyInput(final int statusCode, final Object body, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Object getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "IdempotencyKeyInput{" +
                "statusCode=" + statusCode +
                ", body=" + body +
                ", headers=" + headers.size() +
                '}';
    }
}
