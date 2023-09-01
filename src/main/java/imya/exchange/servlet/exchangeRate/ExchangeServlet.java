package imya.exchange.servlet.exchangeRate;

import imya.exchange.mapper.RequestMapper;
import imya.exchange.dto.request.exchangeRate.CalculateExchangeRequest;
import imya.exchange.dto.response.exchangeRate.CalculateExchangeResponse;
import imya.exchange.dto.response.ErrorResponse;
import imya.exchange.dao.ExchangeRateDao;
import imya.exchange.service.ExchangeService;
import imya.exchange.util.Utils;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import jakarta.validation.ConstraintViolation;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "ExchangeServlet", value = "/exchange")
public class ExchangeServlet extends HttpServlet {
    private ExchangeService exchangeService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ExchangeRateDao exchangeRateDao = (ExchangeRateDao) config.getServletContext().getAttribute("exchangeRateDao");
        exchangeService = new ExchangeService(exchangeRateDao);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CalculateExchangeRequest calculateExchangeRequest = new CalculateExchangeRequest();
        RequestMapper.fillRequestFromHttpRequest(calculateExchangeRequest, request);
        Set<ConstraintViolation<CalculateExchangeRequest>> violations = Utils.validator.validate(calculateExchangeRequest);

        if (violations.isEmpty()) {
            try {
                Optional<CalculateExchangeResponse> calculateExchangeResponse =
                    exchangeService.calculateExchangeResponse(
                        calculateExchangeRequest.getFrom(),
                        calculateExchangeRequest.getTo(),
                        BigDecimal.valueOf(
                            Double.parseDouble(
                                calculateExchangeRequest.getAmount()
                            )
                        )
                    );

                if (calculateExchangeResponse.isPresent()) {
                    Utils.write(response, calculateExchangeResponse.get());
                } else {
                    Utils.writeResponse(response, new ErrorResponse("Exchange rate not found"), SC_NOT_FOUND);
                }
            } catch (SQLException e) {
                Utils.writeSQLErrorResponse(response, e);
            }
        } else {
            Utils.writeResponse(response, Utils.getParameterErrorsJsonFromViolations(violations), SC_BAD_REQUEST);
        }
    }
}
