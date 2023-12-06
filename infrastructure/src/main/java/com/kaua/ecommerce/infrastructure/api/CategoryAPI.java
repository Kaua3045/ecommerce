package com.kaua.ecommerce.infrastructure.api;

import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.infrastructure.category.models.CreateCategoryInput;
import com.kaua.ecommerce.infrastructure.category.models.ListCategoriesResponse;
import com.kaua.ecommerce.infrastructure.category.models.UpdateCategoryInput;
import com.kaua.ecommerce.infrastructure.category.models.UpdateSubCategoriesInput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Category")
@RequestMapping(value = "categories")
public interface CategoryAPI {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new root category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> createCategory(@RequestBody CreateCategoryInput body);

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get all root categories with sub categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found successfully"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    Pagination<ListCategoriesResponse> listCategories(
            @RequestParam(name = "search", required = false, defaultValue = "") final String search,
            @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
            @RequestParam(name = "sort", required = false, defaultValue = "name") final String sort,
            @RequestParam(name = "dir", required = false, defaultValue = "asc") final String direction
    );

    @PatchMapping(
            value = "{categoryId}/sub",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Add sub category in root category id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "404", description = "Root category was not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> updateSubCategories(@PathVariable String categoryId, @RequestBody UpdateSubCategoriesInput body);

    @PatchMapping(
            value = "{categoryId}/sub/{subCategoryId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Add sub category in sub category by root category id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "404", description = "Root category was not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> updateSubSubCategories(@PathVariable String categoryId, @PathVariable String subCategoryId, @RequestBody UpdateSubCategoriesInput body);

    @PatchMapping(
            value = "update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update name and description root category by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category was not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> updateRootCategory(@PathVariable String id, @RequestBody UpdateCategoryInput body);

    @PatchMapping(
            value = "{categoryId}/update/{subCategoryId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update sub category by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category was not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> updateSubCategory(@PathVariable String categoryId, @PathVariable String subCategoryId, @RequestBody UpdateCategoryInput body);

    @DeleteMapping(value = "delete/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete root category by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    void deleteRootCategory(@PathVariable String categoryId);

    @DeleteMapping(value = "{categoryId}/delete/{subCategoryId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete category by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    void deleteSubCategory(@PathVariable String categoryId, @PathVariable String subCategoryId);
}
