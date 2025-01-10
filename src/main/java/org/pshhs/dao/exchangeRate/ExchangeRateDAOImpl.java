package org.pshhs.dao.exchangeRate;

import lombok.extern.slf4j.Slf4j;
import org.pshhs.db.HikariDataSourceConnection;
import org.pshhs.Utils;
import org.pshhs.model.ExchangeRate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ExchangeRateDAOImpl implements ExchangeRateDAO {

    @Override
    public Optional<ExchangeRate> findExchangeRateByCode(String baseCur, String targetCur) throws SQLException {
       String get_exchange_rate_by_code = """
                SELECT er.id AS id,
                bc.id AS baseCurID,
                bc.full_name AS baseName,
                bc.code AS baseCode,
                bc.sign AS baseSign,
                tc.id AS targetCurID,
                tc.full_name AS targetName,
                tc.code AS targetCode,
                tc.sign AS targetSign,
                er.rate AS Rate
                from exchange_rate as er
                join currency bc on er.base_currency_id = bc.id
                join currency tc on er.target_currency_id = tc.id
                where bc.code = ? and tc.code = ?
                """;

        try (var connection = HikariDataSourceConnection.getConnection();
             var ps = connection.prepareStatement(get_exchange_rate_by_code)) {
            ps.setString(1, baseCur);
            ps.setString(2, targetCur);
            var resultSet = ps.executeQuery();
            if (resultSet.next()) {
                var exchangeRate = Utils.getExchangeRateFromResultSet(resultSet);
                return Optional.of(exchangeRate);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ExchangeRate> findAllBaseUsdExchangeRate() throws SQLException {
        String get_exchange_rate_by_code = """
                SELECT er.id AS id,
                bc.id AS baseCurID,
                bc.full_name AS baseName,
                bc.code AS baseCode,
                bc.sign AS baseSign,
                tc.id AS targetCurID,
                tc.full_name AS targetName,
                tc.code AS targetCode,
                tc.sign AS targetSign,
                er.rate AS Rate
                from exchange_rate as er
                join currency bc on er.base_currency_id = bc.id
                join currency tc on er.target_currency_id = tc.id
                where bc.code = 'USD'
                """;

        try (var connection = HikariDataSourceConnection.getConnection();
             var ps = connection.prepareStatement(get_exchange_rate_by_code)) {
            var resultSet = ps.executeQuery();
            if (resultSet.next()) {
                var exchangeRate = Utils.getExchangeRateFromResultSet(resultSet);
                return Optional.of(exchangeRate);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ExchangeRate> update(ExchangeRate exchangeRate) throws SQLException {
        String updateExchangeRateQuery = """
                update exchange_rate set rate = ?
                where base_currency_id = ? and target_currency_id = ?
                """;

        try (var connection = HikariDataSourceConnection.getConnection();
             var ps = connection.prepareStatement(updateExchangeRateQuery)) {
            ps.setBigDecimal(1, exchangeRate.getRate());
            ps.setLong(2, exchangeRate.getBaseCurrency().getId());
            ps.setLong(3, exchangeRate.getTargetCurrency().getId());
            int isUpdate = ps.executeUpdate();
            if (isUpdate == 0) {
               return Optional.empty();
            }
            return findExchangeRateByCode(
                    exchangeRate.getBaseCurrency().getCode(),
                    exchangeRate.getTargetCurrency().getCode()
            );
        }
    }

    @Override
    public List<ExchangeRate> findAll() throws SQLException {
        List<ExchangeRate> allExchangeRates = new ArrayList<>();
        String getAllExchangeRatesQuery = """
                SELECT er.id AS id,
                bc.id AS baseCurID,
                bc.full_name AS baseName,
                bc.code AS baseCode,
                bc.sign AS baseSign,
                tc.id AS targetCurID,
                tc.full_name AS targetName,
                tc.code AS targetCode,
                tc.sign AS targetSign,
                er.rate AS Rate
                FROM exchange_rate AS er
                JOIN currency bc ON er."base_currency_id" = bc.id
                JOIN currency tc ON er."target_currency_id" = tc.id;
                """;

        try (var connection = HikariDataSourceConnection.getConnection();
             var preparedStatement = connection.prepareStatement(getAllExchangeRatesQuery);
             var resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                ExchangeRate erDTO = Utils.getExchangeRateFromResultSet(resultSet);
                allExchangeRates.add(erDTO);
            }
        }
        return allExchangeRates;
    }

    @Override
    public Optional<ExchangeRate> create(ExchangeRate entity) throws SQLException {
        final String query = "INSERT INTO exchange_rate (base_currency_id, target_currency_id, rate) " +
                "VALUES (?, ?, ?) RETURNING *";

        var exchangeRate = new ExchangeRate();

        try (var connection = HikariDataSourceConnection.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, entity.getBaseCurrency().getId());
            preparedStatement.setLong(2, entity.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3, entity.getRate());
            var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                exchangeRate.setId(resultSet.getLong("Id"));
            }
            exchangeRate.setBaseCurrency(entity.getBaseCurrency());
            exchangeRate.setTargetCurrency(entity.getTargetCurrency());
            exchangeRate.setRate(entity.getRate());
        }
        return Optional.of(exchangeRate);
    }


    @Override
    public boolean delete(Long aLong) {
        return false;
    }

}
