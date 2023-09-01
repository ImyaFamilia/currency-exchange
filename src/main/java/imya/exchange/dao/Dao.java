package imya.exchange.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    abstract List<T> findAll() throws SQLException;

    abstract Optional<T> findById(Integer id) throws SQLException;

    abstract Integer save(T entity) throws SQLException;
}
