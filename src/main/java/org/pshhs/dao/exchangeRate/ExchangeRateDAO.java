package org.pshhs.dao.exchangeRate;

import org.pshhs.dao.GenericDAO;
import org.pshhs.model.ExchangeRate;

import java.sql.SQLException;
import java.util.Optional;

public interface ExchangeRateDAO extends GenericDAO<ExchangeRate, Long> {
    Optional<ExchangeRate> findExchangeRateByCode(String baseCurrencyCode, String targetCurrencyCode) throws SQLException;
    Optional<ExchangeRate> findAllBaseUsdExchangeRate() throws SQLException;
}
