package imya.exchange.dto.request.exchangeRate;

import imya.exchange.validation.CurrencyCode;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatchExchangeRateByCodesRequest {
    @CurrencyCode(message = "Should be valid ISO 4217 code")
    private String baseCurrencyCode;

    @CurrencyCode(message = "Should be valid ISO 4217 code")
    private String targetCurrencyCode;

    @NotBlank(message = "Rate should not be empty")
    @Positive(message = "Should be greater than 0")
    @Digits(integer = 100, fraction = 6, message = "Humber of fractions should be no more than 6")
    private String rate;
}
