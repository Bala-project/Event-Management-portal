package com.niet.eventportal.dao;

import com.niet.eventportal.config.DBConnection;
import com.niet.eventportal.model.Event;
import com.niet.eventportal.model.Registration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDAO {

    private final EventDAO eventDAO = new EventDAO();

    /** Registers a student into the dedicated table for the given event. */
    public boolean register(Registration reg) throws SQLException {
        Event event = eventDAO.getEventById(reg.getEventId());
        if (event == null) return false;

        String sql = "INSERT INTO `" + event.getTableName() +
                "` (name, email, phone, department, year) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reg.getName());
            ps.setString(2, reg.getEmail());
            ps.setString(3, reg.getPhone());
            ps.setString(4, reg.getDepartment());
            ps.setString(5, reg.getYear());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException dup) {
            // Same email already registered for this event
            return false;
        }
    }

    /** Returns every event a given email has registered for, by scanning each event's dedicated table. */
    public List<Registration> getRegistrationsByEmail(String email) throws SQLException {
        List<Registration> results = new ArrayList<>();
        List<Event> events = eventDAO.getAllEvents();

        try (Connection conn = DBConnection.getConnection()) {
            for (Event event : events) {
                String sql = "SELECT * FROM `" + event.getTableName() + "` WHERE email = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, email);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            Registration reg = mapRow(rs);
                            reg.setEventName(event.getName());
                            results.add(reg);
                        }
                    }
                } catch (SQLException tableMissing) {
                    // Table may not exist for legacy/edge cases - skip silently
                }
            }
        }
        return results;
    }

    /** Returns the full registration list for a single event (used by Admin > View Registered Students). */
    public List<Registration> getRegistrationsByEvent(int eventId) throws SQLException {
        List<Registration> results = new ArrayList<>();
        Event event = eventDAO.getEventById(eventId);
        if (event == null) return results;

        String sql = "SELECT * FROM `" + event.getTableName() + "` ORDER BY registered_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Registration reg = mapRow(rs);
                reg.setEventName(event.getName());
                results.add(reg);
            }
        }
        return results;
    }

    private Registration mapRow(ResultSet rs) throws SQLException {
        Registration reg = new Registration();
        reg.setId(rs.getInt("id"));
        reg.setName(rs.getString("name"));
        reg.setEmail(rs.getString("email"));
        reg.setPhone(rs.getString("phone"));
        reg.setDepartment(rs.getString("department"));
        reg.setYear(rs.getString("year"));
        reg.setRegisteredAt(rs.getString("registered_at"));
        return reg;
    }
}
