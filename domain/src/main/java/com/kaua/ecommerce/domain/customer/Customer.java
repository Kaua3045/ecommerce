package com.kaua.ecommerce.domain.customer;

import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.customer.address.Address;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

import java.time.Instant;
import java.util.Optional;

public class Customer extends AggregateRoot<CustomerID> {

    private String accountId;
    private String firstName;
    private String lastName;
    private String email;
    private Cpf cpf;
    private Telephone telephone;
    private Address address;
    private Instant createdAt;
    private Instant updatedAt;

    private Customer(
            final CustomerID aCustomerID,
            final String aAccountId,
            final String aFirstName,
            final String aLastName,
            final String aEmail,
            final Cpf aCpf,
            final Telephone aTelephone,
            final Address aAddress,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        super(aCustomerID);
        this.accountId = aAccountId;
        this.firstName = aFirstName;
        this.lastName = aLastName;
        this.email = aEmail;
        this.cpf = aCpf;
        this.telephone = aTelephone;
        this.address = aAddress;
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
                null,
                null,
                aNow,
                aNow
        );
    }

    public Customer changeCpf(final Cpf aCpf) {
        this.cpf = aCpf;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Customer changeTelephone(final Telephone aTelephone) {
        this.telephone = aTelephone;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Customer changeAddress(final Address aAddress) {
        this.address = aAddress;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public static Customer with(
            final String aId,
            final String aAccountId,
            final String aFirstName,
            final String aLastName,
            final String aEmail,
            final String aCpf,
            final String aTelephone,
            final Address aAddress,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        return new Customer(
                CustomerID.from(aId),
                aAccountId,
                aFirstName,
                aLastName,
                aEmail,
                aCpf == null ? null : Cpf.newCpf(aCpf),
                aTelephone == null ? null : Telephone.newTelephone(aTelephone),
                aAddress,
                aCreatedAt,
                aUpdatedAt
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

    public Optional<Cpf> getCpf() {
        return Optional.ofNullable(cpf);
    }

    public Optional<Telephone> getTelephone() {
        return Optional.ofNullable(telephone);
    }

    public Optional<Address> getAddress() {
        return Optional.ofNullable(address);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Customer(" +
                "id='" + getId().getValue() + '\'' +
                ", accountId='" + accountId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", cpf=" + cpf +
                ", telephone=" + telephone +
                ", address=" + address +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ')';
    }
}
