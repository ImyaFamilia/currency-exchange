package imya.exchange.servlet.currency;

import imya.exchange.mapper.RequestMapper;
import imya.exchange.dto.request.currency.CreateCurrencyRequest;
import imya.exchange.dao.CurrencyDao;
import imya.exchange.model.Currency;
import imya.exchange.util.Utils;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import jakarta.validation.ConstraintViolation;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "CurrenciesServlet", value = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyDao currencyDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        currencyDao = (CurrencyDao) config.getServletContext().getAttribute("currencyDao");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Utils.write(response, currencyDao.findAll());
        } catch (SQLException e) {
            Utils.writeSQLErrorResponse(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CreateCurrencyRequest createCurrencyRequest = new CreateCurrencyRequest();
        RequestMapper.fillRequestFromHttpRequest(createCurrencyRequest, request);
        Set<ConstraintViolation<CreateCurrencyRequest>> violations = Utils.validator.validate(createCurrencyRequest);

        if (violations.isEmpty()) {
            Currency currency = RequestMapper.getCurrencyFromRequest(createCurrencyRequest);

            try {
                Utils.write(response, currencyDao.findById(
                    currencyDao.save(currency)).get()
                );
            } catch (SQLException e) {
                Utils.writeSQLErrorResponse(response, e);
            }
        } else {
            Utils.writeResponse(response, Utils.getParameterErrorsJsonFromViolations(violations), SC_BAD_REQUEST);
        }
    }
}
