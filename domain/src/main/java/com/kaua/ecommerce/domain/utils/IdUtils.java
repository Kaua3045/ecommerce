package com.kaua.ecommerce.domain.utils;

import java.util.UUID;

public final class IdUtils {

    private IdUtils() {}

    public static String generate() {
        return UUID.randomUUID().toString().toLowerCase();
    }

    public static String generateWithoutDash() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
