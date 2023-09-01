package imya.exchange.mapper;

import imya.exchange.model.Currency;
import imya.exchange.model.ExchangeRate;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetMapper {
    public static Currency fromResultSetToCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
            resultSet.getInt("id"),
            resultSet.getString("full_name"),
            java.util.Currency.getInstance(
                resultSet.getString("code")
            ),
            resultSet.getString("sign")
        );
    }

    public static ExchangeRate fromResultSetToExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
            resultSet.getInt("id"),
            new Currency(
                resultSet.getInt("base_id"),
                resultSet.getString("base_name"),
                java.util.Currency.getInstance(
                    resultSet.getString("base_code")
                ),
                resultSet.getString("base_sign")
            ),
            new Currency(
                resultSet.getInt("target_id"),
                resultSet.getString("target_name"),
                java.util.Currency.getInstance(
                    resultSet.getString("target_code")
                ),
                resultSet.getString("target_sign")
            ),
            resultSet.getBigDecimal("rate")
        );
    }
}
