package com.kaua.ecommerce.infrastructure.configurations.usecases;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.gateways.*;
import com.kaua.ecommerce.application.usecases.product.attributes.add.AddProductAttributesUseCase;
import com.kaua.ecommerce.application.usecases.product.attributes.add.DefaultAddProductAttributesUseCase;
import com.kaua.ecommerce.application.usecases.product.attributes.remove.DefaultRemoveProductAttributesUseCase;
import com.kaua.ecommerce.application.usecases.product.attributes.remove.RemoveProductAttributesUseCase;
import com.kaua.ecommerce.application.usecases.product.create.CreateProductUseCase;
import com.kaua.ecommerce.application.usecases.product.create.DefaultCreateProductUseCase;
import com.kaua.ecommerce.application.usecases.product.delete.DefaultDeleteProductUseCase;
import com.kaua.ecommerce.application.usecases.product.delete.DeleteProductUseCase;
import com.kaua.ecommerce.application.usecases.product.media.remove.DefaultRemoveProductImageUseCase;
import com.kaua.ecommerce.application.usecases.product.media.remove.RemoveProductImageUseCase;
import com.kaua.ecommerce.application.usecases.product.media.upload.DefaultUploadProductImageUseCase;
import com.kaua.ecommerce.application.usecases.product.media.upload.UploadProductImageUseCase;
import com.kaua.ecommerce.application.usecases.product.retrieve.get.DefaultGetProductByIdUseCase;
import com.kaua.ecommerce.application.usecases.product.retrieve.get.GetProductByIdUseCase;
import com.kaua.ecommerce.application.usecases.product.search.remove.RemoveProductUseCase;
import com.kaua.ecommerce.application.usecases.product.search.retrieve.list.ListProductsUseCase;
import com.kaua.ecommerce.application.usecases.product.search.save.SaveProductUseCase;
import com.kaua.ecommerce.application.usecases.product.update.DefaultUpdateProductUseCase;
import com.kaua.ecommerce.application.usecases.product.update.UpdateProductUseCase;
import com.kaua.ecommerce.application.usecases.product.update.status.DefaultUpdateProductStatusUseCase;
import com.kaua.ecommerce.application.usecases.product.update.status.UpdateProductStatusUseCase;
import com.kaua.ecommerce.domain.product.Product;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class ProductUseCaseConfig {

    private final ProductGateway productGateway;
    private final CategoryGateway categoryGateway;
    private final MediaResourceGateway mediaResourceGateway;
    private final SearchGateway<Product> productSearchGateway;
    private final TransactionManager transactionManager;
    private final EventPublisher eventPublisher;

    public ProductUseCaseConfig(
            final ProductGateway productGateway,
            final CategoryGateway categoryGateway,
            final MediaResourceGateway mediaResourceGateway,
            final SearchGateway<Product> productSearchGateway,
            final TransactionManager transactionManager,
            final EventPublisher eventPublisher
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
        this.productSearchGateway = Objects.requireNonNull(productSearchGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Bean
    public CreateProductUseCase createProductUseCase() {
        return new DefaultCreateProductUseCase(productGateway, categoryGateway, transactionManager, eventPublisher);
    }

    @Bean
    public UploadProductImageUseCase updateProductImageUseCase() {
        return new DefaultUploadProductImageUseCase(productGateway, mediaResourceGateway, transactionManager, eventPublisher);
    }

    @Bean
    public RemoveProductImageUseCase removeProductImageUseCase() {
        return new DefaultRemoveProductImageUseCase(productGateway, mediaResourceGateway, transactionManager, eventPublisher);
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase() {
        return new DefaultUpdateProductUseCase(productGateway, categoryGateway, transactionManager, eventPublisher);
    }

    @Bean
    public UpdateProductStatusUseCase updateProductStatusUseCase() {
        return new DefaultUpdateProductStatusUseCase(productGateway, transactionManager, eventPublisher);
    }

    @Bean
    public DeleteProductUseCase deleteProductUseCase() {
        return new DefaultDeleteProductUseCase(productGateway, transactionManager, eventPublisher);
    }

    @Bean
    public GetProductByIdUseCase getProductByIdUseCase() {
        return new DefaultGetProductByIdUseCase(productGateway);
    }

    @Bean
    public AddProductAttributesUseCase addProductAttributesUseCase() {
        return new DefaultAddProductAttributesUseCase(productGateway, transactionManager, eventPublisher);
    }

    @Bean
    public RemoveProductAttributesUseCase removeProductAttributesUseCase() {
        return new DefaultRemoveProductAttributesUseCase(productGateway, transactionManager, eventPublisher);
    }

    @Bean
    public SaveProductUseCase saveProductUseCase() {
        return new SaveProductUseCase(productSearchGateway);
    }

    @Bean
    public RemoveProductUseCase removeProductUseCase() {
        return new RemoveProductUseCase(productGateway, mediaResourceGateway, productSearchGateway);
    }

    @Bean
    public ListProductsUseCase listProductsUseCase() {
        return new ListProductsUseCase(productSearchGateway);
    }
}
