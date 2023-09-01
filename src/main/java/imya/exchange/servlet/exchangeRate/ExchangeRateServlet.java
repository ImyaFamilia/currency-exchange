package imya.exchange.servlet.exchangeRate;

import imya.exchange.dto.response.ErrorResponse;
import imya.exchange.dto.request.exchangeRate.GetExchangeRateByCodesRequest;
import imya.exchange.dto.request.exchangeRate.PatchExchangeRateByCodesRequest;
import imya.exchange.model.ExchangeRate;
import imya.exchange.dao.ExchangeRateDao;
import imya.exchange.util.Utils;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import jakarta.validation.ConstraintViolation;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "ExchangeRateServlet", value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRateDao exchangeRateDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.exchangeRateDao = (ExchangeRateDao) config.getServletContext().getAttribute("exchangeRateDao");
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getMethod();
        if (method.equals("PATCH")) {
            this.doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String codes = request.getPathInfo().substring(1);

        if (codes.length() != 6) {
            Utils.writeResponse(response, new ErrorResponse("Pair length should be 6 characters"), SC_BAD_REQUEST);
            return;
        }

        GetExchangeRateByCodesRequest getExchangeRateByCodesRequest = new GetExchangeRateByCodesRequest(
            codes.substring(0, 3),
            codes.substring(3)
        );

        Set<ConstraintViolation<GetExchangeRateByCodesRequest>> violations = Utils.validator.validate(getExchangeRateByCodesRequest);

        if (violations.isEmpty()) {
            try {
                Optional<ExchangeRate> exchangeRate = exchangeRateDao.findByCodes(
                    getExchangeRateByCodesRequest.getBaseCurrencyCode(),
                    getExchangeRateByCodesRequest.getTargetCurrencyCode()
                );

                if (exchangeRate.isPresent()) {
                    Utils.write(response, exchangeRate.get());
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

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String codes = request.getPathInfo().substring(1);

        if (codes.length() != 6) {
            Utils.writeResponse(response, new ErrorResponse("Pair length should be 6 characters"), SC_BAD_REQUEST);
            return;
        }

        // request.getParameter() doesn't work
        Map<String, String> parametersMap = Utils.getParametersMap(request.getReader().readLine());

        PatchExchangeRateByCodesRequest patchExchangeRateByCodesRequest = new PatchExchangeRateByCodesRequest(
            codes.substring(0, 3),
            codes.substring(3),
            parametersMap.get("rate")
        );

        Set<ConstraintViolation<PatchExchangeRateByCodesRequest>> violations =
            Utils.validator.validate(patchExchangeRateByCodesRequest);

        if (violations.isEmpty()) {
            try {
                int affectedRows = exchangeRateDao.updateRateByCodes(
                    patchExchangeRateByCodesRequest.getBaseCurrencyCode(),
                    patchExchangeRateByCodesRequest.getTargetCurrencyCode(),
                    BigDecimal.valueOf(
                        Double.parseDouble(
                            patchExchangeRateByCodesRequest.getRate()
                        )
                    )
                );

                if (affectedRows == 1) {
                    Utils.write(
                        response,
                        exchangeRateDao.findByCodes(
                            patchExchangeRateByCodesRequest.getBaseCurrencyCode(),
                            patchExchangeRateByCodesRequest.getTargetCurrencyCode()
                        ).get()
                    );
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
