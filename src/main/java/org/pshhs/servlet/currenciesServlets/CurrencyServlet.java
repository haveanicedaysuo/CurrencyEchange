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
import java.util.NoSuchElementException;
import java.util.Optional;

@WebServlet("/api/currency/*")
public class CurrencyServlet extends BaseHttpServlet {
    CurrencyDAOImpl currencyDAOImpl;

    @Override
    public void init(ServletConfig config) {
        currencyDAOImpl = new CurrencyDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
         String pathInfo = req.getPathInfo().replace("/", "").toUpperCase();
        if (pathInfo.length() != 3) {
            sendMessage(resp, HttpServletResponse.SC_BAD_REQUEST,
                    new ErrorMessage("Код валюты отсутствует в адресе"));
            return;
        }
        String  code = pathInfo.replace("/", "").toUpperCase();
        try {
            Optional<Currency> currency = currencyDAOImpl.getCurrencyByCode(code);

            if (currency.isEmpty()) {
                sendMessage(resp, HttpServletResponse.SC_NOT_FOUND, new ErrorMessage("Валюта не найдена"));
                return;
            }
            sendMessage(resp,HttpServletResponse.SC_OK,currency.get());

        } catch (SQLException e) {
            sendMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ErrorMessage("Ошибка баз данных"));
        }

    }

}
