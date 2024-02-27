package com.kaua.ecommerce.application.product.attributes.add;

import com.kaua.ecommerce.application.usecases.product.attributes.add.AddProductAttributesCommand;
import com.kaua.ecommerce.application.usecases.product.attributes.add.AddProductAttributesCommandParams;
import com.kaua.ecommerce.application.usecases.product.attributes.add.AddProductAttributesUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@IntegrationTest
public class AddProductAttributesUseCaseIT {

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private AddProductAttributesUseCase addProductAttributesUseCase;

    @Test
    void givenAValidValues_whenCallsAddProductAttributes_shouldAddAttribute() {
        // given
        final var aProduct = Fixture.Products.tshirt();
        final var aProductId = aProduct.getId().getValue();

        this.productJpaRepository.save(ProductJpaEntity.toEntity(aProduct));

        Assertions.assertEquals(1, this.productJpaRepository.count());

        final var aCommand = AddProductAttributesCommand.with(
                aProductId,
                List.of(new AddProductAttributesCommandParams(
                        "color",
                        "size",
                        1.0,
                        1.0,
                        1.0,
                        1.0,
                        3
                ))
        );

        // when
        final var actualResult = this.addProductAttributesUseCase.execute(aCommand);

        // then
        Assertions.assertEquals(aProductId, actualResult.productId());

        final var aProductEntity = this.productJpaRepository.findById(aProductId).get();

        Assertions.assertEquals(2, aProductEntity.getAttributes().size());
    }
}
