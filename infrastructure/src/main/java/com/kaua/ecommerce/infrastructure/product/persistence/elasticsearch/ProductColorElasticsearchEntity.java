package com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch;

import com.kaua.ecommerce.domain.product.ProductColor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "product_colors")
public class ProductColorElasticsearchEntity {

    @Id
    private String id;

    @Field(name = "color", type = FieldType.Text)
    private String color;

    public ProductColorElasticsearchEntity() {
    }

    private ProductColorElasticsearchEntity(final String id, final String color) {
        this.id = id;
        this.color = color;
    }

    public static ProductColorElasticsearchEntity toEntity(final ProductColor aProductColor) {
        return new ProductColorElasticsearchEntity(aProductColor.getId(), aProductColor.getColor());
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
