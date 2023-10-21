package com.kaua.ecommerce.domain.utils;

public final class CpfValidator {

    private CpfValidator() {
    }

    public static boolean validateCpf(final String aRawCpf) {
        if (aRawCpf == null || aRawCpf.isBlank()) return false;
        final var aCpf = cleanCpf(aRawCpf);

        if (cpfLengthIsNotValid(aCpf)) return false;
        if (areAllDigitsTheRepeated(aCpf)) return false;

        final var digit1 = calculateDigit(aCpf, 10);
        final var digit2 = calculateDigit(aCpf, 11);
        final var actualDigit = extractDigits(aCpf);
        final var validatedDigit = digit1 + "" + digit2;
        return actualDigit.equals(validatedDigit);
    }

    public static String cleanCpf(final String rawCpf) {
        return rawCpf.replaceAll("[^\\d]", "").trim();
    }

    private static String extractDigits(final String aCpf) {
        return aCpf.substring(9);
    }

    private static boolean cpfLengthIsNotValid(final String aCpf) {
        return aCpf.trim().length() != 11;
    }

    private static int calculateDigit(final String aCpf, int factor) {
        int total = 0;
        for (int digit = 0; digit < aCpf.length(); digit++) {
            int num = Character.getNumericValue(aCpf.charAt(digit));
            if (factor > 1) total += num * factor--;
        }
        final var rest = total % 11;
        return (rest < 2) ? 0 : 11 - rest;
    }

    private static boolean areAllDigitsTheRepeated(final String str) {
        for (int i = 0; i < str.length() - 1; i++) {
            if (str.charAt(i) != str.charAt(i + 1)) {
                return false;
            }
        }
        return true;
    }
}
