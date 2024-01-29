package com.kaua.ecommerce.application.usecases.product.delete;

import com.kaua.ecommerce.application.gateways.MediaResourceGateway;
import com.kaua.ecommerce.application.gateways.ProductGateway;

import java.util.Objects;

public class DefaultDeleteProductUseCase extends DeleteProductUseCase {

    private final ProductGateway productGateway;
    private final MediaResourceGateway mediaResourceGateway;

    public DefaultDeleteProductUseCase(
            final ProductGateway productGateway,
            final MediaResourceGateway mediaResourceGateway
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    // TODO: refactor this method to publish an event to delete the product and its images from the storage
    // before persisting the delete event inactivate product in database
    @Override
    public void execute(String aId) {
        this.productGateway.findById(aId).ifPresent(product -> {
            this.productGateway.delete(aId);
            product.getBannerImage().ifPresent(this.mediaResourceGateway::clearImage);
            if (!product.getImages().isEmpty())
                this.mediaResourceGateway.clearImages(product.getImages());
        });
    }
}
