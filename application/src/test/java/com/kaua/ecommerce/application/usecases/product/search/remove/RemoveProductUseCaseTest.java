package com.kaua.ecommerce.application.usecases.product.search.remove;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.MediaResourceGateway;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.application.usecases.product.search.save.SaveProductUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.product.*;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.eq;

public class RemoveProductUseCaseTest extends UseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Mock
    private SearchGateway<Product> productSearchGateway;

    @InjectMocks
    private RemoveProductUseCase useCase;

    @Test
    void givenAValidProductId_whenCallRemove_shouldRemoveProduct() {
        final var aProduct = Fixture.Products.book();
        aProduct.changeBannerImage(Fixture.Products.productImage(ProductImageType.BANNER));
        aProduct.addImage(Fixture.Products.productImage(ProductImageType.BANNER));

        Mockito.when(productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.doNothing().when(mediaResourceGateway).clearImages(Mockito.any());
        Mockito.doNothing().when(mediaResourceGateway).clearImage(Mockito.any());
        Mockito.doNothing().when(productGateway).delete(Mockito.any());
        Mockito.doNothing().when(productSearchGateway).deleteById(Mockito.any());

        this.useCase.execute(aProduct.getId().getValue());

        Mockito.verify(productGateway, Mockito.times(1)).findById(Mockito.any());
        Mockito.verify(mediaResourceGateway, Mockito.times(1)).clearImages(Mockito.any());
        Mockito.verify(mediaResourceGateway, Mockito.times(1)).clearImage(Mockito.any());
        Mockito.verify(productGateway, Mockito.times(1)).delete(Mockito.any());
        Mockito.verify(productSearchGateway, Mockito.times(1)).deleteById(Mockito.any());
    }

    @Test
    void givenAValidProductIdButNotContainsImages_whenCallRemove_shouldRemoveProduct() {
        final var aProduct = Fixture.Products.book();

        Mockito.when(productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.doNothing().when(productGateway).delete(Mockito.any());
        Mockito.doNothing().when(productSearchGateway).deleteById(Mockito.any());

        this.useCase.execute(aProduct.getId().getValue());

        Mockito.verify(productGateway, Mockito.times(1)).findById(Mockito.any());
        Mockito.verify(mediaResourceGateway, Mockito.times(0)).clearImages(Mockito.any());
        Mockito.verify(mediaResourceGateway, Mockito.times(0)).clearImage(Mockito.any());
        Mockito.verify(productGateway, Mockito.times(1)).delete(Mockito.any());
        Mockito.verify(productSearchGateway, Mockito.times(1)).deleteById(Mockito.any());
    }

    @Test
    void givenAnInvalidProductId_whenCallRemove_shouldBeOk() {
        final var aProductId = "invalid-id";

        Mockito.when(productGateway.findById(Mockito.any())).thenReturn(Optional.empty());
        Mockito.doNothing().when(productSearchGateway).deleteById(Mockito.any());

        this.useCase.execute(aProductId);

        Mockito.verify(productGateway, Mockito.times(1)).findById(Mockito.any());
        Mockito.verify(mediaResourceGateway, Mockito.times(0)).clearImages(Mockito.any());
        Mockito.verify(mediaResourceGateway, Mockito.times(0)).clearImage(Mockito.any());
        Mockito.verify(productGateway, Mockito.times(0)).delete(Mockito.any());
        Mockito.verify(productSearchGateway, Mockito.times(1)).deleteById(Mockito.any());
    }
}
