package com.kaua.ecommerce.infrastructure.product.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductAttributesId implements Serializable {

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "color_id", nullable = false)
    private String colorId;

    @Column(name = "size_id", nullable = false)
    private String sizeId;

    public ProductAttributesId() {
    }

    private ProductAttributesId(
            final String productId,
            final String colorId,
            final String sizeId
    ) {
        this.productId = productId;
        this.colorId = colorId;
        this.sizeId = sizeId;
    }

    public static ProductAttributesId from(
            final String productId,
            final String colorId,
            final String sizeId
    ) {
        return new ProductAttributesId(productId, colorId, sizeId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductAttributesId that = (ProductAttributesId) o;
        return Objects.equals(getProductId(), that.getProductId()) && Objects.equals(getColorId(), that.getColorId()) && Objects.equals(getSizeId(), that.getSizeId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductId(), getColorId(), getSizeId());
    }

    public String getProductId() {
        return productId;
    }

    public String getColorId() {
        return colorId;
    }

    public String getSizeId() {
        return sizeId;
    }
}
