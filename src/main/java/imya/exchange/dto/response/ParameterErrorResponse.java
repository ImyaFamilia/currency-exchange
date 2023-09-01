package imya.exchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParameterErrorResponse {
    private String parameter;
    private String description;
}
