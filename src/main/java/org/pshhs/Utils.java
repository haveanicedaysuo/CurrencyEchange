package org.pshhs;

import org.pshhs.model.Currency;
import org.pshhs.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Utils {

    public static Currency getCurrency(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong(1);
        String code = resultSet.getString(2);
        String fullName = resultSet.getString(3);
        String sign = resultSet.getString(4);
        return new Currency(id, code, fullName, sign);

    }

    public static ExchangeRate getExchangeRateFromResultSet(ResultSet resultSet) throws SQLException {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setId(resultSet.getLong("id"));
        exchangeRate.setBaseCurrency(
                new Currency(
                        resultSet.getLong("baseCurID"),
                        resultSet.getString("baseCode"),
                        resultSet.getString("baseName"),
                        resultSet.getString("baseSign")
                ));
        exchangeRate.setTargetCurrency(
                new Currency(
                        resultSet.getLong("targetCurID"),
                        resultSet.getString("targetCode"),
                        resultSet.getString("targetName"),
                        resultSet.getString("targetSign")
                ));
        exchangeRate.setRate(resultSet.getBigDecimal("Rate"));
        return exchangeRate;
    }

    public static boolean isNotValidExchangeRateParams(String baseCur, String targetCur, String rate) {
        return !(baseCur != null && baseCur.length() == 3 &&
                targetCur != null && targetCur.length() == 3 && rate != null);
    }

    public static boolean isNotValidExchangeParams(String baseCur, String targetCur, BigDecimal amount) {
        return !(baseCur != null && baseCur.length() == 3 &&
                targetCur != null && targetCur.length() == 3 && amount != null || amount.compareTo(BigDecimal.ZERO) <= 0);
    }

}
