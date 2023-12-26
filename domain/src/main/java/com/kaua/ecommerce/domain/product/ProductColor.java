package com.kaua.ecommerce.domain.product;

import com.kaua.ecommerce.domain.ValueObject;
import com.kaua.ecommerce.domain.utils.IdUtils;

import java.util.Objects;

public class ProductColor extends ValueObject {

    private final String id;
    private final String color;

    private ProductColor(final String id, final String color) {
        this.id = Objects.requireNonNull(id);
        this.color = Objects.requireNonNull(color);
    }

    public static ProductColor with(final String color) {
        return new ProductColor(IdUtils.generate(), color);
    }

    public static ProductColor with(final String id, final String color) {
        return new ProductColor(id, color);
    }

    public String id() {
        return id;
    }

    public String color() {
        return color;
    }
}
