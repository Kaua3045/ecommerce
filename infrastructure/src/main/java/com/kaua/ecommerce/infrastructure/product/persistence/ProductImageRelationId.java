package com.kaua.ecommerce.infrastructure.product.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductImageRelationId implements Serializable {

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "image_id", nullable = false)
    private String imageId;

    public ProductImageRelationId() {
    }

    private ProductImageRelationId(final String productId, final String imageId) {
        this.productId = productId;
        this.imageId = imageId;
    }

    public static ProductImageRelationId from(final String productId, final String imageId) {
        return new ProductImageRelationId(productId, imageId);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ProductImageRelationId that = (ProductImageRelationId) o;
        return Objects.equals(getProductId(), that.getProductId()) && Objects.equals(getImageId(), that.getImageId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductId(), getImageId());
    }

    public String getProductId() {
        return productId;
    }

    public String getImageId() {
        return imageId;
    }
}
