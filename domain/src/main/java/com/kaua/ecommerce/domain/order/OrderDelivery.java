package com.kaua.ecommerce.domain.order;

import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.order.identifiers.OrderDeliveryID;
import com.kaua.ecommerce.domain.order.validations.OrderDeliveryValidation;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

import java.util.Optional;

public class OrderDelivery extends AggregateRoot<OrderDeliveryID> {

    private final String freightType;
    private final float freightPrice;
    private final int deliveryEstimated;
    private final String street;
    private final String number;
    private final String complement;
    private final String district;
    private final String city;
    private final String state;
    private final String zipCode;

    private OrderDelivery(
            final OrderDeliveryID aOrderDeliveryID,
            final String aFreightType,
            final float aFreightPrice,
            final int aDeliveryEstimated,
            final String aStreet,
            final String aNumber,
            final String aComplement,
            final String aDistrict,
            final String aCity,
            final String aState,
            final String aZipCode
    ) {
        super(aOrderDeliveryID);
        this.freightType = aFreightType;
        this.freightPrice = aFreightPrice;
        this.deliveryEstimated = aDeliveryEstimated;
        this.street = aStreet;
        this.number = aNumber;
        this.complement = aComplement;
        this.district = aDistrict;
        this.city = aCity;
        this.state = aState;
        this.zipCode = aZipCode;
    }

    public static OrderDelivery newOrderDelivery(
            final String aFreightType,
            final float aFreightPrice,
            final int aDeliveryEstimated,
            final String aStreet,
            final String aNumber,
            final String aComplement,
            final String aDistrict,
            final String aCity,
            final String aState,
            final String aZipCode
    ) {
        final var aOrderDeliveryId = OrderDeliveryID.unique();
        return new OrderDelivery(
                aOrderDeliveryId,
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );
    }

    public static OrderDelivery with(
            final String aOrderDeliveryId,
            final String aFreightType,
            final float aFreightPrice,
            final int aDeliveryEstimated,
            final String aStreet,
            final String aNumber,
            final String aComplement,
            final String aDistrict,
            final String aCity,
            final String aState,
            final String aZipCode
    ) {
        return new OrderDelivery(
                OrderDeliveryID.from(aOrderDeliveryId),
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );
    }

    @Override
    public void validate(ValidationHandler handler) {
        new OrderDeliveryValidation(handler, this).validate();
    }

    public String getFreightType() {
        return freightType;
    }

    public float getFreightPrice() {
        return freightPrice;
    }

    public int getDeliveryEstimated() {
        return deliveryEstimated;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public Optional<String> getComplement() {
        return Optional.ofNullable(complement);
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

    @Override
    public String toString() {
        return "OrderDelivery(" +
                "id='" + getId().getValue() + '\'' +
                ", freightType=" + freightType +
                ", freightPrice=" + freightPrice +
                ", deliveryEstimated=" + deliveryEstimated +
                ", street='" + street + '\'' +
                ", number='" + number + '\'' +
                ", complement='" + complement + '\'' +
                ", district='" + district + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ')';
    }
}
