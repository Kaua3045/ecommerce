package com.kaua.ecommerce.infrastructure.product.persistence;

import com.kaua.ecommerce.domain.product.ProductSize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products_sizes")
public class ProductSizeJpaEntity {

    @Id
    private String id;

    @Column(name = "size_name", nullable = false)
    private String size;

    @Column(name = "weight", nullable = false)
    private double weight;

    @Column(name = "height", nullable = false)
    private double height;

    @Column(name = "width", nullable = false)
    private double width;

    @Column(name = "p_length", nullable = false)
    private double length;

    public ProductSizeJpaEntity() {}

    private ProductSizeJpaEntity(
            final String id,
            final String size,
            final double weight,
            final double height,
            final double width,
            final double length
    ) {
        this.id = id;
        this.size = size;
        this.weight = weight;
        this.height = height;
        this.width = width;
        this.length = length;
    }

    public static ProductSizeJpaEntity toEntity(final ProductSize aProductSize) {
        return new ProductSizeJpaEntity(
                aProductSize.getId(),
                aProductSize.getSize(),
                aProductSize.getWeight(),
                aProductSize.getHeight(),
                aProductSize.getWidth(),
                aProductSize.getLength());
    }

    public ProductSize toDomain() {
        return ProductSize.with(getId(), getSize(), getWeight(), getHeight(), getWidth(), getLength());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }
}
