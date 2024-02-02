package com.kaua.ecommerce.infrastructure.product.persistence;

import com.kaua.ecommerce.domain.product.ProductImage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "products_images")
public class ProductImageJpaEntity {

    @Id
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "url", nullable = false)
    private String url;

    public ProductImageJpaEntity() {}

    private ProductImageJpaEntity(
            final String id,
            final String name,
            final String location,
            final String url
    ) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.url = url;
    }

    public static ProductImageJpaEntity toEntity(ProductImage aProductImage) {
        return new ProductImageJpaEntity(
                aProductImage.getId(),
                aProductImage.getName(),
                aProductImage.getLocation(),
                aProductImage.getUrl()
        );
    }

    public ProductImage toDomain() {
        return ProductImage.with(
                getId(),
                getName(),
                getLocation(),
                getUrl()
        );
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
