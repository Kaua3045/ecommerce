package com.kaua.ecommerce.infrastructure.product.persistence;

import com.kaua.ecommerce.domain.product.ProductColor;
import jakarta.persistence.*;

@Entity
@Table(name = "products_colors")
public class ProductColorJpaEntity {

    @Id
    private String id;

    @Column(name = "color", nullable = false)
    private String color;

    public ProductColorJpaEntity() {}

    private ProductColorJpaEntity(final String id, final String color) {
        this.id = id;
        this.color = color;
    }

    public static ProductColorJpaEntity toEntity(final ProductColor aProductColor) {
        return new ProductColorJpaEntity(aProductColor.getId(), aProductColor.getColor());
    }

    public ProductColor toDomain() {
        return ProductColor.with(getId(), getColor());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
