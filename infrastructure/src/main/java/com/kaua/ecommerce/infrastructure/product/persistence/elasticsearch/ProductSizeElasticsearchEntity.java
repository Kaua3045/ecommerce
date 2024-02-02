package com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch;

import com.kaua.ecommerce.domain.product.ProductSize;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "product_sizes")
public class ProductSizeElasticsearchEntity {

    @Id
    private String id;

    @Field(name = "size", type = FieldType.Text)
    private String size;

    @Field(name = "weight", type = FieldType.Double)
    private double weight;

    @Field(name = "height", type = FieldType.Double)
    private double height;

    @Field(name = "width", type = FieldType.Double)
    private double width;

    @Field(name = "depth", type = FieldType.Double)
    private double depth;

    public ProductSizeElasticsearchEntity() {
    }

    private ProductSizeElasticsearchEntity(
            final String id,
            final String size,
            final double weight,
            final double height,
            final double width,
            final double depth
    ) {
        this.id = id;
        this.size = size;
        this.weight = weight;
        this.height = height;
        this.width = width;
        this.depth = depth;
    }

    public static ProductSizeElasticsearchEntity toEntity(final ProductSize aProductSize) {
        return new ProductSizeElasticsearchEntity(
                aProductSize.getId(),
                aProductSize.getSize(),
                aProductSize.getWeight(),
                aProductSize.getHeight(),
                aProductSize.getWidth(),
                aProductSize.getDepth());
    }

    public ProductSize toDomain() {
        return ProductSize.with(getId(), getSize(), getWeight(), getHeight(), getWidth(), getDepth());
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

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }
}
