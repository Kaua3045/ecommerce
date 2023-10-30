package com.kaua.ecommerce.application.usecases.customer.retrieve.get;

import com.kaua.ecommerce.domain.customer.Customer;

import java.time.Instant;

public record GetCustomerByAccountIdOutput(
        String id,
        String accountId,
        String firstName,
        String lastName,
        String email,
        String cpf,
        String telephone,
        Instant createdAt,
        Instant updatedAt
) {

    public static GetCustomerByAccountIdOutput from(final Customer aCustomer) {
        return new GetCustomerByAccountIdOutput(
                aCustomer.getId().getValue(),
                aCustomer.getAccountId(),
                aCustomer.getFirstName(),
                aCustomer.getLastName(),
                aCustomer.getEmail(),
                aCustomer.getCpf().getFormattedCpf(),
                aCustomer.getTelephone().getValue(),
                aCustomer.getCreatedAt(),
                aCustomer.getUpdatedAt()
        );
    }
}
