package com.kaua.ecommerce.domain.utils;

public final class CommonErrorMessage {

    private CommonErrorMessage() {}

    public static String nullOrBlank(final String fieldName) {
        return "'" + fieldName + "' should not be null or blank";
    }

    public static String lengthBetween(final String fieldName, final int min, final int max) {
        return "'" + fieldName + "' must be between " + min + " and " + max + " characters";
    }
}
