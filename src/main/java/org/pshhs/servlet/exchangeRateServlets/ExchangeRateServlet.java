package org.pshhs.servlet.exchangeRateServlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
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
import java.util.Optional;

@Slf4j
@WebServlet(urlPatterns = "/api/exchangeRate/*")
public class ExchangeRateServlet extends BaseHttpServlet {
    ExchangeRateDAOImpl exchangeRateDAOImpl;
    CurrencyDAOImpl currencyDAOImpl;

    @Override
    public void init(ServletConfig config) {
        exchangeRateDAOImpl = new ExchangeRateDAOImpl();
        currencyDAOImpl = new CurrencyDAOImpl();

    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Entering service method %s".formatted(req.getMethod()));
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            log.info(req.getRequestURI());
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        pathInfo = pathInfo.replace("/", "").toUpperCase();
        log.info("GET:/exchangeRate/*  " + " pathInfo: " + pathInfo);
        if (pathInfo.length() != 6) {
            sendMessage(resp, HttpServletResponse.SC_BAD_REQUEST,
                    new ErrorMessage("Коды валют пары отсутствуют в адресе"));
            return;
        }
        String baseCur = pathInfo.substring(0, 3).toUpperCase();
        String targetCur = pathInfo.substring(3, 6).toUpperCase();
        Optional<ExchangeRate> exchangeRateByCode;
        try {
            exchangeRateByCode = exchangeRateDAOImpl.findExchangeRateByCode(baseCur, targetCur);
            if (exchangeRateByCode.isEmpty()) {
                sendMessage(resp,
                        HttpServletResponse.SC_NOT_FOUND,
                        new ErrorMessage("Обменный курс для пары \"%s %s\" не найден".formatted(baseCur, targetCur)));
                return;
            }
            sendMessage(resp, HttpServletResponse.SC_OK, exchangeRateByCode);
        } catch (SQLException e) {
            sendMessage(resp,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ErrorMessage("Ошибка БД"));
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo().replace("/", "").toUpperCase();
        String baseCurrencyParam = pathInfo.substring(0, 3).toUpperCase();
        String targetCurrencyParam = pathInfo.substring(3, 6).toUpperCase();
        String rateParam = req.getParameter("rate");
        log.info("PUT:/exchangeRate/*  " + " pathInfo: " + pathInfo);

        if (Utils.isNotValidExchangeRateParams(baseCurrencyParam, targetCurrencyParam, rateParam)) {
            sendMessage(resp, HttpServletResponse.SC_BAD_REQUEST,
                    new ErrorMessage("Отсутствует нужное поле формы"));
        }
        try {
            BigDecimal rate = new BigDecimal(rateParam);
            Optional<Currency> baseCurrency = currencyDAOImpl.getCurrencyByCode(baseCurrencyParam);
            if (baseCurrency.isEmpty()) {
                sendMessage(resp, HttpServletResponse.SC_CONFLICT,
                        new ErrorMessage("%s валюта не существует в БД".formatted(baseCurrencyParam)));
                return;
            }

            Optional<Currency> targetCurrency = currencyDAOImpl.getCurrencyByCode(targetCurrencyParam);
            if (targetCurrency.isEmpty()) {
                sendMessage(resp, HttpServletResponse.SC_CONFLICT,
                        new ErrorMessage("%s валюта не существует в БД".formatted(targetCurrencyParam)));
                return;
            }
            Optional<ExchangeRate> exchangeRate =
                    exchangeRateDAOImpl.update(
                            ExchangeRate.builder().
                                    baseCurrency(baseCurrency.get()).
                                    targetCurrency(targetCurrency.get()).
                                    rate(rate).build());
            if (exchangeRate.isPresent()) {
                sendMessage(resp, HttpServletResponse.SC_CREATED,
                        exchangeRate);
            }

        } catch (NumberFormatException e) {
            sendMessage(resp, HttpServletResponse.SC_BAD_REQUEST,
                    new ErrorMessage("Неверный формат данных в поле rate"));
        } catch (IllegalArgumentException e) {
            sendMessage(resp, HttpServletResponse.SC_NOT_FOUND,
                    new ErrorMessage("Валютная пара отсутствует в базе данных"));
        } catch (SQLException e) {
            sendMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ErrorMessage("Ошибка базы данных "));
        }

    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo().replace("/", "");
            String rate = req.getParameter("rate").strip();
            log.info("PATCH:/exchangeRate/* pathInfo:{}  rate:{} ", pathInfo, rate);
            if (pathInfo == null || pathInfo.length() != 6 || rate.isEmpty()) {
                sendMessage(resp, HttpServletResponse.SC_BAD_REQUEST,
                        new ErrorMessage("Отсутствует нужное поле формы"));
                return;
            }

            String baseCur = pathInfo.substring(0, 3).toUpperCase();
            String targetCur = pathInfo.substring(3, 6).toUpperCase();


            Optional<ExchangeRate> exchangeByCode = exchangeRateDAOImpl.findExchangeRateByCode(baseCur, targetCur);
            if (exchangeByCode.isEmpty()) {
                sendMessage(resp, HttpServletResponse.SC_NOT_FOUND,
                        new ErrorMessage("Валютная пара отсутствует в базе данных"));
                return;
            }
            exchangeByCode.get().setRate(new BigDecimal(rate));
            Optional<ExchangeRate> exchangeRate = exchangeRateDAOImpl.update(exchangeByCode.get());
            if (!exchangeRate.isPresent()) {
                sendMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        new ErrorMessage("Ошибка базы данных "));
            }
            sendMessage(resp, HttpServletResponse.SC_OK, exchangeRate);
        } catch (SQLException e) {
            sendMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ErrorMessage("Ошибка базы данных "));
        } catch (Exception e){
            sendMessage(resp,HttpServletResponse.SC_BAD_REQUEST,e.getMessage());
        }

    }
}

