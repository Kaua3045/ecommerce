package com.kaua.ecommerce.infrastructure.idempotency;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class IdempotencyKeyBodyTest implements Serializable {

    @JsonProperty("id")
    private String id;

    public IdempotencyKeyBodyTest() {
    }

    public IdempotencyKeyBodyTest(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
