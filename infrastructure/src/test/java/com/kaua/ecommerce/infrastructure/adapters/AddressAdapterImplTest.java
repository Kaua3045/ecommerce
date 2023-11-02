package com.kaua.ecommerce.infrastructure.adapters;

import com.kaua.ecommerce.application.adapters.responses.AddressResponse;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class AddressAdapterImplTest {

    @Autowired
    private AddressAdapterImpl addressAdapter;

    @Test
    void givenAValidZipCode_whenCallFindAddressByZipCode_shouldReturnAddressResponse() {
        final var aZipCode = "01153000";

        final var actual = addressAdapter.findAddressByZipCode(aZipCode);

        Assertions.assertEquals(getAddressResponse(), actual.get());
    }

    @Test
    void givenAnInvalidZipCode_whenCallFindAddressByZipCode_shouldReturnEmpty() {
        final var aZipCode = "01153001";

        final var actual = addressAdapter.findAddressByZipCode(aZipCode);

        Assertions.assertTrue(actual.isEmpty());
    }

    private AddressResponse getAddressResponse() {
        final var aStreet = "Rua Vitorino Carmilo";
        final var aComplement = "";
        final var aDistrict = "Barra Funda";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final var aZipCode = "01153-000";

        return new AddressResponse(
                aZipCode,
                aStreet,
                aComplement,
                aDistrict,
                aCity,
                aState

        );
    }
}
