package com.kaua.ecommerce.infrastructure.customer.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateCustomerCpfInput(
        @JsonProperty("cpf") String cpf
) {
}
