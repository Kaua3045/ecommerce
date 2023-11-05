package com.kaua.ecommerce.infrastructure.customer.persistence;

import com.kaua.ecommerce.domain.customer.Cpf;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.customer.Telephone;
import com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressCacheEntity;
import jakarta.persistence.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;

@RedisHash(value = "customer", timeToLive = 60 * 60 * 24) // 1 day
public class CustomerCacheEntity {

    @Id
    private String id;

    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String cpf;
    private String telephone;
    private AddressCacheEntity address;
    private Instant createdAt;
    private Instant updatedAt;

    public CustomerCacheEntity() {}

    private CustomerCacheEntity(
            final String id,
            final String customerId,
            final String firstName,
            final String lastName,
            final String email,
            final String cpf,
            final String telephone,
            final AddressCacheEntity address,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.id = id;
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cpf = cpf;
        this.telephone = telephone;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CustomerCacheEntity toEntity(final Customer aCustomer) {
        return new CustomerCacheEntity(
                aCustomer.getAccountId(),
                aCustomer.getId().getValue(),
                aCustomer.getFirstName(),
                aCustomer.getLastName(),
                aCustomer.getEmail(),
                aCustomer.getCpf().map(Cpf::getValue).orElse(null),
                aCustomer.getTelephone().map(Telephone::getValue).orElse(null),
                aCustomer.getAddress().map(AddressCacheEntity::toEntity).orElse(null),
                aCustomer.getCreatedAt(),
                aCustomer.getUpdatedAt()
        );
    }

    public Customer toDomain() {
        return Customer.with(
                getCustomerId(),
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

    public String getAccountId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
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

    public String getTelephone() {
        return telephone;
    }

    public AddressCacheEntity getAddress() {
        return address;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
