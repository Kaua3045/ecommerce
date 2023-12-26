package com.kaua.ecommerce.domain.product;

import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Product extends AggregateRoot<ProductID> {

    private String name;
    private String description;
    private double price;
    private int quantity;
    private ProductImage coverImageUrl;
    private CategoryID categoryId;
    private Set<ProductAttributes> attributes;
    private Instant createdAt;
    private Instant updatedAt;

    private Product(
            final ProductID aProductID,
            final String aName,
            final String aDescription,
            final double aPrice,
            final int aQuantity,
            final ProductImage aCoverImageUrl,
            final CategoryID aCategoryId,
            final Set<ProductAttributes> aAttributes,
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
        this.attributes = aAttributes;
        this.createdAt = aCreatedAt;
        this.updatedAt = aUpdatedAt;
    }

    public static Product newProduct(
            final String aName,
            final String aDescription,
            final double aPrice,
            final int aQuantity,
            final CategoryID aCategoryId,
            final ProductAttributes aAttributes
    ) {
        final var aId = ProductID.unique();
        final var aNow = InstantUtils.now();
        final var aProduct = new Product(
                aId,
                aName,
                aDescription,
                aPrice,
                aQuantity,
                null,
                aCategoryId,
                new HashSet<>(),
                aNow,
                aNow
        );

        aProduct.attributes.add(aAttributes);
        return aProduct;
    }

    public static Product with(
            final String aProductID,
            final String aName,
            final String aDescription,
            final double aPrice,
            final int aQuantity,
            final ProductImage aCoverImageUrl,
            final String aCategoryId,
            final Set<ProductAttributes> aAttributes,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        return new Product(
                ProductID.from(aProductID),
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCoverImageUrl,
                CategoryID.from(aCategoryId),
                new HashSet<>(aAttributes == null ? Collections.emptySet() : aAttributes),
                aCreatedAt,
                aUpdatedAt
        );
    }

    public static Product with(final Product aProduct) {
        return new Product(
                aProduct.getId(),
                aProduct.getName(),
                aProduct.getDescription(),
                aProduct.getPrice(),
                aProduct.getQuantity(),
                aProduct.getCoverImageUrl().orElse(null),
                aProduct.getCategoryId(),
                aProduct.getAttributes(),
                aProduct.getCreatedAt(),
                aProduct.getUpdatedAt()
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

    public Optional<ProductImage> getCoverImageUrl() {
        return Optional.ofNullable(coverImageUrl);
    }

    public CategoryID getCategoryId() {
        return categoryId;
    }

    public Set<ProductAttributes> getAttributes() {
        return Collections.unmodifiableSet(attributes);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
