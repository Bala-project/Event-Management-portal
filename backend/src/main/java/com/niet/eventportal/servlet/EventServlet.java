package com.niet.eventportal.servlet;

import com.niet.eventportal.dao.EventDAO;
import com.niet.eventportal.model.Event;
import com.niet.eventportal.util.ResponseUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GET    /api/events         -> list all events (public, used by Home / Events pages)
 * GET    /api/events?id=1    -> get single event
 * POST   /api/events         -> add a new event (admin only) - also creates its own reg table
 * DELETE /api/events?id=1    -> delete an event (admin only) - also drops its reg table
 */
@WebServlet("/api/events")
public class EventServlet extends HttpServlet {

    private final EventDAO eventDAO = new EventDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idParam = req.getParameter("id");
            if (idParam != null) {
                Event event = eventDAO.getEventById(Integer.parseInt(idParam));
                ResponseUtil.sendJson(resp, 200, event);
            } else {
                List<Event> events = eventDAO.getAllEvents();
                ResponseUtil.sendJson(resp, 200, events);
            }
        } catch (SQLException e) {
            ResponseUtil.sendJson(resp, 500, error("Database error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!AuthUtil.isAdminAuthenticated(req)) {
            ResponseUtil.sendJson(resp, 401, error("Unauthorized. Admin login required."));
            return;
        }
        try {
            Event event = ResponseUtil.gson().fromJson(readBody(req), Event.class);
            if (event == null || event.getName() == null || event.getName().isBlank()) {
                ResponseUtil.sendJson(resp, 400, error("Event name is required."));
                return;
            }
            boolean ok = eventDAO.addEvent(event);
            ResponseUtil.sendJson(resp, ok ? 200 : 500,
                    ok ? success("Event added.") : error("Could not add event."));
        } catch (SQLException e) {
            ResponseUtil.sendJson(resp, 500, error("Database error: " + e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!AuthUtil.isAdminAuthenticated(req)) {
            ResponseUtil.sendJson(resp, 401, error("Unauthorized. Admin login required."));
            return;
        }
        String idParam = req.getParameter("id");
        if (idParam == null) {
            ResponseUtil.sendJson(resp, 400, error("Event id is required."));
            return;
        }
        try {
            boolean ok = eventDAO.deleteEvent(Integer.parseInt(idParam));
            ResponseUtil.sendJson(resp, ok ? 200 : 404,
                    ok ? success("Event deleted.") : error("Event not found."));
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
