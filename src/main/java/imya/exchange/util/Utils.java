package imya.exchange.util;

import imya.exchange.dto.response.ErrorResponse;
import imya.exchange.dto.response.ParameterErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static jakarta.servlet.http.HttpServletResponse.*;

public final class Utils {
    public static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static Map<String, Object> getJsonWithKey(String key, Object jsonResponse) {
        return new HashMap<String, Object>() {{
            put(key, jsonResponse);
        }};
    }

    public static void write(HttpServletResponse response, Object object) throws IOException {
        new ObjectMapper().writeValue(response.getWriter(), object);
    }

    public static void writeResponse(HttpServletResponse response, Object jsonResponse, int code) throws IOException {
        response.setStatus(code);
        new ObjectMapper().writeValue(response.getWriter(), getJsonWithKey("errors", jsonResponse));
    }

    public static void writeSQLErrorResponse(HttpServletResponse response, SQLException e) throws IOException {
        if (e.getErrorCode() == 19) {
            Utils.writeResponse(response, new ErrorResponse("Code already exists"), SC_CONFLICT);
        } else {
            Utils.writeResponse(response, new ErrorResponse("Unexpected error while working with database"),
                SC_INTERNAL_SERVER_ERROR);
        }
    }

    public static <T> List<ParameterErrorResponse> getParameterErrorsJsonFromViolations(Set<ConstraintViolation<T>> violations) {
        List<ParameterErrorResponse> errors = new ArrayList<>();
        for (ConstraintViolation<T> violation : violations) {
            errors.add(
                new ParameterErrorResponse(
                    violation.getPropertyPath().toString(),
                    violation.getMessage()
                )
            );
        }

        return errors;
    }

    public static Map<String, String> getParametersMap(String pairs) {
        Map<String, String> parametersMap = new HashMap<>();

        for (String pair : pairs.split("&")) {
            String[] splitPair = pair.split("=");
            parametersMap.put(splitPair[0], splitPair[1]);
        }

        return parametersMap;
    }
}
