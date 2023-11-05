package com.kaua.ecommerce.infrastructure.customer.address.persistence;

import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.customer.address.Address;
import jakarta.persistence.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;

@RedisHash(value = "address", timeToLive = 60 * 60 * 24) // 1 day
public class AddressCacheEntity {

    @Id
    private String id;

    private String street;
    private String number;
    private String complement;
    private String district;
    private String city;
    private String state;
    private String zipCode;
    private Instant createdAt;
    private Instant updatedAt;

    public AddressCacheEntity() {}

    private AddressCacheEntity(
            final String id,
            final String street,
            final String number,
            final String complement,
            final String district,
            final String city,
            final String state,
            final String zipCode,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.district = district;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AddressCacheEntity toEntity(final Address aAddress) {
        return new AddressCacheEntity(
                aAddress.getId().getValue(),
                aAddress.getStreet(),
                aAddress.getNumber(),
                aAddress.getComplement(),
                aAddress.getDistrict(),
                aAddress.getCity(),
                aAddress.getState(),
                aAddress.getZipCode(),
                aAddress.getCreatedAt(),
                aAddress.getUpdatedAt()
        );
    }

    public Address toDomain() {
        return Address.with(
                getId(),
                getStreet(),
                getNumber(),
                getComplement(),
                getDistrict(),
                getCity(),
                getState(),
                getZipCode(),
                getCreatedAt(),
                getUpdatedAt()
        );
    }

    public String getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getComplement() {
        return complement;
    }

    public String getDistrict() {
        return district;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
