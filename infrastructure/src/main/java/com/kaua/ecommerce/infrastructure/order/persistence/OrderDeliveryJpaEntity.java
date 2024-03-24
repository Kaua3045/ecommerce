package com.kaua.ecommerce.infrastructure.order.persistence;

import com.kaua.ecommerce.domain.order.OrderDelivery;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Optional;

@Table(name = "orders_deliveries")
@Entity
public class OrderDeliveryJpaEntity {

    @Id
    private String id;

    @Column(name = "freight_type", nullable = false)
    private String freightType;

    @Column(name = "freight_price", nullable = false)
    private float freightPrice;

    @Column(name = "delivery_estimated", nullable = false)
    private int deliveryEstimated;

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

    public OrderDeliveryJpaEntity() {}

    private OrderDeliveryJpaEntity(
            final String id,
            final String freightType,
            final float freightPrice,
            final int deliveryEstimated,
            final String street,
            final String number,
            final String complement,
            final String district,
            final String city,
            final String state,
            final String zipCode
    ) {
        this.id = id;
        this.freightType = freightType;
        this.freightPrice = freightPrice;
        this.deliveryEstimated = deliveryEstimated;
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.district = district;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public static OrderDeliveryJpaEntity toEntity(final OrderDelivery aOrderDelivery) {
        return new OrderDeliveryJpaEntity(
                aOrderDelivery.getId().getValue(),
                aOrderDelivery.getFreightType(),
                aOrderDelivery.getFreightPrice(),
                aOrderDelivery.getDeliveryEstimated(),
                aOrderDelivery.getStreet(),
                aOrderDelivery.getNumber(),
                aOrderDelivery.getComplement().orElse(null),
                aOrderDelivery.getDistrict(),
                aOrderDelivery.getCity(),
                aOrderDelivery.getState(),
                aOrderDelivery.getZipCode()
        );
    }

    public OrderDelivery toDomain() {
        return OrderDelivery.with(
                getId(),
                getFreightType(),
                getFreightPrice(),
                getDeliveryEstimated(),
                getStreet(),
                getNumber(),
                getComplement().orElse(null),
                getDistrict(),
                getCity(),
                getState(),
                getZipCode()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFreightType() {
        return freightType;
    }

    public void setFreightType(String freightType) {
        this.freightType = freightType;
    }

    public float getFreightPrice() {
        return freightPrice;
    }

    public void setFreightPrice(float freightPrice) {
        this.freightPrice = freightPrice;
    }

    public int getDeliveryEstimated() {
        return deliveryEstimated;
    }

    public void setDeliveryEstimated(int deliveryEstimated) {
        this.deliveryEstimated = deliveryEstimated;
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

    public Optional<String> getComplement() {
        return Optional.ofNullable(complement);
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
}
