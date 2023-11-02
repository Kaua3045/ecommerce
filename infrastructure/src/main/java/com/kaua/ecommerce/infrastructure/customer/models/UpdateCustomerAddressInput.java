package com.kaua.ecommerce.infrastructure.customer.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateCustomerAddressInput(
        @JsonProperty("street") String street,
        @JsonProperty("number") String number,
        @JsonProperty("complement") String complement,
        @JsonProperty("district") String district,
        @JsonProperty("city") String city,
        @JsonProperty("state") String state,
        @JsonProperty("zip_code") String zipCode
) {
}
