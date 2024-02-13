package com.kaua.ecommerce.domain.utils;

import com.kaua.ecommerce.domain.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;

public class InstantUtilsTest extends UnitTest {

    @Test
    void testCallInstantUtilsNow() {
        final var aInstantNow = InstantUtils.now();

        Assertions.assertNotNull(aInstantNow);
    }

    @Test
    void testCallInstantUtilsFromTimestamp() {
        final var aInstantNow = InstantUtils.now();
        final var aTimestamp = aInstantNow.toEpochMilli();

        final var aInstantNowMillis = aInstantNow.truncatedTo(ChronoUnit.MILLIS);

        final var aInstantFromTimestamp = InstantUtils.fromTimestamp(aTimestamp);

        Assertions.assertNotNull(aInstantFromTimestamp);
        Assertions.assertEquals(aInstantNowMillis, aInstantFromTimestamp);
    }
}
