package imya.exchange.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Currency;

public class CurrencyCodeValidator implements ConstraintValidator<CurrencyCode, String> {
    @Override
    public void initialize(CurrencyCode constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        for (Currency currency : Currency.getAvailableCurrencies()) {
            if (currency.getCurrencyCode().equals(s)) {
                return true;
            }
        }

        return false;
    }
}
