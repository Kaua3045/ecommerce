package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductColor;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductColorJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductColorJpaRepository;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class ProductMySQLGateway implements ProductGateway {

    private final ProductJpaRepository productRepository;
    private final ProductColorJpaRepository productColorRepository;

    public ProductMySQLGateway(
            final ProductJpaRepository productRepository,
            final ProductColorJpaRepository productColorRepository
    ) {
        this.productRepository = Objects.requireNonNull(productRepository);
        this.productColorRepository = Objects.requireNonNull(productColorRepository);
    }

    @Override
    public Product create(Product aProduct) {
        this.productRepository.save(ProductJpaEntity.toEntity(aProduct));
        return aProduct;
    }

    @Override
    public Optional<ProductColor> findColorByName(String aColorName) {
        return this.productColorRepository.findByColorIgnoreCase(aColorName)
                .map(ProductColorJpaEntity::toDomain);
    }
}
