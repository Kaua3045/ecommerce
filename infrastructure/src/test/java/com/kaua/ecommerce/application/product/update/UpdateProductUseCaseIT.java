package com.kaua.ecommerce.application.product.update;

import com.kaua.ecommerce.application.usecases.product.update.UpdateProductCommand;
import com.kaua.ecommerce.application.usecases.product.update.UpdateProductUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@IntegrationTest
public class UpdateProductUseCaseIT {

    @Autowired
    private UpdateProductUseCase updateProductUseCase;

    @Autowired
    private ProductJpaEntityRepository productRepository;

    @Autowired
    private CategoryJpaEntityRepository categoryRepository;

    @Test
    void givenAValidValues_whenCallsUpdateProductUseCase_thenProductShouldBeUpdated() {
        final var aProduct = Fixture.Products.tshirt();
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));
        this.productRepository.save(ProductJpaEntity.toEntity(aProduct));

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = new BigDecimal("590.97");
        final var aCategoryId = aCategory.getId().getValue();

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Assertions.assertEquals(1, this.productRepository.count());

        final var aResult = this.updateProductUseCase.execute(aCommand).getRight();

        Assertions.assertEquals(1, this.productRepository.count());

        Assertions.assertNotNull(aResult.id());

        final var aPersistedProduct = this.productRepository.findById(aResult.id()).get();

        Assertions.assertEquals(aResult.id(), aPersistedProduct.getId());
        Assertions.assertEquals(aName, aPersistedProduct.getName());
        Assertions.assertEquals(aDescription, aPersistedProduct.getDescription());
        Assertions.assertEquals(aPrice, aPersistedProduct.getPrice());
        Assertions.assertEquals(aCategoryId, aPersistedProduct.getCategoryId());
        Assertions.assertTrue(aPersistedProduct.getBannerImage().isEmpty());
        Assertions.assertTrue(aPersistedProduct.getImages().isEmpty());
        Assertions.assertEquals(1, aPersistedProduct.getAttributes().size());
        Assertions.assertNotNull(aPersistedProduct.getCreatedAt());
        Assertions.assertNotNull(aPersistedProduct.getUpdatedAt());
    }

    @Test
    void givenAnInvalidNameLengthLessThan3_whenCallsUpdateProductUseCase_shouldReturnDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));
        this.productRepository.save(ProductJpaEntity.toEntity(aProduct));

        final var aName = "Pr ";
        final String aDescription = null;
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = aCategory.getId().getValue();

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorCount = 1;

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        final var aResult = this.updateProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(1, this.productRepository.count());
    }
}
