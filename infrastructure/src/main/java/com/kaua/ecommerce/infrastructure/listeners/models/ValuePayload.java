package com.kaua.ecommerce.infrastructure.listeners.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ValuePayload<T>(
        @JsonProperty("after") T after,
        @JsonProperty("before") T before,
        @JsonProperty("source") Source source,
        @JsonProperty("op") Operation operation
) {
}
