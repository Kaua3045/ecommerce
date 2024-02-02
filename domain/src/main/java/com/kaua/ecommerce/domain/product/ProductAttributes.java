package com.kaua.ecommerce.domain.product;

import com.kaua.ecommerce.domain.ValueObject;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class ProductAttributes extends ValueObject {

    private static final Pattern CHECK_CONTAINS_NUMBERS = Pattern.compile(".*\\d+.*");
    private static final String REMOVE_WHITE_SPACES = Pattern.compile("\\s+").pattern();
    private static final String DEFAULT_SKU_VALUE = "XXXX";
    private static final int MAX_WORD_LENGTH = 4;

    private final ProductColor color;
    private final ProductSize size;
    private final String sku;

    private ProductAttributes(final ProductColor color, final ProductSize size, final String sku) {
        this.color = Objects.requireNonNull(color, "'color' must not be null");
        this.size = Objects.requireNonNull(size, "'size' must not be null");
        this.sku = Objects.requireNonNull(sku);
    }

    public static ProductAttributes create(final ProductColor color, final ProductSize size, final String productName) {
        final var skuGenerated = generateSku(productName, color, size);
        return new ProductAttributes(color, size, skuGenerated);
    }

    public static ProductAttributes with(final ProductColor color, final ProductSize size, final String sku) {
        return new ProductAttributes(color, size, sku);
    }

    private static String generateSku(final String productName, final ProductColor color, final ProductSize size) {
        final var aUniqueId = UUID.randomUUID().toString().substring(0, 8);
        final var aProductNameNullCheck = processProductName(productName);
        final var aProductColor = processProductColor(color);
        final var aProductSize = processProductSize(size);
        final var aProductNameFirstCharacters = extractProductNameCharacters(aProductNameNullCheck);

        return String.format("%s-%s-%s-%s", aUniqueId, aProductNameFirstCharacters, aProductColor, aProductSize);
    }

    private static String processProductName(final String productName) {
        return (productName == null || productName.isBlank())
                ? RandomStringUtils.generateValue(4).toUpperCase()
                : productName.toUpperCase();
    }

    private static String processProductColor(final ProductColor color) {
        return color == null ? DEFAULT_SKU_VALUE : color.getColor().trim()
                .replaceAll(REMOVE_WHITE_SPACES, "").toUpperCase();
    }

    private static String processProductSize(final ProductSize size) {
        return size == null ? DEFAULT_SKU_VALUE : size.getSize().trim()
                .replaceAll(REMOVE_WHITE_SPACES, "").toUpperCase();
    }

    private static String extractProductNameCharacters(String aProductNameNullCheck) {
        StringBuilder aProductNameFirstCharactersStrBuilder = new StringBuilder();
        String[] aProductWords = aProductNameNullCheck.split(REMOVE_WHITE_SPACES);
        for (String word : aProductWords) {
            if (word.length() < MAX_WORD_LENGTH || aProductWords.length < 2 || CHECK_CONTAINS_NUMBERS.matcher(aProductNameNullCheck).matches()) {
                aProductNameFirstCharactersStrBuilder.append(word, 0, Math.min(MAX_WORD_LENGTH, word.length()));
            } else {
                aProductNameFirstCharactersStrBuilder.append(word.charAt(0));
            }
        }
        return aProductNameFirstCharactersStrBuilder.toString().toUpperCase();
    }

    public ProductColor getColor() {
        return color;
    }

    public ProductSize getSize() {
        return size;
    }

    public String getSku() {
        return sku;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ProductAttributes that = (ProductAttributes) o;
        return Objects.equals(sku, that.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getColor(), getSize(), getSku());
    }
}
