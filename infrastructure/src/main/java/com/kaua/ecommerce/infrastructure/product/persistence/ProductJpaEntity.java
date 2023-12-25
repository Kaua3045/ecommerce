package com.kaua.ecommerce.infrastructure.product.persistence;

import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductAttributes;
import com.kaua.ecommerce.domain.product.ProductColor;
import com.kaua.ecommerce.domain.product.ProductSize;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "products")
public class ProductJpaEntity {

    @Id
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "category_id", nullable = false)
    private String categoryId;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ProductAttributesJpaEntity> attributes;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    public ProductJpaEntity() {}

    private ProductJpaEntity(
            final String id,
            final String name,
            final String description,
            final double price,
            final int quantity,
            final String categoryId,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.attributes = new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ProductJpaEntity toEntity(final Product aProduct) {
        final var aEntity = new ProductJpaEntity(
                aProduct.getId().getValue(),
                aProduct.getName(),
                aProduct.getDescription(),
                aProduct.getPrice(),
                aProduct.getQuantity(),
                aProduct.getCategoryId().getValue(),
                aProduct.getCreatedAt(),
                aProduct.getUpdatedAt()
        );

        aProduct.getAttributes().forEach(aEntity::addAttributes);

        return aEntity;
    }

    public Product toDomain() {
        return Product.with(
                getId(),
                getName(),
                getDescription(),
                getPrice(),
                getQuantity(),
                null,
                getCategoryId(),
                getAttributesToDomain(),
                getCreatedAt(),
                getUpdatedAt()
        );
    }

    public void addAttributes(final ProductAttributes attributes) {
        final var attributesJpaEntity = ProductAttributesJpaEntity.toEntity(attributes, this);
        this.attributes.add(attributesJpaEntity);
    }

    public Set<ProductAttributes> getAttributesToDomain() {
        return getAttributes().stream()
                .map(it -> ProductAttributes.with(
                        ProductColor.with(it.getColor().getId(), it.getColor().getColor()),
                        ProductSize.with(
                                it.getSize().getId(),
                                it.getSize().getSize(),
                                it.getSize().getWeight(),
                                it.getSize().getHeight(),
                                it.getSize().getWidth(),
                                it.getSize().getDepth()
                        ), it.getSku())).collect(Collectors.toSet());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Set<ProductAttributesJpaEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<ProductAttributesJpaEntity> attributes) {
        this.attributes = attributes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
