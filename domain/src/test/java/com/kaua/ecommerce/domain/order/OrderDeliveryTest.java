package com.kaua.ecommerce.domain.order;

import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.order.identifiers.OrderDeliveryID;
import com.kaua.ecommerce.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderDeliveryTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewOrderDelivery_shouldCreateNewOrderDelivery() {
        final var aFreightType = "sedex";
        final var aFreightPrice = 5.0F;
        final var aFreightDays = 5;
        final var aStreet = "aStreet";
        final var aNumber = "235";
        final String aComplement = null;
        final var aDistrict = "aDistrict";
        final var aCity = "aCity";
        final var aState = "aState";
        final var aZipCode = "aZipCode";

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aFreightDays,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        Assertions.assertNotNull(aOrderDelivery);
        Assertions.assertEquals(aFreightType, aOrderDelivery.getFreightType());
        Assertions.assertEquals(aFreightPrice, aOrderDelivery.getFreightPrice());
        Assertions.assertEquals(aFreightDays, aOrderDelivery.getDeliveryEstimated());
        Assertions.assertEquals(aStreet, aOrderDelivery.getStreet());
        Assertions.assertEquals(aNumber, aOrderDelivery.getNumber());
        Assertions.assertTrue(aOrderDelivery.getComplement().isEmpty());
        Assertions.assertEquals(aCity, aOrderDelivery.getCity());
        Assertions.assertEquals(aState, aOrderDelivery.getState());
        Assertions.assertEquals(aZipCode, aOrderDelivery.getZipCode());
        Assertions.assertDoesNotThrow(() -> aOrderDelivery.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidValues_whenCallOrderDeliveryWith_shouldRestoreOrderDelivery() {
        final var aOrderDeliveryId = "aOrderDeliveryId";
        final var aFreightType = "sedex";
        final var aFreightPrice = 5.0F;
        final var aFreightDays = 5;
        final var aStreet = "aStreet";
        final var aNumber = "235";
        final String aComplement = null;
        final var aDistrict = "aDistrict";
        final var aCity = "aCity";
        final var aState = "aState";
        final var aZipCode = "aZipCode";

        final var aOrderDelivery = OrderDelivery.with(
                aOrderDeliveryId,
                aFreightType,
                aFreightPrice,
                aFreightDays,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        Assertions.assertNotNull(aOrderDelivery);
        Assertions.assertEquals(aOrderDeliveryId, aOrderDelivery.getId().getValue());
        Assertions.assertEquals(aFreightType, aOrderDelivery.getFreightType());
        Assertions.assertEquals(aFreightPrice, aOrderDelivery.getFreightPrice());
        Assertions.assertEquals(aFreightDays, aOrderDelivery.getDeliveryEstimated());
        Assertions.assertEquals(aStreet, aOrderDelivery.getStreet());
        Assertions.assertEquals(aNumber, aOrderDelivery.getNumber());
        Assertions.assertTrue(aOrderDelivery.getComplement().isEmpty());
        Assertions.assertEquals(aCity, aOrderDelivery.getCity());
        Assertions.assertEquals(aState, aOrderDelivery.getState());
        Assertions.assertEquals(aZipCode, aOrderDelivery.getZipCode());
        Assertions.assertDoesNotThrow(() -> aOrderDelivery.validate(new ThrowsValidationHandler()));
    }

    @Test
    void testOrderDeliveryIdEqualsAndHashCode() {
        final var aOrderDeliveryId = OrderDeliveryID.from("123456789");
        final var anotherOrderDeliveryId = OrderDeliveryID.from("123456789");

        Assertions.assertTrue(aOrderDeliveryId.equals(anotherOrderDeliveryId));
        Assertions.assertTrue(aOrderDeliveryId.equals(aOrderDeliveryId));
        Assertions.assertFalse(aOrderDeliveryId.equals(null));
        Assertions.assertFalse(aOrderDeliveryId.equals(""));
        Assertions.assertEquals(aOrderDeliveryId.hashCode(), anotherOrderDeliveryId.hashCode());
    }

    @Test
    void givenAOrderDelivery_whenCallToString_shouldReturnStringRepresentation() {
        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                "sedex",
                5.0F,
                5,
                "aStreet",
                "235",
                null,
                "aDistrict",
                "aCity",
                "aState",
                "aZipCode"
        );

        final var aStringRepresentation = aOrderDelivery.toString();

        Assertions.assertNotNull(aStringRepresentation);
        Assertions.assertTrue(aStringRepresentation.contains("OrderDelivery("));
        Assertions.assertTrue(aStringRepresentation.contains("id='"));
        Assertions.assertTrue(aStringRepresentation.contains("freightType=sedex"));
        Assertions.assertTrue(aStringRepresentation.contains("freightPrice=5.0"));
        Assertions.assertTrue(aStringRepresentation.contains("deliveryEstimated=5"));
        Assertions.assertTrue(aStringRepresentation.contains("street='aStreet'"));
        Assertions.assertTrue(aStringRepresentation.contains("number='235'"));
        Assertions.assertTrue(aStringRepresentation.contains("complement='null'"));
        Assertions.assertTrue(aStringRepresentation.contains("district='aDistrict'"));
        Assertions.assertTrue(aStringRepresentation.contains("city='aCity'"));
        Assertions.assertTrue(aStringRepresentation.contains("state='aState'"));
        Assertions.assertTrue(aStringRepresentation.contains("zipCode='aZipCode'"));
    }
}
