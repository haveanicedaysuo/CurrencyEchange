package org.pshhs.dao.currency;

import org.pshhs.dao.GenericDAO;
import org.pshhs.model.Currency;

import java.sql.SQLException;
import java.util.Optional;

public interface CurrencyDAO extends GenericDAO<Currency,Long> {
    Optional<Currency> getCurrencyByCode(String code) throws SQLException;
}
