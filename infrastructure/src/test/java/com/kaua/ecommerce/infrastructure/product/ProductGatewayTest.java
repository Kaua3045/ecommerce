package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductAttributes;
import com.kaua.ecommerce.domain.product.ProductColor;
import com.kaua.ecommerce.domain.product.ProductSize;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DatabaseGatewayTest
public class ProductGatewayTest {

    @Autowired
    private ProductMySQLGateway productGateway;

    @Autowired
    private ProductJpaRepository productRepository;

    @Test
    void givenAValidProduct_whenCallCreate_shouldPersistProduct() {
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = 10.0;
        final var aQuantity = 10;
        final var aCategoryId = CategoryID.unique();
        final var aColor = "Red";
        final var aSize = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var aProduct = Product.newProduct(
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId,
                ProductAttributes.create(
                        ProductColor.with(aColor),
                        ProductSize.with(aSize, aWeight, aHeight, aWidth, aDepth),
                        aName
                )
        );

        Assertions.assertEquals(0, this.productRepository.count());

        final var createdProduct = this.productGateway.create(aProduct);

        Assertions.assertEquals(1, this.productRepository.count());

        final var aPersistedProduct = this.productRepository.findById(createdProduct.getId().getValue()).get();

        Assertions.assertEquals(aProduct.getId().getValue(), aPersistedProduct.getId());
        Assertions.assertEquals(aProduct.getName(), aPersistedProduct.getName());
        Assertions.assertEquals(aProduct.getDescription(), aPersistedProduct.getDescription());
        Assertions.assertEquals(aProduct.getPrice(), aPersistedProduct.getPrice());
        Assertions.assertEquals(aProduct.getQuantity(), aPersistedProduct.getQuantity());
        Assertions.assertEquals(aProduct.getCategoryId().getValue(), aPersistedProduct.getCategoryId());
        Assertions.assertNotNull(aPersistedProduct.getAttributes().stream().findFirst().get().getId());
        Assertions.assertNotNull(aPersistedProduct.getAttributes().stream().findFirst().get().getProduct());
        Assertions.assertEquals(aColor, aPersistedProduct.getAttributes().stream().findFirst().get().getColor().getColor());
        Assertions.assertEquals(aSize, aPersistedProduct.getAttributes().stream().findFirst().get().getSize().getSize());
        Assertions.assertEquals(aProduct.getCreatedAt(), aPersistedProduct.getCreatedAt());
        Assertions.assertEquals(aProduct.getUpdatedAt(), aPersistedProduct.getUpdatedAt());
    }
}
