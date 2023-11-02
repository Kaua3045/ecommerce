package com.kaua.ecommerce.infrastructure.customer.address.persistence;

import com.kaua.ecommerce.domain.customer.address.Address;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity(name = "Address")
@Table(name = "addresses")
public class AddressJpaEntity {

    @Id
    @Column(name = "address_id", nullable = false, unique = true)
    private String id;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "complement")
    private String complement;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    public AddressJpaEntity() {}

    private AddressJpaEntity(
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

    public static AddressJpaEntity toEntity(final Address aAddress) {
        return new AddressJpaEntity(
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

    public void setId(String id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
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
