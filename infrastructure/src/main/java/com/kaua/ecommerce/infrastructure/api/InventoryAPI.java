package com.kaua.ecommerce.infrastructure.api;

import com.kaua.ecommerce.infrastructure.inventory.models.CreateInventoryInput;
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
