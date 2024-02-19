package com.kaua.ecommerce.infrastructure.api.controllers;

import com.kaua.ecommerce.application.usecases.product.attributes.add.AddProductAttributesCommand;
import com.kaua.ecommerce.application.usecases.product.attributes.add.AddProductAttributesUseCase;
import com.kaua.ecommerce.application.usecases.product.attributes.remove.RemoveProductAttributesCommand;
import com.kaua.ecommerce.application.usecases.product.attributes.remove.RemoveProductAttributesUseCase;
import com.kaua.ecommerce.application.usecases.product.create.CreateProductUseCase;
import com.kaua.ecommerce.application.usecases.product.delete.DeleteProductUseCase;
import com.kaua.ecommerce.application.usecases.product.media.remove.RemoveProductImageCommand;
import com.kaua.ecommerce.application.usecases.product.media.remove.RemoveProductImageUseCase;
import com.kaua.ecommerce.application.usecases.product.media.upload.UploadProductImageCommand;
import com.kaua.ecommerce.application.usecases.product.media.upload.UploadProductImageUseCase;
import com.kaua.ecommerce.application.usecases.product.retrieve.get.GetProductByIdUseCase;
import com.kaua.ecommerce.application.usecases.product.search.retrieve.list.ListProductsUseCase;
import com.kaua.ecommerce.application.usecases.product.update.UpdateProductCommand;
import com.kaua.ecommerce.application.usecases.product.update.UpdateProductUseCase;
import com.kaua.ecommerce.application.usecases.product.update.status.UpdateProductStatusCommand;
import com.kaua.ecommerce.application.usecases.product.update.status.UpdateProductStatusUseCase;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductImageResource;
import com.kaua.ecommerce.domain.product.ProductImageType;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.infrastructure.api.ProductAPI;
import com.kaua.ecommerce.infrastructure.product.models.*;
import com.kaua.ecommerce.infrastructure.product.presenter.ProductApiPresenter;
import com.kaua.ecommerce.infrastructure.utils.LogControllerResult;
import com.kaua.ecommerce.infrastructure.utils.ResourceOf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ProductController implements ProductAPI {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final CreateProductUseCase createProductUseCase;
    private final UploadProductImageUseCase uploadProductImageUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductStatusUseCase updateProductStatusUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final RemoveProductImageUseCase removeProductImageUseCase;
    private final AddProductAttributesUseCase addProductAttributesUseCase;
    private final RemoveProductAttributesUseCase removeProductAttributesUseCase;

    public ProductController(
            final CreateProductUseCase createProductUseCase,
            final UploadProductImageUseCase uploadProductImageUseCase,
            final UpdateProductUseCase updateProductUseCase,
            final DeleteProductUseCase deleteProductUseCase,
            final UpdateProductStatusUseCase updateProductStatusUseCase,
            final GetProductByIdUseCase getProductByIdUseCase,
            final ListProductsUseCase listProductsUseCase,
            final RemoveProductImageUseCase removeProductImageUseCase,
            final AddProductAttributesUseCase addProductAttributesUseCase,
            final RemoveProductAttributesUseCase removeProductAttributesUseCase
    ) {
        this.createProductUseCase = createProductUseCase;
        this.uploadProductImageUseCase = uploadProductImageUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.updateProductStatusUseCase = updateProductStatusUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.listProductsUseCase = listProductsUseCase;
        this.removeProductImageUseCase = removeProductImageUseCase;
        this.addProductAttributesUseCase = addProductAttributesUseCase;
        this.removeProductAttributesUseCase = removeProductAttributesUseCase;
    }

    @Override
    public ResponseEntity<?> createProduct(CreateProductInput body) {
        final var aCommand = body.toCommand();

        final var aResult = this.createProductUseCase.execute(aCommand);

        LogControllerResult.logResult(
                log,
                Product.class,
                "createProduct",
                aResult);

        return aResult.isLeft()
                ? ResponseEntity.unprocessableEntity().body(aResult.getLeft())
                : ResponseEntity.status(HttpStatus.CREATED).body(aResult.getRight());
    }

    @Override
    public Pagination<ListProductsResponse> listProducts(String search, int page, int perPage, String sort, String direction) {
        final var aQuery = new SearchQuery(page, perPage, search, sort, direction);
        return this.listProductsUseCase.execute(aQuery)
                .map(ProductApiPresenter::present);
    }

    @Override
    public GetProductResponse getProductById(String id) {
        return ProductApiPresenter.present(this.getProductByIdUseCase.execute(id));
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

        LogControllerResult.logResult(
                log,
                Product.class,
                "uploadProductImageByType",
                aResult);

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

        LogControllerResult.logResult(
                log,
                Product.class,
                "updateProduct",
                aResult);

        return aResult.isLeft()
                ? ResponseEntity.unprocessableEntity().body(aResult.getLeft())
                : ResponseEntity.ok(aResult.getRight());
    }

    @Override
    public ResponseEntity<?> updateProductStatus(String id, String status) {
        final var aResult = this.updateProductStatusUseCase.execute(UpdateProductStatusCommand.with(
                id, status));

        LogControllerResult.logResult(
                log,
                Product.class,
                "updateProductStatus",
                aResult);

        return ResponseEntity.status(HttpStatus.OK).body(aResult);
    }

    @Override
    public ResponseEntity<?> addProductAttributes(String id, AddProductAttributesInput body) {
        final var aResult = this.addProductAttributesUseCase.execute(
                AddProductAttributesCommand.with(id, body.toCommandParams())
        );

        LogControllerResult.logResult(
                log,
                Product.class,
                "addProductAttributes",
                aResult);

        return ResponseEntity.ok(aResult);
    }

    @Override
    public void deleteProduct(String id) {
        this.deleteProductUseCase.execute(id);
        LogControllerResult.logResult(
                log,
                Product.class,
                "deleteProduct",
                id + " deleted");
    }

    @Override
    public void deleteProductImage(String id, String location) {
        this.removeProductImageUseCase.execute(RemoveProductImageCommand.with(id, location));
        LogControllerResult.logResult(
                log,
                Product.class,
                "deleteProductImage",
                location + " deleted");
    }

    @Override
    public void deleteProductAttribute(String id, String sku) {
        this.removeProductAttributesUseCase.execute(RemoveProductAttributesCommand.with(id, sku));
        LogControllerResult.logResult(
                log,
                Product.class,
                "deleteProductAttribute",
                sku + " deleted");
    }
}
