package com.kaua.ecommerce.infrastructure.product.presenter;

import com.kaua.ecommerce.application.usecases.product.retrieve.get.GetProductByIdOutput;
import com.kaua.ecommerce.infrastructure.product.models.GetProductResponse;

public final class ProductApiPresenter {

    private ProductApiPresenter() {
    }

    public static GetProductResponse present(final GetProductByIdOutput aOutput) {
        return new GetProductResponse(
                aOutput.productId(),
                aOutput.name(),
                aOutput.description(),
                aOutput.price(),
                aOutput.quantity(),
                aOutput.categoryId(),
                aOutput.bannerImage(),
                aOutput.galleryImages(),
                aOutput.attributes(),
                aOutput.status(),
                aOutput.createdAt(),
                aOutput.updatedAt(),
                aOutput.version()
        );
    }
}
