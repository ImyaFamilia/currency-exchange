package imya.exchange.servlet.currency;

import imya.exchange.dto.response.ErrorResponse;
import imya.exchange.model.Currency;
import imya.exchange.dao.CurrencyDao;
import imya.exchange.util.Utils;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "CurrencyServlet", value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyDao currencyDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        currencyDao = (CurrencyDao) config.getServletContext().getAttribute("currencyDao");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getPathInfo().substring(1);

        try {
            Optional<Currency> currency = currencyDao.findByCode(code);
            if (currency.isPresent()) {
                Utils.write(response, currency.get());
            } else {
                Utils.writeResponse(response, new ErrorResponse("Currency not found"), SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            Utils.writeSQLErrorResponse(response, e);
        }
    }
}
