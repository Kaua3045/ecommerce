package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.gateways.responses.ProductDetails;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductColor;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductColorJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductColorJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Component
public class ProductMySQLGateway implements ProductGateway {

    private static final Logger log = LoggerFactory.getLogger(ProductMySQLGateway.class);

    private final ProductJpaEntityRepository productEntityRepository;
    private final ProductColorJpaEntityRepository productColorEntityRepository;

    public ProductMySQLGateway(
            final ProductJpaEntityRepository productEntityRepository,
            final ProductColorJpaEntityRepository productColorEntityRepository
    ) {
        this.productEntityRepository = Objects.requireNonNull(productEntityRepository);
        this.productColorEntityRepository = Objects.requireNonNull(productColorEntityRepository);
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
        return this.productEntityRepository.findById(aProductID)
                .map(ProductJpaEntity::toDomain);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ProductColor> findColorByName(String aColorName) {
        return this.productColorEntityRepository.findByColorIgnoreCase(aColorName)
                .map(ProductColorJpaEntity::toDomain);
    }

    @Override
    public Optional<ProductDetails> findProductDetailsBySku(String aSku) {
        return this.productEntityRepository.findFirstProductDetailsBySku(aSku);
    }

    @Override
    public Product update(Product aProduct) {
        final var aResult = save(aProduct);
        log.info("updated product: {}", aResult);
        return aResult;
    }

    @Override
    public void delete(String aProductID) {
        this.productEntityRepository.deleteById(aProductID);
        log.info("deleted product: {}", aProductID);
    }

    private Product save(final Product aProduct) {
        return this.productEntityRepository.saveAndFlush(ProductJpaEntity
                .toEntity(aProduct)).toDomain();
    }
}
