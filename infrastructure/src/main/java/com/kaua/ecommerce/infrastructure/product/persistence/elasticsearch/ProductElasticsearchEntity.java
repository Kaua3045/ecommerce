package com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch;

import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductAttributes;
import com.kaua.ecommerce.domain.product.ProductImage;
import com.kaua.ecommerce.domain.product.ProductStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Document(indexName = "products")
public class ProductElasticsearchEntity {

    @Id
    private String id;

    @MultiField(
            mainField = @Field(type = FieldType.Text, name = "name"),
            otherFields = @InnerField(suffix = "keyword", type = FieldType.Keyword)
    )
    private String name;

    @Field(type = FieldType.Text, name = "description")
    private String description;

    private BigDecimal price;

    @Field(type = FieldType.Integer, name = "quantity")
    private int quantity;

    @Field(type = FieldType.Nested, name = "banner_image")
    private ProductImageElasticsearchEntity bannerImage;

    @Field(type = FieldType.Nested, name = "images")
    private Set<ProductImageElasticsearchEntity> images;

    @MultiField(
            mainField = @Field(type = FieldType.Text, name = "category_id"),
            otherFields = @InnerField(suffix = "keyword", type = FieldType.Keyword)
    )
    private String categoryId;

    @Field(type = FieldType.Nested, name = "attributes")
    private Set<ProductAttributesElasticsearchEntity> attributes;

    private ProductStatus status;

    @Field(type = FieldType.Date, name = "created_at")
    private Instant createdAt;

    @Field(type = FieldType.Date, name = "updated_at")
    private Instant updatedAt;

    public ProductElasticsearchEntity() {
    }

    public ProductElasticsearchEntity(
            final String id,
            final String name,
            final String description,
            final BigDecimal price,
            final int quantity,
            final ProductImageElasticsearchEntity bannerImage,
            final String categoryId,
            final ProductStatus status,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.bannerImage = bannerImage;
        this.images = new HashSet<>();
        this.categoryId = categoryId;
        this.attributes = new HashSet<>();
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ProductElasticsearchEntity toEntity(final Product aProduct) {
        final var aEntity = new ProductElasticsearchEntity(
                aProduct.getId().getValue(),
                aProduct.getName(),
                aProduct.getDescription(),
                aProduct.getPrice(),
                aProduct.getQuantity(),
                aProduct.getBannerImage()
                        .map(ProductImageElasticsearchEntity::toEntity)
                        .orElse(null),
                aProduct.getCategoryId().getValue(),
                aProduct.getStatus(),
                aProduct.getCreatedAt(),
                aProduct.getUpdatedAt()
        );

        aProduct.getImages().forEach(aEntity::addImage);
        aProduct.getAttributes().forEach(aEntity::addAttribute);

        return aEntity;
    }

    public Product toDomain() {
        return Product.with(
                getId(),
                getName(),
                getDescription(),
                getPrice(),
                getQuantity(),
                getBannerImage().map(ProductImageElasticsearchEntity::toDomain).orElse(null),
                getImages().stream()
                        .map(ProductImageElasticsearchEntity::toDomain)
                        .collect(Collectors.toSet()),
                getCategoryId(),
                getAttributesDomain(),
                getStatus(),
                getCreatedAt(),
                getUpdatedAt()
        );
    }

    private Set<ProductAttributes> getAttributesDomain() {
        return getAttributes()
                .stream().map(attribute -> ProductAttributes.with(
                        attribute.getColor().toDomain(),
                        attribute.getSize().toDomain(),
                        attribute.getSku()
                )).collect(Collectors.toSet());
    }

    public void addImage(final ProductImage aImage) {
        this.images.add(ProductImageElasticsearchEntity.toEntity(aImage));
    }

    public void addAttribute(final ProductAttributes aAttribute) {
        this.attributes.add(ProductAttributesElasticsearchEntity.toEntity(aAttribute));
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

    public Optional<ProductImageElasticsearchEntity> getBannerImage() {
        return Optional.ofNullable(bannerImage);
    }

    public void setBannerImage(ProductImageElasticsearchEntity bannerImage) {
        this.bannerImage = bannerImage;
    }

    public Set<ProductImageElasticsearchEntity> getImages() {
        return images;
    }

    public void setImages(Set<ProductImageElasticsearchEntity> images) {
        this.images = images;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Set<ProductAttributesElasticsearchEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<ProductAttributesElasticsearchEntity> attributes) {
        this.attributes = attributes;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
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
