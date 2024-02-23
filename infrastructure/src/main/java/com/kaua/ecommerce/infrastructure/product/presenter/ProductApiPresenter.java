package com.kaua.ecommerce.infrastructure.product.presenter;

import com.kaua.ecommerce.application.usecases.product.retrieve.get.GetProductByIdOutput;
import com.kaua.ecommerce.application.usecases.product.search.retrieve.list.ListProductsOutput;
import com.kaua.ecommerce.infrastructure.product.models.GetProductResponse;
import com.kaua.ecommerce.infrastructure.product.models.ListProductsResponse;

public final class ProductApiPresenter {

    private ProductApiPresenter() {
    }

    public static ListProductsResponse present(final ListProductsOutput aOutput) {
        return new ListProductsResponse(
                aOutput.id(),
                aOutput.name(),
                aOutput.description(),
                aOutput.price(),
//                aOutput.quantity(),
                aOutput.categoryId(),
                aOutput.bannerImage(),
                aOutput.status(),
                aOutput.createdAt(),
                aOutput.updatedAt(),
                aOutput.version()
        );
    }

    public static GetProductResponse present(final GetProductByIdOutput aOutput) {
        return new GetProductResponse(
                aOutput.productId(),
                aOutput.name(),
                aOutput.description(),
                aOutput.price(),
//                aOutput.quantity(),
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
