package com.kaua.ecommerce.application.usecases.product.search.retrieve.list;

import com.kaua.ecommerce.domain.product.Product;

import java.math.BigDecimal;
import java.time.Instant;

public record ListProductsOutput(
        String id,
        String name,
        String description,
        BigDecimal price,
//        int quantity,
        String categoryId,
        ListProductsImagesOutput bannerImage,
        String status,
        Instant createdAt,
        Instant updatedAt,
        long version
) {

    public static ListProductsOutput from(final Product aProduct) {
        return new ListProductsOutput(
                aProduct.getId().getValue(),
                aProduct.getName(),
                aProduct.getDescription(),
                aProduct.getPrice(),
//                aProduct.getQuantity(),
                aProduct.getCategoryId().getValue(),
                aProduct.getBannerImage().map(ListProductsImagesOutput::from).orElse(null),
                aProduct.getStatus().name(),
                aProduct.getCreatedAt(),
                aProduct.getUpdatedAt(),
                aProduct.getVersion()
        );
    }
}
