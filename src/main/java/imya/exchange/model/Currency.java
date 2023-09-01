package imya.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Currency {
    private Integer id;
    private @NonNull String name;
    private @NonNull java.util.Currency code;
    private @NonNull String sign;
}
