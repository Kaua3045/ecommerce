package com.kaua.ecommerce.domain.freight;

import com.kaua.ecommerce.domain.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FreightTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewFreight_thenShouldReturnFreight() {
        final var aFreight = Freight.newFreight(
                "12345678",
                FreightType.SEDEX,
                25.0F,
                3
        );

        Assertions.assertNotNull(aFreight);
        Assertions.assertEquals("12345678", aFreight.getCep());
        Assertions.assertEquals(FreightType.SEDEX, aFreight.getType());
        Assertions.assertEquals(25.0F, aFreight.getPrice());
        Assertions.assertEquals(3, aFreight.getDeadline());
    }

    @Test
    void givenAValidValues_whenCallToString_thenShouldReturnString() {
        final var aFreight = Freight.newFreight(
                "12345678",
                FreightType.SEDEX,
                25.0F,
                3
        );

        final var aString = aFreight.toString();

        Assertions.assertNotNull(aString);
        Assertions.assertEquals(
                "Freight(cep='12345678', type=SEDEX, price=25.0, deadline=3)",
                aString
        );
    }

    @Test
    void givenAValidValue_whenCallFreightTypeOf_thenShouldReturnFreightType() {
        final var aFreightTypeName = "SEDEX";

        final var aFreightType = FreightType.of(aFreightTypeName);

        Assertions.assertNotNull(aFreightType);
        Assertions.assertEquals(FreightType.SEDEX.name(), aFreightType.get().name());
    }
}
