package com.kaua.ecommerce.application.usecases.customer.retrieve.get;

import com.kaua.ecommerce.domain.customer.Cpf;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.customer.Telephone;

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
                aCustomer.getCpf().map(Cpf::getFormattedCpf).orElse(null),
                aCustomer.getTelephone().map(Telephone::getValue).orElse(null),
                aCustomer.getAddress().map(GetCustomerAddressOutput::from).orElse(null),
                aCustomer.getCreatedAt(),
                aCustomer.getUpdatedAt()
        );
    }
}
