package com.kaua.ecommerce.domain.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class SlugUtils {

    private SlugUtils() {}

    private static final Pattern NON_LATIN_PATTERN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("[\\s]");
    private static final Pattern EDGE_DASHES_PATTERN = Pattern.compile("(^-|-$)");

    public static String createSlug(final String aInput) {
        if (aInput == null || aInput.trim().isBlank()) return null;
        String noWhitespace = WHITESPACE_PATTERN.matcher(aInput).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String withoutNonLatin = NON_LATIN_PATTERN.matcher(normalized).replaceAll("");
        String withoutEdges = EDGE_DASHES_PATTERN.matcher(withoutNonLatin).replaceAll("");
        return withoutEdges.toLowerCase(Locale.ENGLISH);
    }
}
