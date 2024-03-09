package com.kaua.ecommerce.infrastructure.api;

import com.kaua.ecommerce.infrastructure.coupon.models.CreateCouponInput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Coupon")
@RequestMapping(value = "v1/coupons")
public interface CouponAPI {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new coupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> createCoupon(@RequestBody CreateCouponInput body);

    @GetMapping(
            value = "/validate/{code}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Validate a coupon by it's code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon success validated"),
            @ApiResponse(responseCode = "404", description = "Coupon not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> validateCouponByCode(@PathVariable String code);

    @PatchMapping(
            value = "activate/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Activate a coupon by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activated successfully"),
            @ApiResponse(responseCode = "404", description = "Coupon not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> activateCoupon(@PathVariable String id);

    @PatchMapping(
            value = "deactivate/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Deactivate a coupon by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Coupon not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> deactivateCoupon(@PathVariable String id);

    @DeleteMapping(
            value = "slots/{code}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Remove a coupon slot by it's code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Removed successfully"),
            @ApiResponse(responseCode = "404", description = "Coupon not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> removeCouponSlot(@PathVariable String code);

    @DeleteMapping(value = "{id}")
    @Operation(summary = "Delete a coupon by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    @ResponseStatus(HttpStatus.OK)
    void deleteCoupon(@PathVariable String id);
}
