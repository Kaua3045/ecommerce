package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.application.gateways.MediaResourceGateway;
import com.kaua.ecommerce.domain.product.ProductID;
import com.kaua.ecommerce.domain.product.ProductImage;
import com.kaua.ecommerce.domain.product.ProductImageResource;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.configurations.properties.storage.StorageProperties;
import com.kaua.ecommerce.infrastructure.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

@Component
public class ProductMediaResourceGateway implements MediaResourceGateway {

    private static final Logger log = LoggerFactory.getLogger(ProductMediaResourceGateway.class);

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
        log.info("stored image: {}", aProductImage);
        return aProductImage;
    }

    @Override
    public void clearImage(ProductImage aImage) {
        this.storageService.delete(aImage.getLocation());
        log.info("deleted image: {}", aImage);
    }

    @Override
    public void clearImages(Set<ProductImage> aImages) {
        final var aLocations = aImages.stream()
                .map(ProductImage::getLocation)
                .toList();
        this.storageService.deleteAllByLocation(aLocations);
        log.info("deleted images: {}", aLocations.size());
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
        this.storageService.store(aProductImage.getLocation(), aImageResource);
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
