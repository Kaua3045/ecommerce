package com.kaua.ecommerce.domain.product;

import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Product extends AggregateRoot<ProductID> {

    private String name;
    private String description;
    private BigDecimal price;
    private int quantity;
    private ProductImage bannerImage;
    private Set<ProductImage> images;
    private CategoryID categoryId;
    private Set<ProductAttributes> attributes;
    private ProductStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    private Product(
            final ProductID aProductID,
            final String aName,
            final String aDescription,
            final BigDecimal aPrice,
            final int aQuantity,
            final ProductImage aBannerImage,
            final Set<ProductImage> aImages,
            final CategoryID aCategoryId,
            final Set<ProductAttributes> aAttributes,
            final ProductStatus aStatus,
            final Instant aCreatedAt,
            final Instant aUpdatedAt,
            final long aVersion
    ) {
        super(aProductID, aVersion);
        this.name = aName;
        this.description = aDescription;
        this.price = aPrice;
        this.quantity = aQuantity;
        this.bannerImage = aBannerImage;
        this.images = aImages;
        this.categoryId = aCategoryId;
        this.attributes = aAttributes;
        this.status = aStatus;
        this.createdAt = aCreatedAt;
        this.updatedAt = aUpdatedAt;
    }

    public static Product newProduct(
            final String aName,
            final String aDescription,
            final BigDecimal aPrice,
            final int aQuantity,
            final CategoryID aCategoryId,
            final Set<ProductAttributes> aAttributes
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
                new HashSet<>(),
                aCategoryId,
                new HashSet<>(),
                ProductStatus.ACTIVE,
                aNow,
                aNow,
                0
        );

        if (aAttributes == null) {
            aProduct.attributes.add(null);
            return aProduct;
        }

        aProduct.attributes.addAll(aAttributes);
        return aProduct;
    }

    public static Product with(
            final String aProductID,
            final String aName,
            final String aDescription,
            final BigDecimal aPrice,
            final int aQuantity,
            final ProductImage aBannerImage,
            final Set<ProductImage> aImages,
            final String aCategoryId,
            final Set<ProductAttributes> aAttributes,
            final ProductStatus aStatus,
            final Instant aCreatedAt,
            final Instant aUpdatedAt,
            final long aVersion
    ) {
        return new Product(
                ProductID.from(aProductID),
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aBannerImage,
                new HashSet<>(aImages == null ? Collections.emptySet() : aImages),
                CategoryID.from(aCategoryId),
                new HashSet<>(aAttributes == null ? Collections.emptySet() : aAttributes),
                aStatus,
                aCreatedAt,
                aUpdatedAt,
                aVersion
        );
    }

    public static Product with(final Product aProduct) {
        return new Product(
                aProduct.getId(),
                aProduct.getName(),
                aProduct.getDescription(),
                aProduct.getPrice(),
                aProduct.getQuantity(),
                aProduct.getBannerImage().orElse(null),
                new HashSet<>(aProduct.getImages()),
                aProduct.getCategoryId(),
                new HashSet<>(aProduct.getAttributes()),
                aProduct.getStatus(),
                aProduct.getCreatedAt(),
                aProduct.getUpdatedAt(),
                aProduct.getVersion()
        );
    }

    public void addImage(final ProductImage aImage) {
        if (aImage == null) {
            return;
        }

        if (this.images.size() == 20) {
            throw DomainException.with(new Error("Product can't have more than 20 images"));
        }

        this.images.add(aImage);
        this.updatedAt = InstantUtils.now();
    }

    public void changeBannerImage(final ProductImage aImage) {
        this.bannerImage = aImage;
        this.updatedAt = InstantUtils.now();
    }

    public Product update(
            final String aName,
            final String aDescription,
            final BigDecimal aPrice,
            final int aQuantity,
            final CategoryID aCategoryId
    ) {
        this.name = aName;
        this.description = aDescription;
        this.price = aPrice;
        this.quantity = aQuantity;
        this.categoryId = aCategoryId;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Product updateStatus(final ProductStatus aStatus) {
        this.status = aStatus;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public ProductImage removeImage(final String aLocation) {
        if (aLocation == null || aLocation.isBlank()) {
            return null;
        }

        final var aResult = this.images.stream()
                .filter(image -> image.getLocation().equalsIgnoreCase(aLocation))
                .findFirst();

        if (aResult.isPresent()) {
            this.images.remove(aResult.get());
            this.updatedAt = InstantUtils.now();
            return aResult.get();
        }
        return null;
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

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public Optional<ProductImage> getBannerImage() {
        return Optional.ofNullable(bannerImage);
    }

    public Set<ProductImage> getImages() {
        return Collections.unmodifiableSet(images);
    }

    public CategoryID getCategoryId() {
        return categoryId;
    }

    public Set<ProductAttributes> getAttributes() {
        return Collections.unmodifiableSet(attributes);
    }

    public ProductStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Product(" +
                "id='" + getId().getValue() + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", bannerImage=" + getBannerImage().map(ProductImage::getLocation).orElse(null) +
                ", images=" + images.size() +
                ", categoryId=" + categoryId.getValue() +
                ", attributes=" + attributes.size() +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ')';
    }
}
