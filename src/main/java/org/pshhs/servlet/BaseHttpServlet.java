package org.pshhs.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class BaseHttpServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        super.service(req, resp);
    }

    public <T> void sendMessage(HttpServletResponse resp, int respCode, T message) throws IOException {
        String jsonMessage = gson.toJson(message);
        resp.setStatus(respCode);
        resp.getWriter().println(jsonMessage);
        resp.getWriter().flush();
    }
}
