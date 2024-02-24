package com.kaua.ecommerce.application.usecases.product.search.remove;

import com.kaua.ecommerce.application.UnitUseCase;
import com.kaua.ecommerce.application.gateways.MediaResourceGateway;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.gateways.ProductInventoryGateway;
import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.domain.product.Product;

import java.util.Objects;

public class RemoveProductUseCase extends UnitUseCase<String> {

    private final ProductGateway productGateway;
    private final MediaResourceGateway mediaResourceGateway;
    private final SearchGateway<Product> productSearchGateway;
    private final ProductInventoryGateway productInventoryGateway;

    public RemoveProductUseCase(
            final ProductGateway productGateway,
            final MediaResourceGateway mediaResourceGateway,
            final SearchGateway<Product> productSearchGateway,
            final ProductInventoryGateway productInventoryGateway
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
        this.productSearchGateway = Objects.requireNonNull(productSearchGateway);
        this.productInventoryGateway = Objects.requireNonNull(productInventoryGateway);
    }

    @Override
    public void execute(String aId) {
        this.productGateway.findById(aId)
                .ifPresent(aProduct -> {
                    if (!aProduct.getImages().isEmpty()) {
                        this.mediaResourceGateway.clearImages(aProduct.getImages());
                    }

                    aProduct.getBannerImage().ifPresent(this.mediaResourceGateway::clearImage);
                    this.productGateway.delete(aId);
                });
        this.productInventoryGateway.cleanInventoriesByProductId(aId);
        this.productSearchGateway.deleteById(aId);
    }
}
