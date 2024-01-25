package com.kaua.ecommerce.domain.product;

import com.kaua.ecommerce.domain.ValueObject;
import com.kaua.ecommerce.domain.utils.IdUtils;

import java.util.Objects;

public class ProductImage extends ValueObject {

    private final String id;
    private final String name;
    private final String location;
    private final String url;

    private ProductImage(
            final String id,
            final String name,
            final String location,
            final String url
    ) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.location = Objects.requireNonNull(location);
        this.url = Objects.requireNonNull(url);
    }

    public static ProductImage with(
            final String name,
            final String location,
            final String url
    ) {
        return new ProductImage(IdUtils.generateWithoutDash(), name, location, url);
    }

    public static ProductImage with(
            final String id,
            final String name,
            final String location,
            final String url
    ) {
        return new ProductImage(id, name, location, url);
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String location() {
        return location;
    }

    public String url() {
        return url;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ProductImage that = (ProductImage) o;
        return Objects.equals(id, that.id) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, location, url);
    }
}
