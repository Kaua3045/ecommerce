package com.kaua.ecommerce.application.usecases.product.retrieve.get;

import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.utils.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record GetProductByIdOutput(
        String productId,
        String name,
        String description,
        BigDecimal price,
        String categoryId,
        GetProductByIdOutputImages bannerImage,
        Set<GetProductByIdOutputImages> galleryImages,
        Set<GetProductByIdOutputAttributes> attributes,
        String status,
        Instant createdAt,
        Instant updatedAt,
        long version
) {

    public static GetProductByIdOutput from(final Product aProduct) {
        return new GetProductByIdOutput(
                aProduct.getId().getValue(),
                aProduct.getName(),
                aProduct.getDescription(),
                aProduct.getPrice(),
                aProduct.getCategoryId().getValue(),
                aProduct.getBannerImage().map(GetProductByIdOutputImages::from).orElse(null),
                CollectionUtils.mapTo(aProduct.getImages(), GetProductByIdOutputImages::from),
                CollectionUtils.mapTo(aProduct.getAttributes(), GetProductByIdOutputAttributes::from),
                aProduct.getStatus().name(),
                aProduct.getCreatedAt(),
                aProduct.getUpdatedAt(),
                aProduct.getVersion()
        );
    }
}
