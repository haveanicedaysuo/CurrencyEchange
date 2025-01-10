package org.pshhs.servlet.currenciesServlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pshhs.dao.currency.CurrencyDAOImpl;
import org.pshhs.exception.ErrorMessage;
import org.pshhs.model.Currency;
import org.pshhs.servlet.BaseHttpServlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = "/api/currencies")
public class CurrenciesServlet extends BaseHttpServlet {
    CurrencyDAOImpl currencyDAOImpl;

    @Override
    public void init(ServletConfig config) {
        currencyDAOImpl = new CurrencyDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<Currency> allCurrencies = currencyDAOImpl.findAll();
            sendMessage(resp, HttpServletResponse.SC_OK, allCurrencies);
        } catch (SQLException e) {
            sendMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ErrorMessage("Ошибка БД"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name").toUpperCase();
        String code = req.getParameter("code").toUpperCase();
        String sign = req.getParameter("sign").toUpperCase();

        try {
            checkRequestParamsCurrency(name, code, sign);
            Optional<Currency> currency = currencyDAOImpl.create(Currency.builder().
                    name(name).
                    code(code).
                    sign(sign).
                    build());
            sendMessage(resp, HttpServletResponse.SC_CREATED,
                    "Валюта %s создана".formatted(currency.get()));

        } catch (IllegalArgumentException e) {
            sendMessage(resp, HttpServletResponse.SC_BAD_REQUEST,
                    new ErrorMessage(e.getMessage()));
        } catch (SQLException e) {
            sendMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ErrorMessage("Ошибка базы данных"));
        }
    }

    private void checkRequestParamsCurrency(String name, String code, String sign) {
        if (name == null || name.length() < 3 || code == null || code.length() > 3 || sign == null
                || sign.length() > 3 || sign.isEmpty())
            throw new IllegalArgumentException("Не корректные данные.");
    }
}
