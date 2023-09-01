package imya.exchange.dto.request.exchangeRate;

import imya.exchange.validation.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetExchangeRateByCodesRequest {
    @CurrencyCode(message = "Should be valid ISO 4217 code")
    private String baseCurrencyCode;

    @CurrencyCode(message = "Should be valid ISO 4217 code")
    private String targetCurrencyCode;
}
