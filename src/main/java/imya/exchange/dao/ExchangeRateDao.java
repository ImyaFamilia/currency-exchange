package imya.exchange.dao;

import imya.exchange.model.ExchangeRate;
import imya.exchange.mapper.ResultSetMapper;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao implements Dao<ExchangeRate> {
    private final DataSource dataSource;

    public ExchangeRateDao(DataSource dataSource, CurrencyDao currencyDao) throws SQLException {
        this.dataSource = dataSource;
    }

    @Override
    public List<ExchangeRate> findAll() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                "SELECT\n" +
                    "    r.id AS id,\n" +
                    "    c.id AS base_id,\n" +
                    "    c.full_name AS base_name,\n" +
                    "    c.code AS base_code,\n" +
                    "    c.sign AS base_sign,\n" +
                    "    c2.id AS target_id,\n" +
                    "    c2.full_name AS target_name,\n" +
                    "    c2.code AS target_code,\n" +
                    "    c2.sign AS target_sign,\n" +
                    "    r.rate AS rate\n" +
                    "FROM exchange_rates r\n" +
                    "JOIN currencies c ON c.id = r.base_currency_id\n" +
                    "JOIN currencies c2 ON c2.id = r.target_currency_id"
            );

            return getExchangeRates(resultSet);
        }
    }

    @Override
    public Optional<ExchangeRate> findById(Integer id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT\n" +
                    "    r.id AS id,\n" +
                    "    c.id AS base_id,\n" +
                    "    c.full_name AS base_name,\n" +
                    "    c.code AS base_code,\n" +
                    "    c.sign AS base_sign,\n" +
                    "    c2.id AS target_id,\n" +
                    "    c2.full_name AS target_name,\n" +
                    "    c2.code AS target_code,\n" +
                    "    c2.sign AS target_sign,\n" +
                    "    r.rate AS rate\n" +
                    "FROM exchange_rates r\n" +
                    "JOIN currencies c ON c.id = r.base_currency_id\n" +
                    "JOIN currencies c2 ON c2.id = r.target_currency_id\n" +
                    "WHERE r.id = ?"
            );

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            return getExchangeRate(resultSet);
        }
    }

    @Override
    public Integer save(ExchangeRate entity) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO exchange_rates (\n" +
                    "    base_currency_id,\n" +
                    "    target_currency_id, \n" +
                    "    rate\n" +
                    ") VALUES (?, ?, ?)"
            );

            preparedStatement.setInt(1, entity.getBaseCurrency().getId());
            preparedStatement.setInt(2, entity.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3, entity.getRate());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) return resultSet.getInt(1);
            }

            return null;
        }
    }

    public Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT\n" +
                    "    r.id AS id,\n" +
                    "    c.id AS base_id,\n" +
                    "    c.full_name AS base_name,\n" +
                    "    c.code AS base_code,\n" +
                    "    c.sign AS base_sign,\n" +
                    "    c2.id AS target_id,\n" +
                    "    c2.full_name AS target_name,\n" +
                    "    c2.code AS target_code,\n" +
                    "    c2.sign AS target_sign,\n" +
                    "    r.rate AS rate\n" +
                    "FROM exchange_rates r\n" +
                    "JOIN currencies c ON c.id = r.base_currency_id\n" +
                    "JOIN currencies c2 ON c2.id = r.target_currency_id\n" +
                    "WHERE c.code = ? AND c2.code = ?"
            );

            preparedStatement.setString(1, baseCurrencyCode);
            preparedStatement.setString(2, targetCurrencyCode);
            ResultSet resultSet = preparedStatement.executeQuery();

            return getExchangeRate(resultSet);
        }
    }

    public Integer updateRateByCodes(String baseCurrencyCode, String targetCurrencyCode, BigDecimal newRate) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE exchange_rates \n" +
                    "SET rate = ?\n" +
                    "WHERE id = (\n" +
                    "    SELECT r.id \n" +
                    "    FROM exchange_rates r\n" +
                    "    JOIN currencies c ON c.id = r.base_currency_id\n" +
                    "    JOIN currencies c2 ON c2.id = r.target_currency_id\n" +
                    "    WHERE c.code = ? AND c2.code = ?\n" +
                    ")"
            );

            preparedStatement.setBigDecimal(1, newRate);
            preparedStatement.setString(2, baseCurrencyCode);
            preparedStatement.setString(3, targetCurrencyCode);

            return preparedStatement.executeUpdate();
        }
    }

    public List<ExchangeRate> findByCodesWithUsdBase(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT\n" +
                    "    r.id AS id,\n" +
                    "    c.id AS base_id,\n" +
                    "    c.full_name AS base_name,\n" +
                    "    c.code AS base_code,\n" +
                    "    c.sign AS base_sign,\n" +
                    "    c2.id AS target_id,\n" +
                    "    c2.full_name AS target_name,\n" +
                    "    c2.code AS target_code,\n" +
                    "    c2.sign AS target_sign,\n" +
                    "    r.rate AS rate\n" +
                    "FROM exchange_rates r\n" +
                    "JOIN currencies c ON c.id = r.base_currency_id\n" +
                    "JOIN currencies c2 ON c2.id = r.target_currency_id\n" +
                    "WHERE (\n" +
                    "    base_currency_id = (SELECT a.id FROM currencies a WHERE a.code = 'USD') AND\n" +
                    "    target_currency_id = (SELECT a2.id FROM currencies a2 WHERE a2.code = ?) OR\n" +
                    "    target_currency_id = (SELECT a3.id FROM currencies a3 WHERE a3.code = ?)\n" +
                    ")"
            );

            preparedStatement.setString(1, baseCurrencyCode);
            preparedStatement.setString(2, targetCurrencyCode);
            ResultSet resultSet = preparedStatement.executeQuery();

            return getExchangeRates(resultSet);
        }
    }

    private List<ExchangeRate> getExchangeRates(ResultSet resultSet) throws SQLException {
        List<ExchangeRate> exchangeRates = new LinkedList<>();

        while (resultSet.next()) {
            exchangeRates.add(
                ResultSetMapper.fromResultSetToExchangeRate(resultSet)
            );
        }

        return exchangeRates;
    }

    private Optional<ExchangeRate> getExchangeRate(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return Optional.of(
                ResultSetMapper.fromResultSetToExchangeRate(resultSet)
            );
        }

        return Optional.empty();
    }
}
