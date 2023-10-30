package com.kaua.ecommerce.infrastructure.adapters;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.kaua.ecommerce.application.adapters.TelephoneAdapter;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import org.springframework.stereotype.Component;

@Component
public class TelephoneAdapterImpl implements TelephoneAdapter {

    private final PhoneNumberUtil phoneNumberUtil;

    public TelephoneAdapterImpl() {
        this.phoneNumberUtil = PhoneNumberUtil.getInstance();
    }

    @Override
    public boolean validate(final String aTelephone) {
        try {
            final var parsedTelephone = this.phoneNumberUtil.parse(aTelephone, null);

            return this.phoneNumberUtil.isValidNumber(parsedTelephone);
        } catch (NumberParseException e) {
            throw DomainException.with(new Error(CommonErrorMessage.nullOrBlank("telephone")));
        }
    }

    @Override
    public String formatInternational(final String aTelephone) {
        try {
            final var parsedTelephone = this.phoneNumberUtil.parse(aTelephone, null);

            return this.phoneNumberUtil.format(parsedTelephone, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            throw DomainException.with(new Error(CommonErrorMessage.nullOrBlank("telephone")));
        }
    }
}
