package com.kaua.ecommerce.domain.customer;

import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

import java.time.Instant;

public class Customer extends AggregateRoot<CustomerID> {

    private String accountId;
    private String firstName;
    private String lastName;
    private String email;
    private String cpf;
    private Instant createdAt;
    private Instant updatedAt;

    private Customer(
            final CustomerID aCustomerID,
            final String aAccountId,
            final String aFirstName,
            final String aLastName,
            final String aEmail,
            final String aCpf,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        super(aCustomerID);
        this.accountId = aAccountId;
        this.firstName = aFirstName;
        this.lastName = aLastName;
        this.email = aEmail;
        this.cpf = aCpf;
        this.createdAt = aCreatedAt;
        this.updatedAt = aUpdatedAt;
    }

    public static Customer newCustomer(
            final String aAccountId,
            final String aFirstName,
            final String aLastName,
            final String aEmail
    ) {
        final var aId = CustomerID.unique();
        final var aNow = InstantUtils.now();
        return new Customer(
                aId,
                aAccountId,
                aFirstName,
                aLastName,
                aEmail,
                null,
                aNow,
                aNow
        );
    }

    @Override
    public void validate(ValidationHandler handler) {
        new CustomerValidation(this, handler).validate();
    }

    public String getAccountId() {
        return accountId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}