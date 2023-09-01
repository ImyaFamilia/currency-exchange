package imya.exchange.dto.response.exchangeRate;

import imya.exchange.model.Currency;
import imya.exchange.model.ExchangeRate;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class CalculateExchangeResponse {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;

    public CalculateExchangeResponse(ExchangeRate exchangeRate, BigDecimal amount, BigDecimal convertedAmount) {
        this.baseCurrency = exchangeRate.getBaseCurrency();
        this.targetCurrency = exchangeRate.getTargetCurrency();
        this.rate = exchangeRate.getRate();
        this.amount = amount;
        this.convertedAmount = convertedAmount.setScale(2, RoundingMode.DOWN);
    }
}
