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
    private final double length;

    private ProductSize(
            final String id,
            final String size,
            final double weight,
            final double height,
            final double width,
            final double length
    ) {
        this.id = Objects.requireNonNull(id);
        this.size = Objects.requireNonNull(size);
        this.weight = weight;
        this.height = height;
        this.width = width;
        this.length = length;
    }

    public static ProductSize with(
            final String size,
            final double weight,
            final double height,
            final double width,
            final double length
    ) {
        return new ProductSize(IdUtils.generate(), size.toUpperCase(), weight, height, width, length);
    }

    public static ProductSize with(
            final String id,
            final String size,
            final double weight,
            final double height,
            final double width,
            final double length
    ) {
        return new ProductSize(id, size.toUpperCase(), weight, height, width, length);
    }

    public String getId() {
        return id;
    }

    public String getSize() {
        return size;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public double getLength() {
        return length;
    }
}
