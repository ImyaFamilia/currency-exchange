package imya.exchange.servlet.exchangeRate;

import imya.exchange.mapper.RequestMapper;
import imya.exchange.dto.request.exchangeRate.CreateExchangeRateRequest;
import imya.exchange.dto.response.ErrorResponse;
import imya.exchange.dto.response.ParameterErrorResponse;
import imya.exchange.model.Currency;
import imya.exchange.model.ExchangeRate;
import imya.exchange.dao.CurrencyDao;
import imya.exchange.dao.ExchangeRateDao;
import imya.exchange.util.Utils;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import jakarta.validation.ConstraintViolation;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "ExchangeRatesServlet", value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRateDao exchangeRateDao;
    private CurrencyDao currencyDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.exchangeRateDao = (ExchangeRateDao) config.getServletContext().getAttribute("exchangeRateDao");
        this.currencyDao = (CurrencyDao) config.getServletContext().getAttribute("currencyDao");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Utils.write(response, exchangeRateDao.findAll());
        } catch (SQLException e) {
            Utils.writeResponse(response, new ErrorResponse("Unexpected error while working with database"), SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CreateExchangeRateRequest createExchangeRateRequest = new CreateExchangeRateRequest();
        RequestMapper.fillRequestFromHttpRequest(createExchangeRateRequest, request);
        Set<ConstraintViolation<CreateExchangeRateRequest>> violations = Utils.validator.validate(createExchangeRateRequest);

        if (violations.isEmpty()) {
            try {
                Optional<Currency> baseCurrency = currencyDao.findByCode(createExchangeRateRequest.getBaseCurrencyCode());
                Optional<Currency> targetCurrency = currencyDao.findByCode(createExchangeRateRequest.getTargetCurrencyCode());

                if (baseCurrency.isPresent() && targetCurrency.isPresent()) {
                    ExchangeRate exchangeRate = RequestMapper.getExchangeRateFromRequest(
                        createExchangeRateRequest,
                        baseCurrency.get(),
                        targetCurrency.get()
                    );

                    Utils.write(response, exchangeRateDao.findById(exchangeRateDao.save(exchangeRate)).get());
                } else {
                    List<ParameterErrorResponse> errorResponses = new LinkedList<>();
                    if (!baseCurrency.isPresent()) errorResponses.add(new ParameterErrorResponse(
                        "baseCurrencyCode", "baseCurrencyCode does not exist in database"));
                    if (!targetCurrency.isPresent()) errorResponses.add(new ParameterErrorResponse(
                        "targetCurrencyCode", "targetCurrencyCode does not exist in database"));

                    Utils.writeResponse(response, errorResponses, SC_BAD_REQUEST);
                }
            } catch (SQLException e) {
                Utils.writeSQLErrorResponse(response, e);
            }
        } else {
            Utils.writeResponse(response, Utils.getParameterErrorsJsonFromViolations(violations), SC_BAD_REQUEST);
        }
    }
}
