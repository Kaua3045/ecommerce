package com.kaua.ecommerce.infrastructure.api;

import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.infrastructure.coupon.models.ApplyCouponInput;
import com.kaua.ecommerce.infrastructure.coupon.models.CreateCouponInput;
import com.kaua.ecommerce.infrastructure.coupon.models.ListCouponsResponse;
import com.kaua.ecommerce.infrastructure.coupon.models.UpdateCouponInput;
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

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all coupons")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found successfully"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    Pagination<ListCouponsResponse> listCoupons(
            @RequestParam(name = "search", required = false, defaultValue = "") final String search,
            @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
            @RequestParam(name = "sort", required = false, defaultValue = "code") final String sort,
            @RequestParam(name = "dir", required = false, defaultValue = "asc") final String direction,
            @RequestParam(name = "startDate", required = false, defaultValue = "") final String startDate,
            @RequestParam(name = "endDate", required = false, defaultValue = "") final String endDate
    );


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

    @PatchMapping(
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update a coupon by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "404", description = "Coupon not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> updateCouponById(@PathVariable String id, @RequestBody UpdateCouponInput body);

    @DeleteMapping(
            value = "slots/{code}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Apply a coupon by it's code and remove a slot from it's slots list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Applied successfully"),
            @ApiResponse(responseCode = "404", description = "Coupon not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> applyCoupon(@PathVariable String code, @RequestBody ApplyCouponInput input);

    @DeleteMapping(value = "{id}")
    @Operation(summary = "Delete a coupon by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    @ResponseStatus(HttpStatus.OK)
    void deleteCoupon(@PathVariable String id);
}
