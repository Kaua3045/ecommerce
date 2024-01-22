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

    private ProductImage generateProductImage(ProductID aProductID, ProductImageResource aResource) {
        final var aImageResource = aResource.resource();
        final var aProductImageId = IdUtils.generate().replace("-", "");
        // TODO: Refactor buildLocation to receive aProductImageId as parameter, in this moment buildLocation use other random id
        final var aLocation = buildLocation(aProductID, aResource);
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

    private String buildLocation(ProductID aProductID, ProductImageResource aResource) {
        final var aImageResource = aResource.resource();
        return aProductID.getValue()
                .concat("-")
                .concat(aResource.type().name())
                .concat("-")
                .concat(IdUtils.generate().replace("-", ""))
                .concat("-")
                .concat(aImageResource.fileName());
    }
}
