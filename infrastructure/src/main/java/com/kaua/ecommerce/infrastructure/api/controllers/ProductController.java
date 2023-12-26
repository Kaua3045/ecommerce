package com.kaua.ecommerce.infrastructure.api.controllers;

import com.kaua.ecommerce.application.usecases.product.create.CreateProductCommand;
import com.kaua.ecommerce.application.usecases.product.create.CreateProductUseCase;
import com.kaua.ecommerce.infrastructure.api.ProductAPI;
import com.kaua.ecommerce.infrastructure.product.models.CreateProductInput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController implements ProductAPI {

    private final CreateProductUseCase createProductUseCase;

    public ProductController(final CreateProductUseCase createProductUseCase) {
        this.createProductUseCase = createProductUseCase;
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
}
