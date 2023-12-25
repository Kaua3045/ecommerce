package com.kaua.ecommerce.domain.product;

import com.kaua.ecommerce.domain.ValueObject;
import com.kaua.ecommerce.domain.utils.IdUtils;

import java.util.Objects;

public class ProductImage extends ValueObject {

    private final String id;
    private final String checksum;
    private final String name;
    private final String location;

    private ProductImage(
            final String id,
            final String checksum,
            final String name,
            final String location
    ) {
        this.id = Objects.requireNonNull(id);
        this.checksum = Objects.requireNonNull(checksum);
        this.name = Objects.requireNonNull(name);
        this.location = Objects.requireNonNull(location);
    }

    public static ProductImage with(
            final String checksum,
            final String name,
            final String location
    ) {
        return new ProductImage(IdUtils.generate(), checksum, name, location);
    }

    public static ProductImage with(
            final String id,
            final String checksum,
            final String name,
            final String location
    ) {
        return new ProductImage(id, checksum, name, location);
    }

    public String id() {
        return id;
    }

    public String checksum() {
        return checksum;
    }

    public String name() {
        return name;
    }

    public String location() {
        return location;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ProductImage that = (ProductImage) o;
        return Objects.equals(checksum, that.checksum) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checksum, name, location);
    }
}
