package imya.exchange.dto.request.currency;

import imya.exchange.validation.CurrencyCode;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCurrencyRequest {
    @NotBlank(message = "Name should not be empty")
    private String name;

    @CurrencyCode(message = "Should be valid ISO 4217 code")
    private String code;

    @NotBlank(message = "Sign should not be empty")
    private String sign;
}
