package com.kaua.ecommerce.application.adapters;

public interface TelephoneAdapter {

    boolean validate(String aTelephone);

    String formatInternational(String aTelephone);
}