package com.kaua.ecommerce.application.usecases.inventory.retrieve.list;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

public class ListInventoriesByProductIdUseCaseTest extends UseCaseTest {

    @Mock
    private InventoryGateway inventoryGateway;

    @InjectMocks
    private DefaultListInventoriesByProductIdUseCase useCase;

    @Test
    void givenAValidProductIdAndQuery_whenCallListInventoriesByProductIdUseCase_shouldReturnAInventories() {
        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aInventories = List.of(
                aInventory,
                Inventory.newInventory("1", "abablabla", 10));

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTotalPages = 1;
        final var aTerms = "";
        final var aSort = "sku";
        final var aDirection = "asc";

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);
        final var aPagination = new Pagination<>(aPage, aPerPage, aTotalPages, aInventories.size(), aInventories);

        final var aItemsCount = 2;
        final var aResult = aPagination.map(ListInventoriesByProductIdOutput::from);

        Mockito.when(inventoryGateway.findAllByProductId(aQuery, "1")).thenReturn(aPagination);

        final var actualResult = this.useCase.execute(ListInventoriesByProductIdCommand.with("1", aQuery));

        Assertions.assertEquals(aItemsCount, actualResult.totalItems());
        Assertions.assertEquals(aResult, actualResult);
        Assertions.assertEquals(aPage, actualResult.currentPage());
        Assertions.assertEquals(aPerPage, actualResult.perPage());
        Assertions.assertEquals(aTotalPages, actualResult.totalPages());
        Assertions.assertEquals(aInventories.size(), actualResult.items().size());
    }

    @Test
    void givenAValidProductIdAndQueryButHasNoData_whenCallListInventoriesByProductIdUseCase_shouldReturnEmptyInventories() {
        final var aInventories = List.<Inventory>of();

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTotalPages = 1;
        final var aTerms = "";
        final var aSort = "sku";
        final var aDirection = "asc";

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);
        final var aPagination = new Pagination<>(aPage, aPerPage, aTotalPages, aInventories.size(), aInventories);

        final var aItemsCount = 0;
        final var aResult = aPagination.map(ListInventoriesByProductIdOutput::from);

        Mockito.when(inventoryGateway.findAllByProductId(aQuery, "1")).thenReturn(aPagination);

        final var actualResult = this.useCase.execute(ListInventoriesByProductIdCommand.with("1", aQuery));

        Assertions.assertEquals(aItemsCount, actualResult.totalItems());
        Assertions.assertEquals(aResult, actualResult);
        Assertions.assertEquals(aPage, actualResult.currentPage());
        Assertions.assertEquals(aPerPage, actualResult.perPage());
        Assertions.assertEquals(aTotalPages, actualResult.totalPages());
        Assertions.assertEquals(aInventories.size(), actualResult.items().size());
    }
}
