package imya.exchange.mapper;

import imya.exchange.dto.request.currency.CreateCurrencyRequest;
import imya.exchange.dto.request.exchangeRate.CreateExchangeRateRequest;
import imya.exchange.model.Currency;
import imya.exchange.model.ExchangeRate;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public final class RequestMapper {
    public static void fillRequestFromHttpRequest(Object object, HttpServletRequest request) {
        Class<?> tClass = object.getClass();
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.getType().equals(String.class)) throw new RuntimeException("Illegal field type");

            field.setAccessible(true);
            try {
                field.set(object, request.getParameter(field.getName()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static ExchangeRate getExchangeRateFromRequest(CreateExchangeRateRequest createExchangeRateRequest,
                                                          Currency baseCurrency, Currency targetCurrency) {
        return new ExchangeRate(
            baseCurrency,
            targetCurrency,
            BigDecimal.valueOf(
                Double.parseDouble(
                    createExchangeRateRequest.getRate()
                )
            )
        );
    }

    public static Currency getCurrencyFromRequest(CreateCurrencyRequest createCurrencyRequest) {
        return new Currency(
            createCurrencyRequest.getName(),
            java.util.Currency.getInstance(
                createCurrencyRequest.getCode()
            ),
            createCurrencyRequest.getSign()
        );
    }
}