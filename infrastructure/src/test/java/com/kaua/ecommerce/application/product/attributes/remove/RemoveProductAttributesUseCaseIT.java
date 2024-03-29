package com.kaua.ecommerce.application.product.attributes.remove;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.ProductInventoryGateway;
import com.kaua.ecommerce.application.usecases.product.attributes.remove.RemoveProductAttributesCommand;
import com.kaua.ecommerce.application.usecases.product.attributes.remove.RemoveProductAttributesUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
public class RemoveProductAttributesUseCaseIT {

    @Autowired
    private ProductJpaEntityRepository productJpaRepository;

    @MockBean
    private ProductInventoryGateway productInventoryGateway;

    @Autowired
    private RemoveProductAttributesUseCase removeProductAttributesUseCase;

    @Test
    void givenAValidValues_whenCallsRemoveProductAttributes_shouldRemoveAttribute() {
        // given
        final var aProduct = Fixture.Products.tshirt();
        final var aProductAttribute = Fixture.Products.productAttributes(aProduct.getName());
        aProduct.addAttribute(aProductAttribute);

        final var aProductId = aProduct.getId().getValue();
        final var aSku = aProductAttribute.getSku();

        this.productJpaRepository.save(ProductJpaEntity.toEntity(aProduct));

        Mockito.when(this.productInventoryGateway.deleteInventoryBySku(aSku))
                .thenReturn(Either.right(null));

        Assertions.assertEquals(1, this.productJpaRepository.count());

        final var aCommand = RemoveProductAttributesCommand.with(aProductId, aSku);

        // when
        Assertions.assertDoesNotThrow(() -> this.removeProductAttributesUseCase.execute(aCommand));

        // then
        final var aProductEntity = this.productJpaRepository.findById(aProductId).get();

        Assertions.assertEquals(1, aProductEntity.getAttributes().size());

        Mockito.verify(this.productInventoryGateway, Mockito.times(1)).deleteInventoryBySku(aSku);
    }
}
