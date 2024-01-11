package com.kaua.ecommerce.infrastructure.api.controllers;

import com.kaua.ecommerce.application.usecases.product.create.CreateProductCommand;
import com.kaua.ecommerce.application.usecases.product.create.CreateProductUseCase;
import com.kaua.ecommerce.application.usecases.product.media.upload.UploadProductImageCommand;
import com.kaua.ecommerce.application.usecases.product.media.upload.UploadProductImageUseCase;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.product.ProductImageResource;
import com.kaua.ecommerce.domain.product.ProductImageType;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.infrastructure.api.ProductAPI;
import com.kaua.ecommerce.infrastructure.product.models.CreateProductInput;
import com.kaua.ecommerce.infrastructure.utils.ResourceOf;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
public class ProductController implements ProductAPI {

    private final CreateProductUseCase createProductUseCase;
    private final UploadProductImageUseCase uploadProductImageUseCase;

    public ProductController(
            final CreateProductUseCase createProductUseCase,
            final UploadProductImageUseCase uploadProductImageUseCase
    ) {
        this.createProductUseCase = createProductUseCase;
        this.uploadProductImageUseCase = uploadProductImageUseCase;
    }

    @Override
    public ResponseEntity<?> createProduct(CreateProductInput body) {
        final var aCommand = CreateProductCommand.with(
                body.name(),
                body.description(),
                body.price(),
                body.quantity(),
                body.categoryId(),
                body.colorName(),
                body.sizeName(),
                body.weight(),
                body.height(),
                body.width(),
                body.depth()
        );

        final var aResult = this.createProductUseCase.execute(aCommand);

        return aResult.isLeft()
                ? ResponseEntity.unprocessableEntity().body(aResult.getLeft())
                : ResponseEntity.status(HttpStatus.CREATED).body(aResult.getRight());
    }

    @Override
    public ResponseEntity<?> uploadProductImageByType(String id, String type, MultipartFile media) {
        final var aType = ProductImageType.of(type)
                .orElseThrow(() -> DomainException
                        .with(new Error("type %s was not found".formatted(type))));

        final var aCommand = UploadProductImageCommand.with(
                id, ProductImageResource.with(ResourceOf.with(media), aType));

        final var aResult = this.uploadProductImageUseCase.execute(aCommand);

        return ResponseEntity
                .created(URI.create("/products/%s/medias/%s".formatted(id, type)))
                .body(aResult);
    }
}
