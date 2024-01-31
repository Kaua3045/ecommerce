package com.kaua.ecommerce.application.usecases.product.media.upload;

import com.kaua.ecommerce.application.exceptions.OnlyOneBannerImagePermittedException;
import com.kaua.ecommerce.application.exceptions.ProductIsDeletedException;
import com.kaua.ecommerce.application.gateways.MediaResourceGateway;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductImageResource;
import com.kaua.ecommerce.domain.product.ProductImageType;
import com.kaua.ecommerce.domain.product.ProductStatus;
import com.kaua.ecommerce.domain.product.events.ProductUpdatedEvent;
import com.kaua.ecommerce.domain.validation.Error;

import java.util.List;
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
        final var aResource = input.productImagesResources();

        if (aProduct.getStatus().equals(ProductStatus.DELETED)) {
            throw new ProductIsDeletedException();
        }

        validateBannerImage(aResource);

        aResource.forEach(resource -> {
            switch (resource.type()) {
                case BANNER -> {
                    aProduct.getBannerImage().ifPresent(this.mediaResourceGateway::clearImage);
                    aProduct.changeBannerImage(this.mediaResourceGateway.storeImage(aProductId, resource));
                }
                case GALLERY -> aProduct.addImage(this.mediaResourceGateway.storeImage(aProductId, resource));
                default -> throw DomainException.with(new Error("'productImageType' not implemented"));
            }
        });

        this.productGateway.update(aProduct);
        aProduct.registerEvent(ProductUpdatedEvent.from(aProduct));

        return UploadProductImageOutput.from(aProduct);
    }

    private void validateBannerImage(final List<ProductImageResource> productImageResource) {
        final var aBannerCount = productImageResource.stream().filter(image -> image.type()
                .equals(ProductImageType.BANNER)).count();

        if (aBannerCount > 1) {
            throw new OnlyOneBannerImagePermittedException();
        }
    }
}
