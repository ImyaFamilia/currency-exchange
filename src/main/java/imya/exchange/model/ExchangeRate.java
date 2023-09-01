package imya.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ExchangeRate {
    private Integer id;
    private @NonNull Currency baseCurrency;
    private @NonNull Currency targetCurrency;
    private @NonNull BigDecimal rate;
}
