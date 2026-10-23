package com.niet.eventportal.servlet;

import com.niet.eventportal.dao.RegistrationDAO;
import com.niet.eventportal.model.Registration;
import com.niet.eventportal.util.ResponseUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/** POST /api/register -> { eventId, name, email, phone, department, year } */
@WebServlet("/api/register")
public class RegistrationServlet extends HttpServlet {

    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Registration reg = ResponseUtil.gson().fromJson(readBody(req), Registration.class);

        if (reg == null || reg.getName() == null || reg.getEmail() == null ||
                reg.getPhone() == null || reg.getDepartment() == null || reg.getYear() == null) {
            ResponseUtil.sendJson(resp, 400, error("All fields are required."));
            return;
        }

        try {
            boolean ok = registrationDAO.register(reg);
            if (ok) {
                ResponseUtil.sendJson(resp, 200, success("Registered successfully."));
            } else {
                ResponseUtil.sendJson(resp, 409, error("You have already registered for this event, or the event does not exist."));
            }
        } catch (SQLException e) {
            ResponseUtil.sendJson(resp, 500, error("Database error: " + e.getMessage()));
        }
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private Map<String, Object> success(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("success", true);
        m.put("message", msg);
        return m;
    }

    private Map<String, Object> error(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("success", false);
        m.put("message", msg);
        return m;
    }
}
