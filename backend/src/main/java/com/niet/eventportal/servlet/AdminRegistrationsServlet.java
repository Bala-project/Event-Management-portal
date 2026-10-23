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

/** GET /api/admin/registrations?eventId=1 -> full registration list for that event (admin only) */
@WebServlet("/api/admin/registrations")
public class AdminRegistrationsServlet extends HttpServlet {

    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!AuthUtil.isAdminAuthenticated(req)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ResponseUtil.sendJson(resp, 401, Collections.emptyList());
            return;
        }
        String eventIdParam = req.getParameter("eventId");
        if (eventIdParam == null) {
            ResponseUtil.sendJson(resp, 400, Collections.emptyList());
            return;
        }
        try {
            List<Registration> regs = registrationDAO.getRegistrationsByEvent(Integer.parseInt(eventIdParam));
            ResponseUtil.sendJson(resp, 200, regs);
        } catch (SQLException e) {
            ResponseUtil.sendJson(resp, 500, Collections.emptyList());
        }
    }
}
