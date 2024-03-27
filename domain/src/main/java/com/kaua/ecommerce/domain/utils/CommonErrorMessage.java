package com.kaua.ecommerce.domain.utils;

public final class CommonErrorMessage {

    private CommonErrorMessage() {
    }

    public static String nullOrBlank(final String fieldName) {
        return "'" + fieldName + "' should not be null or blank";
    }

    public static String nullMessage(final String fieldName) {
        return "'" + fieldName + "' should not be null";
    }

    public static String blankMessage(final String fieldName) {
        return "'" + fieldName + "' should not be blank";
    }

    public static String lengthBetween(final String fieldName, final int min, final int max) {
        return "'" + fieldName + "' must be between " + min + " and " + max + " characters";
    }

    public static String maxSize(final String fieldName, final int max) {
        return "'" + fieldName + "' must be less than " + max + " characters";
    }

    public static String minSize(final String fieldName, final int min) {
        return "'" + fieldName + "' must be greater than " + min + " characters";
    }

    public static String greaterThan(final String fieldName, final int value) {
        return "'" + fieldName + "' must be greater than " + value;
    }

    public static String greaterOrEqual(final String fieldName, final int value) {
        return "'" + fieldName + "' must be greater or equal to " + value;
    }

    public static String dateMustBeFuture(final String fieldName) {
        return "'" + fieldName + "' must be a future date";
    }
}
