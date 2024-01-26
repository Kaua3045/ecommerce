package com.kaua.ecommerce.infrastructure.product.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateProductInputAttributes(
        @JsonProperty("color_name") String colorName,
        @JsonProperty("size_name") String sizeName,
        @JsonProperty("weight") double weight,
        @JsonProperty("height") double height,
        @JsonProperty("width") double width,
        @JsonProperty("depth") double depth
) {
}
