package com.kaua.ecommerce.domain.utils;

import java.util.Random;

public final class RandomStringUtils {

    private static final Random random = new Random();

    private RandomStringUtils() {}

    public static String generateValue(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
