package com.kaua.ecommerce.infrastructure.api;

import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.infrastructure.inventory.models.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Inventory")
@RequestMapping(value = "v1/inventories")
public interface InventoryAPI {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new inventory for sku")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> createInventory(@RequestBody CreateInventoryInput body);

    @GetMapping(
            value = "list/{productId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get all inventories by productId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found successfully"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    Pagination<ListInventoriesResponse> listInventoriesByProductId(
            @PathVariable String productId,
            @RequestParam(name = "search", required = false, defaultValue = "") final String search,
            @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
            @RequestParam(name = "sort", required = false, defaultValue = "sku") final String sort,
            @RequestParam(name = "dir", required = false, defaultValue = "asc") final String direction
    );

    @GetMapping(
            value = "{sku}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get inventory by it's sku")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory found successfully"),
            @ApiResponse(responseCode = "404", description = "Inventory was not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    GetInventoryResponse getInventoryBySku(@PathVariable String sku);

    @PostMapping(
            value = "/rollback/{productId}/{sku}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Rollback inventory by sku and productId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rollback successfully"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    @ResponseStatus(HttpStatus.OK)
    void rollbackInventoryBySkuAndProductId(@PathVariable String productId, @PathVariable String sku);

    @PatchMapping(
            value = "/increase/{sku}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Increase inventory quantity by sku")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Increased successfully"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> increaseInventoryQuantityBySku(@PathVariable String sku, @RequestBody IncreaseInventoryQuantityInput body);

    @PatchMapping(
            value = "/decrease/{sku}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Decrease inventory quantity by sku")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Decreased successfully"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> decreaseInventoryQuantityBySku(@PathVariable String sku, @RequestBody DecreaseInventoryQuantityInput body);


    @DeleteMapping(value = "{productId}")
    @Operation(summary = "Delete a inventories by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    @ResponseStatus(HttpStatus.OK)
    void deleteInventoriesByProductId(@PathVariable String productId);

    @DeleteMapping(value = "/sku/{sku}")
    @Operation(summary = "Delete a inventory by sku")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    @ResponseStatus(HttpStatus.OK)
    void deleteInventoryBySku(@PathVariable String sku);
}
