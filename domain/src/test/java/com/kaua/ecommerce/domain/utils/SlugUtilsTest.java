package com.kaua.ecommerce.domain.utils;

import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.category.SlugUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class SlugUtilsTest extends UnitTest {

    @ParameterizedTest
    @CsvSource({
            "Hello World !!, hello-world",
            "H e llo wORLD!! , h-e-llo-world",
            "Hello World, hello-world",
            "Test SLUG cReation, test-slug-creation",
    })
    void givenAValidString_whenCallCreateSlug_shouldReturnSlug(
            final String aInput,
            final String aSlugResult
    ) {
        final var aResult = SlugUtils.createSlug(aInput);
        Assertions.assertEquals(aSlugResult, aResult);
    }

    @Test
    void givenAnInvalidNullString_whenCallCreateSlug_shouldReturnNull() {
        final String aInput = null;

        final var aOutput = SlugUtils.createSlug(aInput);

        Assertions.assertNull(aOutput);
    }

    @Test
    void givenAnInvalidBlankString_whenCallCreateSlug_shouldReturnNull() {
        final var aInput = " ";

        final var aOutput = SlugUtils.createSlug(aInput);

        Assertions.assertNull(aOutput);
    }
}
