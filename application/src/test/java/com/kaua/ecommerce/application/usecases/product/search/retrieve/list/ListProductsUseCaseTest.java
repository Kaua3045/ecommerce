package com.kaua.ecommerce.application.usecases.product.search.retrieve.list;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductImageType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

public class ListProductsUseCaseTest extends UseCaseTest {

    @Mock
    private SearchGateway<Product> searchProductGateway;

    @InjectMocks
    private ListProductsUseCase useCase;

    @Test
    void givenAValidQuery_whenCallListProductsUseCase_shouldReturnAProducts() {
        final var aTshirt = Fixture.Products.tshirt();
        aTshirt.changeBannerImage(Fixture.Products.productImage(ProductImageType.BANNER));

        final var aProducts = List.of(
                aTshirt,
                Fixture.Products.book());

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTotalPages = 1;
        final var aTerms = "";
        final var aSort = "name";
        final var aDirection = "asc";

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);
        final var aPagination = new Pagination<>(aPage, aPerPage, aTotalPages, aProducts.size(), aProducts);

        final var aItemsCount = 2;
        final var aResult = aPagination.map(ListProductsOutput::from);

        Mockito.when(this.searchProductGateway.findAll(aQuery)).thenReturn(aPagination);

        final var actualResult = this.useCase.execute(aQuery);

        Assertions.assertEquals(aItemsCount, actualResult.totalItems());
        Assertions.assertEquals(aResult, actualResult);
        Assertions.assertEquals(aPage, actualResult.currentPage());
        Assertions.assertEquals(aPerPage, actualResult.perPage());
        Assertions.assertEquals(aTotalPages, actualResult.totalPages());
        Assertions.assertEquals(aProducts.size(), actualResult.items().size());
    }

    @Test
    void givenAValidQueryButHasNoData_whenCallListProductsUseCase_shouldReturnEmptyProducts() {
        final var aProducts = List.<Product>of();

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTotalPages = 1;
        final var aTerms = "";
        final var aSort = "name";
        final var aDirection = "asc";

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);
        final var aPagination = new Pagination<>(aPage, aPerPage, aTotalPages, aProducts.size(), aProducts);

        final var aItemsCount = 0;
        final var aResult = aPagination.map(ListProductsOutput::from);

        Mockito.when(this.searchProductGateway.findAll(aQuery)).thenReturn(aPagination);

        final var actualResult = this.useCase.execute(aQuery);

        Assertions.assertEquals(aItemsCount, actualResult.totalItems());
        Assertions.assertEquals(aResult, actualResult);
        Assertions.assertEquals(aPage, actualResult.currentPage());
        Assertions.assertEquals(aPerPage, actualResult.perPage());
        Assertions.assertEquals(aTotalPages, actualResult.totalPages());
        Assertions.assertEquals(aProducts.size(), actualResult.items().size());
    }
}
