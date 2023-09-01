package imya.exchange.dao;

import imya.exchange.mapper.ResultSetMapper;
import imya.exchange.model.Currency;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class CurrencyDao implements Dao<imya.exchange.model.Currency> {
    private final DataSource dataSource;

    public CurrencyDao(DataSource dataSource) throws SQLException {
        this.dataSource = dataSource;
    }

    @Override
    public List<imya.exchange.model.Currency> findAll() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM currencies");

            return getCurrencies(resultSet);
        }
    }

    @Override
    public Optional<imya.exchange.model.Currency> findById(Integer id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currencies WHERE id = ?");

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            return getCurrency(resultSet);
        }
    }

    public Optional<imya.exchange.model.Currency> findByCode(String code) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currencies WHERE code = ?");

            preparedStatement.setString(1, code);

            ResultSet resultSet = preparedStatement.executeQuery();
            return getCurrency(resultSet);
        }
    }

    @Override
    public Integer save(imya.exchange.model.Currency entity) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement =
                connection.prepareStatement("INSERT INTO currencies (full_name, code, sign) VALUES (?, ?, ?)");

            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getCode().toString());
            preparedStatement.setString(3, entity.getSign());
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            return null;
        }
    }

    private List<imya.exchange.model.Currency> getCurrencies(ResultSet resultSet) throws SQLException {
        List<imya.exchange.model.Currency> currencies = new LinkedList<>();

        while (resultSet.next()) {
            currencies.add(
                ResultSetMapper.fromResultSetToCurrency(resultSet)
            );
        }

        return currencies;
    }

    private Optional<Currency> getCurrency(ResultSet currency) throws SQLException {
        if (currency.next()) {
            return Optional.of(ResultSetMapper.fromResultSetToCurrency(currency));
        }

        return Optional.empty();
    }
}
