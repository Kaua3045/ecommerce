package com.kaua.ecommerce.infrastructure.product.persistence;

import com.kaua.ecommerce.domain.product.ProductAttributes;
import jakarta.persistence.*;

@Entity
@Table(name = "products_attributes")
public class ProductAttributesJpaEntity {

    @EmbeddedId
    private ProductAttributesId id;

    @MapsId("productId")
    @ManyToOne
    private ProductJpaEntity product;

    @MapsId("colorId")
    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private ProductColorJpaEntity color;

    @MapsId("sizeId")
    @ManyToOne(cascade = CascadeType.ALL)
    private ProductSizeJpaEntity size;

    @Column(name = "sku", nullable = false)
    private String sku;

    public ProductAttributesJpaEntity() {
    }

    private ProductAttributesJpaEntity(
            final ProductAttributes attributes,
            final ProductJpaEntity product,
            final String sku
    ) {
        this.id = ProductAttributesId.from(
                product.getId(),
                attributes.getColor().getId(),
                attributes.getSize().getId()
        );
        this.color = ProductColorJpaEntity.toEntity(attributes.getColor());
        this.size = ProductSizeJpaEntity.toEntity(attributes.getSize());
        this.product = product;
        this.sku = sku;
    }

    public static ProductAttributesJpaEntity toEntity(final ProductAttributes attributes, final ProductJpaEntity product) {
        return new ProductAttributesJpaEntity(attributes, product, attributes.getSku());
    }

    public ProductAttributesId getId() {
        return id;
    }

    public ProductJpaEntity getProduct() {
        return product;
    }

    public ProductColorJpaEntity getColor() {
        return color;
    }

    public ProductSizeJpaEntity getSize() {
        return size;
    }

    public String getSku() {
        return sku;
    }
}
