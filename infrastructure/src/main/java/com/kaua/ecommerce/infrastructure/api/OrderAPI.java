package com.kaua.ecommerce.infrastructure.api;

import com.kaua.ecommerce.application.usecases.order.retrieve.get.GetOrderByCodeOutput;
import com.kaua.ecommerce.infrastructure.order.models.CreateOrderInput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order")
@RequestMapping(value = "v1/orders")
public interface OrderAPI {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> createOrder(@RequestBody CreateOrderInput body);

    @GetMapping(
            value = "{code}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get order by code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found successfully"),
            @ApiResponse(responseCode = "404", description = "Order was not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    GetOrderByCodeOutput getOrderByCode(@PathVariable String code);
}
