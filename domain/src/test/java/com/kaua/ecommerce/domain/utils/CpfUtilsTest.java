package com.kaua.ecommerce.domain.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CpfValidatorTest {

    @Test
    void givenAValidCpf_whenCallValidateCpf_shouldReturnTrue() {
        final var aCpf = "815.959.150-01";
        final var aResult = CpfValidator.validateCpf(aCpf);
        Assertions.assertTrue(aResult);
    }

    @Test
    void givenAnInvalidNullCpf_whenCallValidateCpf_shouldReturnFalse() {
        final String aCpf = null;
        final var aResult = CpfValidator.validateCpf(aCpf);
        Assertions.assertFalse(aResult);
    }

    @Test
    void givenAnInvalidBlankCpf_whenCallValidateCpf_shouldReturnFalse() {
        final var aCpf = " ";
        final var aResult = CpfValidator.validateCpf(aCpf);
        Assertions.assertFalse(aResult);
    }

    @Test
    void givenAnInvalidCpfLengthLessThan11_whenCallValidateCpf_shouldReturnFalse() {
        final var aCpf = "815.959.150";
        final var aResult = CpfValidator.validateCpf(aCpf);
        Assertions.assertFalse(aResult);
    }

    @Test
    void givenAnInvalidCpfLengthMoreThan14_whenCallValidateCpf_shouldReturnFalse() {
        final var aCpf = "815.959.150-011";
        final var aResult = CpfValidator.validateCpf(aCpf);
        Assertions.assertFalse(aResult);
    }

    @Test
    void givenAValidRawCpf_whenCallCleanCpf_shouldReturnCleanedCpf() {
        final var aRawCpf = "815.959.150-01";
        final var cleanedCpf = "81595915001";
        final var aResult = CpfValidator.cleanCpf(aRawCpf);
        Assertions.assertEquals(cleanedCpf, aResult);
    }

    @ParameterizedTest
    @CsvSource({
            "00000000000",
            "11111111111",
            "22222222222",
            "33333333333",
            "44444444444",
            "55555555555",
            "66666666666",
            "77777777777",
            "88888888888",
            "99999999999",
    })
    void givenAnInvalidRepeatedFirstNumberCpf_whenCallValidateCpf_shouldReturnFalse(
            final String aCpf
    ) {
        final var aResult = CpfValidator.validateCpf(aCpf);
        Assertions.assertFalse(aResult);
    }

    @ParameterizedTest
    @CsvSource({
            "01010101010",
            "02030101010",
            "10101010101",
            "213.151.212-12",
            "32326232327",
            "43434343434",
            "54543454540",
            "65656565656",
            "76767676767",
            "87878787878",
            "98989898989",
    })
    void givenAnInvalidRepeatedNumberCpf_whenCallValidateCpf_shouldReturnFalse(
            final String aCpf
    ) {
        final var aResult = CpfValidator.validateCpf(aCpf);
        Assertions.assertFalse(aResult);
    }
}
