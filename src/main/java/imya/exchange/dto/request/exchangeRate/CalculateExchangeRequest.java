package imya.exchange.dto.request.exchangeRate;

import imya.exchange.validation.CurrencyCode;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CalculateExchangeRequest {
    @CurrencyCode(message = "Should be valid ISO 4217 code")
    private String from;

    @CurrencyCode(message = "Should be valid ISO 4217 code")
    private String to;

    @NotBlank(message = "Rate should not be empty")
    @Positive(message = "Should be greater than 0")
    @Digits(integer = 100, fraction = 2, message = "Number of fractions should be no more than 2")
    private String amount;
}
