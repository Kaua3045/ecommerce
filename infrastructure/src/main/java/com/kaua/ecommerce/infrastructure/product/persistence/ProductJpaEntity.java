package com.kaua.ecommerce.infrastructure.product.persistence;

import com.kaua.ecommerce.domain.product.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
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

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "category_id", nullable = false)
    private String categoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ProductAttributesJpaEntity> attributes;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "banner_image_id", referencedColumnName = "id")
    private ProductImageJpaEntity bannerImage;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ProductImageRelationJpaEntity> images;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    @Version
    private long version;

    public ProductJpaEntity() {
    }

    private ProductJpaEntity(
            final String id,
            final String name,
            final String description,
            final BigDecimal price,
            final int quantity,
            final String categoryId,
            final ProductStatus status,
            final ProductImageJpaEntity bannerImage,
            final Instant createdAt,
            final Instant updatedAt,
            final long version
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.status = status;
        this.attributes = new HashSet<>();
        this.bannerImage = bannerImage;
        this.images = new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static ProductJpaEntity toEntity(final Product aProduct) {
        final var aEntity = new ProductJpaEntity(
                aProduct.getId().getValue(),
                aProduct.getName(),
                aProduct.getDescription(),
                aProduct.getPrice(),
                aProduct.getQuantity(),
                aProduct.getCategoryId().getValue(),
                aProduct.getStatus(),
                aProduct.getBannerImage().map(ProductImageJpaEntity::toEntity).orElse(null),
                aProduct.getCreatedAt(),
                aProduct.getUpdatedAt(),
                aProduct.getVersion()
        );

        aProduct.getAttributes().forEach(aEntity::addAttributes);
        aProduct.getImages().forEach(aEntity::addImages);

        return aEntity;
    }

    public Product toDomain() {
        return Product.with(
                getId(),
                getName(),
                getDescription(),
                getPrice(),
                getQuantity(),
                getBannerImage().map(ProductImageJpaEntity::toDomain).orElse(null),
                getImagesToDomain(),
                getCategoryId(),
                getAttributesToDomain(),
                getStatus(),
                getCreatedAt(),
                getUpdatedAt(),
                getVersion()
        );
    }

    public void addAttributes(final ProductAttributes attributes) {
        final var attributesJpaEntity = ProductAttributesJpaEntity.toEntity(attributes, this);
        this.attributes.add(attributesJpaEntity);
    }

    public void addImages(final ProductImage image) {
        final var aImageEntity = ProductImageRelationJpaEntity.toEntity(image, this);
        this.images.add(aImageEntity);
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

    public Set<ProductImage> getImagesToDomain() {
        return getImages().stream()
                .map(it -> ProductImage.with(
                        it.getImage().getId(),
                        it.getImage().getName(),
                        it.getImage().getLocation(),
                        it.getImage().getUrl()
                )).collect(Collectors.toSet());
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
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

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public Set<ProductAttributesJpaEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<ProductAttributesJpaEntity> attributes) {
        this.attributes = attributes;
    }

    public Optional<ProductImageJpaEntity> getBannerImage() {
        return Optional.ofNullable(bannerImage);
    }

    public void setBannerImage(ProductImageJpaEntity bannerImage) {
        this.bannerImage = bannerImage;
    }

    public Set<ProductImageRelationJpaEntity> getImages() {
        return images;
    }

    public void setImages(Set<ProductImageRelationJpaEntity> images) {
        this.images = images;
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

    public long getVersion() {
        return version;
    }
}
