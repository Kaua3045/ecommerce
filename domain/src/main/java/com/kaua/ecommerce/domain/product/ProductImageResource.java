package com.kaua.ecommerce.domain.product;

import com.kaua.ecommerce.domain.ValueObject;
import com.kaua.ecommerce.domain.utils.Resource;

import java.util.Objects;

public class ProductImageResource extends ValueObject {

    private final Resource resource;
    private final ProductImageType type;

    private ProductImageResource(final Resource resource, final ProductImageType type) {
        this.resource = Objects.requireNonNull(resource);
        this.type = Objects.requireNonNull(type);
    }

    public static ProductImageResource with(final Resource resource, final ProductImageType type) {
        return new ProductImageResource(resource, type);
    }

    public Resource resource() {
        return resource;
    }

    public ProductImageType type() {
        return type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ProductImageResource that = (ProductImageResource) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, type);
    }
}
