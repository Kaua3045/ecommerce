package com.kaua.ecommerce.infrastructure.customer.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.application.usecases.customer.retrieve.get.GetCustomerAddressOutput;
import com.kaua.ecommerce.infrastructure.adapters.TelephoneAdapterImpl;

import java.time.Instant;

public record GetCustomerByAccountIdResponse(
        @JsonProperty("id") String id,
        @JsonProperty("account_id") String accountId,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("email") String email,
        @JsonProperty("cpf") String cpf,
        @JsonProperty("telephone") String telephone,
        @JsonProperty("address") GetCustomerAddressOutput address,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt
) {

    @JsonIgnore
    private static final TelephoneAdapterImpl telephoneAdapter = new TelephoneAdapterImpl();

    public static GetCustomerByAccountIdResponse with(
            final String id,
            final String accountId,
            final String firstName,
            final String lastName,
            final String email,
            final String cpf,
            final String telephone,
            final GetCustomerAddressOutput address,
            final Instant createdAt,
            final Instant updatedAt,
            final String locale
    ) {
        return new GetCustomerByAccountIdResponse(
                id,
                accountId,
                firstName,
                lastName,
                email,
                cpf,
                telephoneAdapter.formatToCountry(telephone, locale),
                address,
                createdAt,
                updatedAt
        );
    }
}
