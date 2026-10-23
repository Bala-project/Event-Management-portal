package com.niet.eventportal.servlet;

import com.niet.eventportal.dao.AdminDAO;
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

/** POST /api/admin/login -> { username, password } => { success, token } */
@WebServlet("/api/admin/login")
public class AdminLoginServlet extends HttpServlet {

    private final AdminDAO adminDAO = new AdminDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<?, ?> body = ResponseUtil.gson().fromJson(readBody(req), Map.class);
        String username = body != null ? String.valueOf(body.get("username")) : null;
        String password = body != null ? String.valueOf(body.get("password")) : null;

        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            ResponseUtil.sendJson(resp, 400, errorResponse("Admin ID and password are required."));
            return;
        }

        try {
            boolean valid = adminDAO.validateAdmin(username, password);
            if (valid) {
                String token = AuthUtil.issueToken();
                Map<String, Object> res = new HashMap<>();
                res.put("success", true);
                res.put("token", token);
                ResponseUtil.sendJson(resp, 200, res);
            } else {
                ResponseUtil.sendJson(resp, 401, errorResponse("Invalid Admin ID or Password."));
            }
        } catch (SQLException e) {
            ResponseUtil.sendJson(resp, 500, errorResponse("Database error: " + e.getMessage()));
        }
    }

    private Map<String, Object> errorResponse(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("success", false);
        m.put("message", msg);
        return m;
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }
}
