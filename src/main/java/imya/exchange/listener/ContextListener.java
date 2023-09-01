package imya.exchange.listener;

import imya.exchange.dao.CurrencyDao;
import imya.exchange.dao.ExchangeRateDao;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.sqlite.SQLiteDataSource;

import java.sql.SQLException;

@WebListener
public class ContextListener implements ServletContextListener {
    private static final String URL = "jdbc:sqlite::resource:currency.sqlite";
    private static final SQLiteDataSource DATA_SOURCE = new SQLiteDataSource();

    public ContextListener() {
        try {
            Class.forName("org.sqlite.JDBC");
            DATA_SOURCE.setUrl(URL);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        try {
            CurrencyDao currencyDao = new CurrencyDao(DATA_SOURCE);
            servletContext.setAttribute("currencyDao", currencyDao);
            servletContext.setAttribute("exchangeRateDao", new ExchangeRateDao(DATA_SOURCE, currencyDao));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
