package com.niet.eventportal.servlet;

import com.niet.eventportal.dao.RegistrationDAO;
import com.niet.eventportal.model.Registration;
import com.niet.eventportal.util.ResponseUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/** GET /api/registered?email=someone@example.com -> list of that student's registered events */
@WebServlet("/api/registered")
public class RegisteredEventsServlet extends HttpServlet {

    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        if (email == null || email.isBlank()) {
            ResponseUtil.sendJson(resp, 200, Collections.emptyList());
            return;
        }
        try {
            List<Registration> regs = registrationDAO.getRegistrationsByEmail(email.trim());
            ResponseUtil.sendJson(resp, 200, regs);
        } catch (SQLException e) {
            ResponseUtil.sendJson(resp, 500, Collections.emptyList());
        }
    }
}
