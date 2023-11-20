package com.kaua.ecommerce.application.usecases.category.search.retrieve.list;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.application.usecases.category.search.retrieve.list.DefaultListCategoriesUseCase;
import com.kaua.ecommerce.application.usecases.category.search.retrieve.list.ListCategoriesOutput;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

public class ListCategoriesUseCaseTest extends UseCaseTest {

    @Mock
    private SearchGateway<Category> categoryGateway;

    @InjectMocks
    private DefaultListCategoriesUseCase useCase;

    @Test
    void givenAValidQuery_whenCallListCategoriesUseCase_shouldReturnACategories() {
        final var aTechCategory = Fixture.Categories.tech();
        final var aSubCategories = Fixture.Categories.makeSubCategories(2, aTechCategory);
        aTechCategory.addSubCategories(aSubCategories);
        aTechCategory.updateSubCategoriesLevel();

        final var aCategories = List.of(
                aTechCategory,
                Fixture.Categories.home());

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTotalPages = 1;
        final var aTerms = "";
        final var aSort = "name";
        final var aDirection = "asc";

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);
        final var aPagination = new Pagination<>(aPage, aPerPage, aTotalPages, aCategories.size(), aCategories);

        final var aItemsCount = 2;
        final var aResult = aPagination.map(ListCategoriesOutput::from);

        Mockito.when(this.categoryGateway.findAll(aQuery)).thenReturn(aPagination);

        final var actualResult = this.useCase.execute(aQuery);

        Assertions.assertEquals(aItemsCount, actualResult.totalItems());
        Assertions.assertEquals(aResult, actualResult);
        Assertions.assertEquals(aPage, actualResult.currentPage());
        Assertions.assertEquals(aPerPage, actualResult.perPage());
        Assertions.assertEquals(aTotalPages, actualResult.totalPages());
        Assertions.assertEquals(aCategories.size(), actualResult.items().size());
    }

    @Test
    void givenAValidQueryButHasNoData_whenCallListCategoriesUseCase_shouldReturnEmptyCategories() {
        final var aCategories = List.<Category>of();

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTotalPages = 1;
        final var aTerms = "";
        final var aSort = "name";
        final var aDirection = "asc";

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);
        final var aPagination = new Pagination<>(aPage, aPerPage, aTotalPages, aCategories.size(), aCategories);

        final var aItemsCount = 0;
        final var aResult = aPagination.map(ListCategoriesOutput::from);

        Mockito.when(this.categoryGateway.findAll(aQuery)).thenReturn(aPagination);

        final var actualResult = this.useCase.execute(aQuery);

        Assertions.assertEquals(aItemsCount, actualResult.totalItems());
        Assertions.assertEquals(aResult, actualResult);
        Assertions.assertEquals(aPage, actualResult.currentPage());
        Assertions.assertEquals(aPerPage, actualResult.perPage());
        Assertions.assertEquals(aTotalPages, actualResult.totalPages());
        Assertions.assertEquals(aCategories.size(), actualResult.items().size());
    }
}
