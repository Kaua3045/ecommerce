package com.kaua.ecommerce.infrastructure.api;

import com.kaua.ecommerce.infrastructure.customer.models.UpdateCustomerAddressInput;
import com.kaua.ecommerce.infrastructure.customer.models.UpdateCustomerCpfInput;
import com.kaua.ecommerce.infrastructure.customer.models.UpdateCustomerTelephoneInput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Customer")
@RequestMapping(value = "customers")
public interface CustomerAPI {

    @GetMapping(
            value = "{accountId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get customer by account id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "20", description = "Customer retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Customer was not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> getCustomer(
            @PathVariable String accountId,
            @RequestParam(name = "locale", defaultValue = "BR") String locale);

    @PatchMapping(
            value = "{accountId}/cpf",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update customer cpf by account id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer was not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> updateCustomerCpf(@PathVariable String accountId, @RequestBody UpdateCustomerCpfInput body);

    @PatchMapping(
            value = "{accountId}/telephone",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update customer telephone by account id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer was not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> updateCustomerTelephone(@PathVariable String accountId, @RequestBody UpdateCustomerTelephoneInput body);

    @PatchMapping(
            value = "{accountId}/address",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update customer address by account id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer was not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> updateCustomerAddress(@PathVariable String accountId, @RequestBody UpdateCustomerAddressInput body);
}
