package com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch;

import com.kaua.ecommerce.domain.product.ProductAttributes;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "product_attributes")
public class ProductAttributesElasticsearchEntity {

    @Field(name = "color", type = FieldType.Nested)
    private ProductColorElasticsearchEntity color;

    @Field(name = "size", type = FieldType.Nested)
    private ProductSizeElasticsearchEntity size;

    @Field(name = "sku", type = FieldType.Text)
    private String sku;

    public ProductAttributesElasticsearchEntity() {
    }

    private ProductAttributesElasticsearchEntity(
            final ProductAttributes attributes,
            final String sku
    ) {
        this.color = ProductColorElasticsearchEntity.toEntity(attributes.getColor());
        this.size = ProductSizeElasticsearchEntity.toEntity(attributes.getSize());
        this.sku = sku;
    }

    public static ProductAttributesElasticsearchEntity toEntity(final ProductAttributes attributes) {
        return new ProductAttributesElasticsearchEntity(attributes, attributes.getSku());
    }

    public ProductColorElasticsearchEntity getColor() {
        return color;
    }

    public ProductSizeElasticsearchEntity getSize() {
        return size;
    }

    public String getSku() {
        return sku;
    }
}
