package imya.exchange.service;

import imya.exchange.dao.ExchangeRateDao;
import imya.exchange.dto.response.exchangeRate.CalculateExchangeResponse;
import imya.exchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static java.math.MathContext.*;
import static java.math.RoundingMode.*;

public class ExchangeService {
    private final ExchangeRateDao exchangeRateDao;

    public ExchangeService(ExchangeRateDao exchangeRateDao) {
        this.exchangeRateDao = exchangeRateDao;
    }

    public Optional<CalculateExchangeResponse> calculateExchangeResponse(
        String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) throws SQLException {
        Optional<ExchangeRate> exchangeRate = getExchangeRate(baseCurrencyCode, targetCurrencyCode);

        if (exchangeRate.isPresent()) {
            BigDecimal convertedAmount = amount.multiply(exchangeRate.get().getRate()).setScale(2, DOWN);

            return Optional.of(
                new CalculateExchangeResponse(
                    exchangeRate.get(),
                    amount,
                    convertedAmount
                )
            );
        }

        return Optional.empty();
    }

    private Optional<ExchangeRate> getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        Optional<ExchangeRate> exchangeRate = getDirectExchangeRate(baseCurrencyCode, targetCurrencyCode);

        if (!exchangeRate.isPresent()) exchangeRate = getReversedExchangeRate(baseCurrencyCode, targetCurrencyCode);
        if (!exchangeRate.isPresent()) exchangeRate = getUsdExchangeRate(baseCurrencyCode, targetCurrencyCode);

        return exchangeRate;
    }


    private Optional<ExchangeRate> getDirectExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        return exchangeRateDao.findByCodes(baseCurrencyCode, targetCurrencyCode);
    }

    private Optional<ExchangeRate> getReversedExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        Optional<ExchangeRate> exchangeRateOptional = exchangeRateDao.findByCodes(targetCurrencyCode, baseCurrencyCode);

        if (!exchangeRateOptional.isPresent()) return Optional.empty();

        ExchangeRate reversedExchangeRate = exchangeRateOptional.get();

        return Optional.of(
            new ExchangeRate(
                reversedExchangeRate.getTargetCurrency(),
                reversedExchangeRate.getBaseCurrency(),
                BigDecimal.ONE.divide(reversedExchangeRate.getRate(), DECIMAL64)
            )
        );
    }

    private Optional<ExchangeRate> getUsdExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        List<ExchangeRate> ratesWithUsdBase = exchangeRateDao.findByCodesWithUsdBase(baseCurrencyCode, targetCurrencyCode);

        Optional<ExchangeRate> usdToBaseExchange = getExchangeRateForCode(ratesWithUsdBase, baseCurrencyCode);
        Optional<ExchangeRate> usdToTargetExchange = getExchangeRateForCode(ratesWithUsdBase, targetCurrencyCode);

        if (usdToBaseExchange.isPresent() && usdToTargetExchange.isPresent()) {
            BigDecimal usdToBaseRate = usdToBaseExchange.get().getRate();
            BigDecimal usdToTargetRate = usdToTargetExchange.get().getRate();

            BigDecimal baseToTargetRate = usdToTargetRate.divide(usdToBaseRate, DECIMAL64);

            return Optional.of(
                new ExchangeRate(
                    usdToBaseExchange.get().getTargetCurrency(),
                    usdToTargetExchange.get().getTargetCurrency(),
                    baseToTargetRate.setScale(6, DOWN)
                )
            );
        }

        return Optional.empty();
    }

    private Optional<ExchangeRate> getExchangeRateForCode(List<ExchangeRate> rates, String code) {
        java.util.Currency currency = java.util.Currency.getInstance(code);

        return rates.stream()
            .filter(rate -> rate.getTargetCurrency().getCode().equals(currency))
            .findFirst();
    }
}
