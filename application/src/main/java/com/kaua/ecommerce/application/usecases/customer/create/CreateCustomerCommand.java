package com.kaua.ecommerce.application.usecases.customer.create;

public record CreateCustomerCommand(
        String accountId,
        String firstName,
        String lastName,
        String email
) {

    public static CreateCustomerCommand with(
            final String aAccountId,
            final String aFirstName,
            final String aLastName,
            final String aEmail
    ) {
        return new CreateCustomerCommand(aAccountId, aFirstName, aLastName, aEmail);
    }
}
