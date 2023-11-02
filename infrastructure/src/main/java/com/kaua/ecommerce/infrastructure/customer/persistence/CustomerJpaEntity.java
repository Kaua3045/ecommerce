package com.kaua.ecommerce.infrastructure.customer.persistence;

import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaEntity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity(name = "Customer")
@Table(name = "customers")
public class CustomerJpaEntity {

    @Id
    @Column(name = "customer_id", nullable = false, unique = true)
    private String id;

    @Column(name = "account_id", nullable = false, unique = true)
    private String accountId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "cpf", unique = true)
    private String cpf;

    @Column(name = "telephone", unique = true)
    private String telephone;

    @JoinColumn(name = "address_id", referencedColumnName = "address_id")
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private AddressJpaEntity address;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    public CustomerJpaEntity() {}

    private CustomerJpaEntity(
            final String id,
            final String accountId,
            final String firstName,
            final String lastName,
            final String email,
            final String cpf,
            final String telephone,
            final AddressJpaEntity address,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.id = id;
        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cpf = cpf;
        this.telephone = telephone;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CustomerJpaEntity toEntity(final Customer aCustomer) {
        return new CustomerJpaEntity(
                aCustomer.getId().getValue(),
                aCustomer.getAccountId(),
                aCustomer.getFirstName(),
                aCustomer.getLastName(),
                aCustomer.getEmail(),
                aCustomer.getCpf() == null ? null : aCustomer.getCpf().getValue(),
                aCustomer.getTelephone() == null ? null : aCustomer.getTelephone().getValue(),
                aCustomer.getAddress() == null ? null : AddressJpaEntity.toEntity(aCustomer.getAddress()),
                aCustomer.getCreatedAt(),
                aCustomer.getUpdatedAt()
        );
    }

    public Customer toDomain() {
        return Customer.with(
                getId(),
                getAccountId(),
                getFirstName(),
                getLastName(),
                getEmail(),
                getCpf(),
                getTelephone(),
                getAddress() == null ? null : getAddress().toDomain(),
                getCreatedAt(),
                getUpdatedAt()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public AddressJpaEntity getAddress() {
        return address;
    }

    public void setAddress(AddressJpaEntity address) {
        this.address = address;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
