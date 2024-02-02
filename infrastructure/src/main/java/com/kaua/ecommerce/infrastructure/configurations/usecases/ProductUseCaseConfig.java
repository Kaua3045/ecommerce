package com.kaua.ecommerce.infrastructure.configurations.usecases;

import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.application.gateways.MediaResourceGateway;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.application.usecases.product.create.CreateProductUseCase;
import com.kaua.ecommerce.application.usecases.product.create.DefaultCreateProductUseCase;
import com.kaua.ecommerce.application.usecases.product.delete.DefaultDeleteProductUseCase;
import com.kaua.ecommerce.application.usecases.product.delete.DeleteProductUseCase;
import com.kaua.ecommerce.application.usecases.product.media.upload.DefaultUploadProductImageUseCase;
import com.kaua.ecommerce.application.usecases.product.media.upload.UploadProductImageUseCase;
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

    public ProductUseCaseConfig(
            final ProductGateway productGateway,
            final CategoryGateway categoryGateway,
            final MediaResourceGateway mediaResourceGateway,
            final SearchGateway<Product> productSearchGateway
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
        this.productSearchGateway = Objects.requireNonNull(productSearchGateway);
    }

    @Bean
    public CreateProductUseCase createProductUseCase() {
        return new DefaultCreateProductUseCase(productGateway, categoryGateway);
    }

    @Bean
    public UploadProductImageUseCase updateProductImageUseCase() {
        return new DefaultUploadProductImageUseCase(productGateway, mediaResourceGateway);
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase() {
        return new DefaultUpdateProductUseCase(productGateway, categoryGateway);
    }

    @Bean
    public UpdateProductStatusUseCase updateProductStatusUseCase() {
        return new DefaultUpdateProductStatusUseCase(productGateway);
    }

    @Bean
    public DeleteProductUseCase deleteProductUseCase() {
        return new DefaultDeleteProductUseCase(productGateway);
    }

    @Bean
    public SaveProductUseCase saveProductUseCase() {
        return new SaveProductUseCase(productSearchGateway);
    }
}
