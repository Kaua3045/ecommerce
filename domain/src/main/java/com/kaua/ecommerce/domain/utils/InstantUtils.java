package com.kaua.ecommerce.domain.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class InstantUtils {

    private InstantUtils() {}

    public static Instant now() {
        return Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    public static Instant fromTimestamp(final Long timestamp) {
        return Instant.ofEpochMilli(timestamp).truncatedTo(ChronoUnit.MICROS);
    }

    public static Instant parse(final String instant) {
        return Instant.parse(instant).truncatedTo(ChronoUnit.MICROS);
    }
}
