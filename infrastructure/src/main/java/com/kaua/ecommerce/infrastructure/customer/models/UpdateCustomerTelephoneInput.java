package com.kaua.ecommerce.infrastructure.customer.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateCustomerTelephoneInput(
        @JsonProperty("telephone") String telephone
) {
}
