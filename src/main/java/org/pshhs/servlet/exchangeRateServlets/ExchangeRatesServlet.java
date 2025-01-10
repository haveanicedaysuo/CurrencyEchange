package org.pshhs.servlet.exchangeRateServlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.pshhs.Utils;
import org.pshhs.dao.currency.CurrencyDAOImpl;
import org.pshhs.dao.exchangeRate.ExchangeRateDAOImpl;
import org.pshhs.exception.ErrorMessage;
import org.pshhs.model.Currency;
import org.pshhs.model.ExchangeRate;
import org.pshhs.servlet.BaseHttpServlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
@Slf4j
@WebServlet(urlPatterns = "/api/exchangeRates")
public class ExchangeRatesServlet extends BaseHttpServlet {
    ExchangeRateDAOImpl exchangeRateDAO;
    CurrencyDAOImpl currencyDAO;

    @Override
    public void init(ServletConfig config) {
        exchangeRateDAO = new ExchangeRateDAOImpl();
        currencyDAO = new CurrencyDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            log.info("GET: /exchangeRate " + req.getRequestURI());
            List<ExchangeRate> allExchangeRates = exchangeRateDAO.findAll();
            sendMessage(resp, HttpServletResponse.SC_OK, allExchangeRates);

        } catch (SQLException e) {
            sendMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка базы данных ");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode").toUpperCase();
        String targetCurrencyCode = req.getParameter("targetCurrencyCode").toUpperCase();
        String rateParam = req.getParameter("rate");

        log.info("POST: /exchangeRate " + req.getRequestURI() + " " + baseCurrencyCode + " " + targetCurrencyCode
                + " " + rateParam);

        if (Utils.isNotValidExchangeRateParams(baseCurrencyCode,
                targetCurrencyCode, rateParam)) {
            sendMessage(resp, HttpServletResponse.SC_BAD_REQUEST,
                    new ErrorMessage("Отсутствует нужное поле формы или неверный формат данных"));
            return;
        }
        try {
            BigDecimal rate = new BigDecimal(rateParam);
            Optional<Currency> baseCurrency = currencyDAO.getCurrencyByCode(baseCurrencyCode);
            if (baseCurrency.isEmpty()) {
                sendMessage(resp, HttpServletResponse.SC_CONFLICT,
                        new ErrorMessage("%s валюта не существует в БД".formatted(baseCurrencyCode)));
                return;
            }

            Optional<Currency> targetCurrency = currencyDAO.getCurrencyByCode(targetCurrencyCode);
            if (targetCurrency.isEmpty()) {
                sendMessage(resp, HttpServletResponse.SC_CONFLICT,
                        new ErrorMessage("%s валюта не существует в БД".formatted(targetCurrencyCode)));
                return;
            }

            Optional<ExchangeRate> exchangeRateByCode = exchangeRateDAO.
                    findExchangeRateByCode(baseCurrencyCode, targetCurrencyCode);

            if (exchangeRateByCode.isPresent()) {
                sendMessage(resp, HttpServletResponse.SC_CONFLICT,
                        new ErrorMessage("Валютная пара с таким кодом уже существует"));
                return;
            }
            Optional<ExchangeRate> exchangeRate = exchangeRateDAO.create(
                    ExchangeRate.builder().
                            baseCurrency(baseCurrency.get()).
                            targetCurrency(targetCurrency.get()).
                            rate(rate).
                            build());
            sendMessage(resp, HttpServletResponse.SC_CREATED, exchangeRate);

        } catch (SQLException e) {
            sendMessage(resp,e.getErrorCode(),
                    new ErrorMessage(e.getMessage()));

        }
    }
}
