package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.application.gateways.MediaResourceGateway;
import com.kaua.ecommerce.domain.product.ProductID;
import com.kaua.ecommerce.domain.product.ProductImage;
import com.kaua.ecommerce.domain.product.ProductImageResource;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.configurations.properties.storage.StorageProperties;
import com.kaua.ecommerce.infrastructure.service.StorageService;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

@Component
public class ProductMediaResourceGateway implements MediaResourceGateway {

    private final StorageService storageService;
    private final StorageProperties storageProperties;

    public ProductMediaResourceGateway(
            final StorageService storageService,
            final StorageProperties storageProperties
    ) {
        this.storageService = Objects.requireNonNull(storageService);
        this.storageProperties = Objects.requireNonNull(storageProperties);
    }

    @Override
    public ProductImage storeImage(ProductID aProductID, ProductImageResource aResource) {
        final var aProductImage = this.generateProductImage(aProductID, aResource);
        store(aProductImage, aResource);
        return aProductImage;
    }

    @Override
    public void clearImage(ProductImage aImage) {
        this.storageService.delete(aImage.location());
    }

    @Override
    public void clearImages(Set<ProductImage> aImages) {
        final var aLocations = aImages.stream()
                .map(ProductImage::location)
                .toList();
        this.storageService.deleteAllByLocation(aLocations);
    }

    private ProductImage generateProductImage(ProductID aProductID, ProductImageResource aResource) {
        final var aImageResource = aResource.resource();
        final var aProductImageId = IdUtils.generateWithoutDash();
        final var aLocation = buildLocation(aProductID, aResource, aProductImageId);
        final var aUrl = getProviderUrl().concat("/").concat(aLocation);
        return ProductImage.with(
                aProductImageId,
                aImageResource.fileName(),
                aLocation,
                aUrl
        );
    }

    private void store(final ProductImage aProductImage, final ProductImageResource aResource) {
        final var aImageResource = aResource.resource();
        this.storageService.store(aProductImage.location(), aImageResource);
    }

    public String getProviderUrl() {
        return this.storageProperties.getProviderUrl();
    }

    private String buildLocation(
            final ProductID aProductID,
            final ProductImageResource aResource,
            final String aProductImageId
    ) {
        final var aImageResource = aResource.resource();
        return aProductID.getValue()
                .concat("-")
                .concat(aResource.type().name())
                .concat("-")
                .concat(aProductImageId)
                .concat("-")
                .concat(aImageResource.fileName());
    }
}
