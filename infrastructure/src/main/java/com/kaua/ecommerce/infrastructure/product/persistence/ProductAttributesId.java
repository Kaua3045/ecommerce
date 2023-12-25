package com.kaua.ecommerce.infrastructure.product.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductAttributesId implements Serializable {

    @Column(name = "product_id", nullable = false)
    private String productId;

    public ProductAttributesId() {
    }

    private ProductAttributesId(final String productId) {
        this.productId = productId;
    }

    public static ProductAttributesId from(final String productId) {
        return new ProductAttributesId(productId);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ProductAttributesId that = (ProductAttributesId) o;
        return Objects.equals(getProductId(), that.getProductId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductId());
    }

    public String getProductId() {
        return productId;
    }
}
