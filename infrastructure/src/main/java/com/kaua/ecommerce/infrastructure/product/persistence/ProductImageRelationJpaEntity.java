package com.kaua.ecommerce.infrastructure.product.persistence;

import com.kaua.ecommerce.domain.product.ProductImage;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "products_images_relations")
public class ProductImageRelationJpaEntity {

    @EmbeddedId
    private ProductImageRelationId id;

    @MapsId("productId")
    @ManyToOne
    private ProductJpaEntity product;

    @MapsId("imageId")
    @ManyToOne(cascade = CascadeType.ALL)
    private ProductImageJpaEntity image;

    public ProductImageRelationJpaEntity() {}

    private ProductImageRelationJpaEntity(
            final ProductJpaEntity product,
            final ProductImage image
    ) {
        this.id = ProductImageRelationId.from(product.getId(), image.getId());
        this.product = product;
        this.image = ProductImageJpaEntity.toEntity(image);
    }

    public static ProductImageRelationJpaEntity toEntity(
            final ProductImage image,
            final ProductJpaEntity product
    ) {
        return new ProductImageRelationJpaEntity(product, image);
    }

    public ProductImageRelationId getId() {
        return id;
    }

    public ProductJpaEntity getProduct() {
        return product;
    }

    public ProductImageJpaEntity getImage() {
        return image;
    }
}
