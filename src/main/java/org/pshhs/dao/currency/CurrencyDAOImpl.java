package org.pshhs.dao.currency;

import lombok.extern.slf4j.Slf4j;
import org.pshhs.db.HikariDataSourceConnection;
import org.pshhs.Utils;
import org.pshhs.model.Currency;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Slf4j
public class CurrencyDAOImpl implements CurrencyDAO {
    public CurrencyDAOImpl() {
    }

    public Optional<Currency> getCurrencyByCode(String codeCurrency) throws SQLException {
        Currency currency = null;
        String get_currency_by_code = """
                SELECT * FROM currency WHERE code = ?
                """;
        log.debug("getCurrencyByCode \"{}\" ",codeCurrency);
        try (var connection = HikariDataSourceConnection.getConnection();
             var preparedStatement = connection.prepareStatement(get_currency_by_code)) {
            preparedStatement.setString(1, codeCurrency);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    currency = Utils.getCurrency(resultSet);
                }
            }
        }
        return Optional.ofNullable(currency);
    }

    @Override
    public Optional<Currency> create(Currency currency) throws SQLException {
        final String query = """
                INSERT INTO currency (code,full_name,sign)
                values (?,?,?)
                """;
        if (getCurrencyByCode(currency.getCode()).isPresent())
            throw new IllegalArgumentException("Валюта с данным кодом \"%s\" существует.".formatted(currency.getCode()));

        try (var connection = HikariDataSourceConnection.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getName());
            preparedStatement.setString(3, currency.getSign());
            int rows = preparedStatement.executeUpdate();
            if (rows == 1) {
                return getCurrencyByCode(currency.getCode());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Currency> update(Currency entity) {
        return Optional.empty();
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }


    @Override
    public List<Currency> findAll() throws SQLException {
        List<Currency> allCurrencies = new ArrayList<>();
        String query = "SELECT * FROM currency";

        try (var connection = HikariDataSourceConnection.getConnection();
             var resulSet = connection.prepareStatement(query).executeQuery()) {
            while (resulSet.next()) {
                allCurrencies.add(Utils.getCurrency(resulSet));
            }
        }
        return allCurrencies;
    }

}

