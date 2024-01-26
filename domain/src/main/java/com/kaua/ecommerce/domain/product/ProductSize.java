package com.kaua.ecommerce.domain.product;

import com.kaua.ecommerce.domain.ValueObject;
import com.kaua.ecommerce.domain.utils.IdUtils;

import java.util.Objects;

public class ProductSize extends ValueObject {

    private final String id;
    private final String size;
    private final double weight;
    private final double height;
    private final double width;
    private final double depth;

    private ProductSize(
            final String id,
            final String size,
            final double weight,
            final double height,
            final double width,
            final double depth
    ) {
        this.id = Objects.requireNonNull(id);
        this.size = Objects.requireNonNull(size);
        this.weight = weight;
        this.height = height;
        this.width = width;
        this.depth = depth;
    }

    public static ProductSize with(
            final String size,
            final double weight,
            final double height,
            final double width,
            final double depth
    ) {
        return new ProductSize(IdUtils.generate(), size.toUpperCase(), weight, height, width, depth);
    }

    public static ProductSize with(
            final String id,
            final String size,
            final double weight,
            final double height,
            final double width,
            final double depth
    ) {
        return new ProductSize(id, size.toUpperCase(), weight, height, width, depth);
    }

    public String id() {
        return id;
    }

    public String size() {
        return size;
    }

    public double weight() {
        return weight;
    }

    public double height() {
        return height;
    }

    public double width() {
        return width;
    }

    public double depth() {
        return depth;
    }
}
