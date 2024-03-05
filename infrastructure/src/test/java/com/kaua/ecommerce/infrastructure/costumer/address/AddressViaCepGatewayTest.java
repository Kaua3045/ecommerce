package com.kaua.ecommerce.infrastructure.costumer.address;

import com.kaua.ecommerce.application.gateways.AddressGateway;
import com.kaua.ecommerce.application.gateways.responses.AddressResponse;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.api.ViaCepClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
public class AddressViaCepGatewayTest {

    @MockBean
    private ViaCepClient viaCepClient;

    @Autowired
    private AddressGateway addressGateway;

    @Test
    void givenAValidZipCode_whenCallFindAddressByZipCode_shouldReturnAddressResponse() {
        final var aZipCode = "01153000";

        Mockito.when(viaCepClient.getAddressByZipCode(aZipCode))
                .thenReturn(getAddressResponse());

        final var actual = addressGateway.findAddressByZipCodeInExternalService(aZipCode);

        Assertions.assertEquals(getAddressResponse(), actual.get());
    }

    @Test
    void givenAnInvalidZipCode_whenCallFindAddressByZipCode_shouldReturnEmpty() {
        final var aZipCode = "01153001";

        Mockito.when(viaCepClient.getAddressByZipCode(aZipCode))
                .thenReturn(new AddressResponse(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                ));

        final var actual = addressGateway.findAddressByZipCodeInExternalService(aZipCode);

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
