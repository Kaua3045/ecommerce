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
        GetCustomerAddressOutput address,
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
                aCustomer.getCpf() == null ? null : aCustomer.getCpf().getFormattedCpf(),
                aCustomer.getTelephone() == null ? null : aCustomer.getTelephone().getValue(),
                aCustomer.getAddress() == null ? null : GetCustomerAddressOutput.from(aCustomer.getAddress()),
                aCustomer.getCreatedAt(),
                aCustomer.getUpdatedAt()
        );
    }
}
