package com.kaua.ecommerce.infrastructure.customer.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetCustomerByAccountIdResponse(
        @JsonProperty("id") String id,
        @JsonProperty("account_id") String accountId,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("email") String email,
        @JsonProperty("cpf") String cpf,
        @JsonProperty("telephone") String telephone,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("updated_at") String updatedAt
) {
}
