package com.kaua.ecommerce.domain.product;

import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

import java.time.Instant;
import java.util.Optional;

public class Product extends AggregateRoot<ProductID> {

    private String name;
    private String description;
    private double price;
    private int quantity;
    private String coverImageUrl;
    private CategoryID categoryId;
    private Instant createdAt;
    private Instant updatedAt;

    private Product(
            final ProductID aProductID,
            final String aName,
            final String aDescription,
            final double aPrice,
            final int aQuantity,
            final String aCoverImageUrl,
            final CategoryID aCategoryId,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        super(aProductID);
        this.name = aName;
        this.description = aDescription;
        this.price = aPrice;
        this.quantity = aQuantity;
        this.coverImageUrl = aCoverImageUrl;
        this.categoryId = aCategoryId;
        this.createdAt = aCreatedAt;
        this.updatedAt = aUpdatedAt;
    }

    public static Product newProduct(
            final String aName,
            final String aDescription,
            final double aPrice,
            final int aQuantity,
            final CategoryID aCategoryId
    ) {
        final var aId = ProductID.unique();
        final var aNow = InstantUtils.now();
        return new Product(
                aId,
                aName,
                aDescription,
                aPrice,
                aQuantity,
                null,
                aCategoryId,
                aNow,
                aNow
        );
    }

    @Override
    public void validate(ValidationHandler handler) {
        new ProductValidation(this, handler).validate();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public Optional<String> getCoverImageUrl() {
        return Optional.ofNullable(coverImageUrl);
    }

    public CategoryID getCategoryId() {
        return categoryId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
