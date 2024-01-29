package com.kaua.ecommerce.infrastructure.api.controllers;

import com.kaua.ecommerce.application.usecases.product.create.CreateProductUseCase;
import com.kaua.ecommerce.application.usecases.product.delete.DeleteProductUseCase;
import com.kaua.ecommerce.application.usecases.product.media.upload.UploadProductImageCommand;
import com.kaua.ecommerce.application.usecases.product.media.upload.UploadProductImageUseCase;
import com.kaua.ecommerce.application.usecases.product.update.UpdateProductCommand;
import com.kaua.ecommerce.application.usecases.product.update.UpdateProductUseCase;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.product.ProductImageResource;
import com.kaua.ecommerce.domain.product.ProductImageType;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.infrastructure.api.ProductAPI;
import com.kaua.ecommerce.infrastructure.product.models.CreateProductInput;
import com.kaua.ecommerce.infrastructure.product.models.UpdateProductInput;
import com.kaua.ecommerce.infrastructure.utils.ResourceOf;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ProductController implements ProductAPI {

    private final CreateProductUseCase createProductUseCase;
    private final UploadProductImageUseCase uploadProductImageUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    public ProductController(
            final CreateProductUseCase createProductUseCase,
            final UploadProductImageUseCase uploadProductImageUseCase,
            final UpdateProductUseCase updateProductUseCase,
            final DeleteProductUseCase deleteProductUseCase
    ) {
        this.createProductUseCase = createProductUseCase;
        this.uploadProductImageUseCase = uploadProductImageUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
    }

    @Override
    public ResponseEntity<?> createProduct(CreateProductInput body) {
        final var aCommand = body.toCommand();

        final var aResult = this.createProductUseCase.execute(aCommand);

        return aResult.isLeft()
                ? ResponseEntity.unprocessableEntity().body(aResult.getLeft())
                : ResponseEntity.status(HttpStatus.CREATED).body(aResult.getRight());
    }

    @Override
    public ResponseEntity<?> uploadProductImageByType(String id, String type, List<MultipartFile> media) {
        final var aType = ProductImageType.of(type)
                .orElseThrow(() -> DomainException
                        .with(new Error("type %s was not found".formatted(type))));

        final var aProductImageResource = media.stream().map(it -> ProductImageResource
                        .with(ResourceOf.with(it), aType))
                .toList();

        final var aCommand = UploadProductImageCommand.with(
                id, aProductImageResource);

        final var aResult = this.uploadProductImageUseCase.execute(aCommand);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(aResult);
    }

    @Override
    public ResponseEntity<?> updateProduct(String id, UpdateProductInput body) {
        final var aCommand = UpdateProductCommand.with(
                id,
                body.name(),
                body.description(),
                body.price(),
                body.quantity(),
                body.categoryId()
        );

        final var aResult = this.updateProductUseCase.execute(aCommand);

        return aResult.isLeft()
                ? ResponseEntity.unprocessableEntity().body(aResult.getLeft())
                : ResponseEntity.ok(aResult.getRight());
    }

    @Override
    public void deleteProduct(String id) {
        this.deleteProductUseCase.execute(id);
    }
}
