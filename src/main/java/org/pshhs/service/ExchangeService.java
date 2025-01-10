package org.pshhs.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.pshhs.dao.exchangeRate.ExchangeRateDAOImpl;
import org.pshhs.dto.ExchangeRateDTO;
import org.pshhs.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j
public class ExchangeService {
    private final ExchangeRateDAOImpl exchangeRateDAO;

    public ExchangeService() {
        this.exchangeRateDAO = new ExchangeRateDAOImpl();
    }

    public Optional<ExchangeRateDTO> getExchangeRate(String baseCurrencyStr, String targetCurrencyStr, BigDecimal amount)
            throws SQLException {
        Optional<ExchangeRate> exchangeRate =
                getByDirectExchangeRate(baseCurrencyStr, targetCurrencyStr);

        if (exchangeRate.isPresent()) {
            var convertedAmount = getConvertedAmount(exchangeRate, amount);
            return Optional.ofNullable(convertedAmount);
        }
        //reverse exchange
        exchangeRate =
                getByReversExchangeRate(baseCurrencyStr, targetCurrencyStr);

        if (exchangeRate.isPresent()) {
            var convertedAmount = getConvertedAmount(exchangeRate, amount);
            return Optional.ofNullable(convertedAmount);
        }
        exchangeRate = getFromCrossExchangeRate(baseCurrencyStr, targetCurrencyStr);
        if (exchangeRate.isPresent()) {
            var convertedAmount = getConvertedAmount(exchangeRate, amount);
            return Optional.ofNullable(convertedAmount);
        }
        return Optional.empty();
    }

    private ExchangeRateDTO getConvertedAmount(Optional<ExchangeRate> exchangeRate, BigDecimal amount) {
        return ExchangeRateDTO.builder().
                targetCurrency(exchangeRate.get().getTargetCurrency()).
                baseCurrency(exchangeRate.get().getBaseCurrency()).
                rate(exchangeRate.get().getRate()).
                amount(amount).
                convertedAmount(amount.multiply(exchangeRate.get().getRate().setScale(2, RoundingMode.HALF_EVEN))).build();
    }

    private Optional<ExchangeRate> getByDirectExchangeRate(String baseCurrencyStr, String targetCurrencyStr) throws SQLException {
        return exchangeRateDAO.findExchangeRateByCode(baseCurrencyStr, targetCurrencyStr);
    }

    private Optional<ExchangeRate> getByReversExchangeRate(String baseCurrencyStr, String targetCurrencyStr) throws SQLException {
        Optional<ExchangeRate> exchangeRateByCode = exchangeRateDAO.findExchangeRateByCode(targetCurrencyStr, baseCurrencyStr);
        log.debug("\nFind revers exchange rate \n{}", exchangeRateByCode);
        if (exchangeRateByCode.isPresent()) {
            BigDecimal divide = new BigDecimal(1).divide(exchangeRateByCode.get().getRate(), 4, RoundingMode.HALF_EVEN);
            return Optional.ofNullable(ExchangeRate.builder().
                    baseCurrency(exchangeRateByCode.get().getTargetCurrency()).
                    targetCurrency(exchangeRateByCode.get().getBaseCurrency()).
                    rate(divide).
                    build());
        }
        return Optional.empty();
    }

    private Optional<ExchangeRate> getFromCrossExchangeRate(String baseCurrencyStr, String targetCurrencyStr) throws SQLException {
        Optional<ExchangeRate> exchangeRateUsdTarget = exchangeRateDAO.findExchangeRateByCode("USD", targetCurrencyStr);
        Optional<ExchangeRate> exchangeRateUsdBase = exchangeRateDAO.findExchangeRateByCode("USD", baseCurrencyStr);
        if (exchangeRateUsdBase.isPresent() && exchangeRateUsdTarget.isPresent()) {
            var target = exchangeRateUsdTarget.get().getRate();
            var base = exchangeRateUsdBase.get().getRate();
            var rate = target.divide(base, 4, RoundingMode.HALF_EVEN);

            return Optional.ofNullable(ExchangeRate.builder().
                    baseCurrency(exchangeRateUsdBase.get().getTargetCurrency()).
                    targetCurrency(exchangeRateUsdTarget.get().getTargetCurrency()).
                    rate(rate).build());
        }
        return Optional.empty();
    }
}
