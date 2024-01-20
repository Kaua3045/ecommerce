package com.kaua.ecommerce.application.usecases.product.media.upload;

import com.kaua.ecommerce.application.gateways.MediaResourceGateway;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.validation.Error;

import java.util.Objects;

public class DefaultUploadProductImageUseCase extends UploadProductImageUseCase {

    private final ProductGateway productGateway;
    private final MediaResourceGateway mediaResourceGateway;

    public DefaultUploadProductImageUseCase(
            final ProductGateway productGateway,
            final MediaResourceGateway mediaResourceGateway
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public UploadProductImageOutput execute(UploadProductImageCommand input) {
        final var aProduct = this.productGateway.findById(input.productId())
                .orElseThrow(NotFoundException.with(Product.class, input.productId()));
        final var aProductId = aProduct.getId();
        final var aResource = input.productImageResource();

        switch (aResource.type()) {
            case COVER -> {
                aProduct.getCoverImage().ifPresent(this.mediaResourceGateway::clearImage);
                aProduct.changeCoverImage(this.mediaResourceGateway.storeImage(aProductId, aResource));
            }
            case GALLERY -> aProduct.addImage(this.mediaResourceGateway.storeImage(aProductId, aResource));
            default -> throw DomainException.with(new Error("'productImageType' not implemented"));
        }

        this.productGateway.update(aProduct);

        return UploadProductImageOutput.from(aProduct, aResource.type());
    }
}
