package com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch;

import com.kaua.ecommerce.domain.product.ProductImage;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "product_images")
public class ProductImageElasticsearchEntity {

    @Id
    private String id;

    @Field(name = "name", type = FieldType.Text)
    private String name;

    @Field(name = "location", type = FieldType.Text)
    private String location;

    @Field(name = "url", type = FieldType.Text)
    private String url;

    public ProductImageElasticsearchEntity() {
    }

    private ProductImageElasticsearchEntity(
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

    public static ProductImageElasticsearchEntity toEntity(ProductImage aProductImage) {
        return new ProductImageElasticsearchEntity(
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

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
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
