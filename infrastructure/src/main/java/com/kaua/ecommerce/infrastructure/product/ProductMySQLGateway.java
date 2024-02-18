package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductColor;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductColorJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductColorJpaRepository;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Component
public class ProductMySQLGateway implements ProductGateway {

    private static final Logger log = LoggerFactory.getLogger(ProductMySQLGateway.class);

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
        final var aResult = save(aProduct);
        log.info("inserted product: {}", aResult);
        return aResult;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Product> findById(String aProductID) {
        return this.productRepository.findById(aProductID)
                .map(ProductJpaEntity::toDomain);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ProductColor> findColorByName(String aColorName) {
        return this.productColorRepository.findByColorIgnoreCase(aColorName)
                .map(ProductColorJpaEntity::toDomain);
    }

    @Override
    public Product update(Product aProduct) {
        final var aResult = save(aProduct);
        log.info("updated product: {}", aResult);
        return aResult;
    }

    @Override
    public void delete(String aProductID) {
        this.productRepository.deleteById(aProductID);
        log.info("deleted product: {}", aProductID);
    }

    private Product save(final Product aProduct) {
        return this.productRepository.saveAndFlush(ProductJpaEntity
                .toEntity(aProduct)).toDomain();
    }
}
