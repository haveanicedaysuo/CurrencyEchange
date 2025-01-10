package org.pshhs.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pshhs.Utils;
import org.pshhs.dto.ExchangeRateDTO;
import org.pshhs.exception.ErrorMessage;
import org.pshhs.service.ExchangeService;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(urlPatterns = "/exchange/*")
public class ExchangeServlet extends BaseHttpServlet {
    ExchangeService exchangeService;


    @Override
    public void init(ServletConfig config) {
        exchangeService = new ExchangeService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String from = req.getParameter("from").toUpperCase();
        String to = req.getParameter("to").toUpperCase();
        BigDecimal amount = new BigDecimal(req.getParameter("amount"));
        //todo check params
        if (Utils.isNotValidExchangeParams(from, to,amount)) {
            sendMessage(resp,HttpServletResponse.SC_BAD_REQUEST,"Некорректные данные");
        }
            try {
                Optional<ExchangeRateDTO> exchangeRate = exchangeService.getExchangeRate(from, to, amount);
                if (exchangeRate.isPresent()) {
                    sendMessage(resp, HttpServletResponse.SC_OK, exchangeRate);
                    return;
                }
                sendMessage(resp, HttpServletResponse.SC_NOT_FOUND, new ErrorMessage("Обменный курс не найден"));
            } catch (SQLException e) {
                sendMessage(resp, e.getErrorCode(), new ErrorMessage(e.getMessage()));
            }
    }
}
