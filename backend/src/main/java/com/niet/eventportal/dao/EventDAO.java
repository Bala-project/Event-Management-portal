package com.niet.eventportal.dao;

import com.niet.eventportal.config.DBConnection;
import com.niet.eventportal.model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all CRUD operations for events.
 * Also creates/drops a DEDICATED registration table for every event
 * (as requested: "provide separate table for each event, column names
 * = the details filled by the user while registering").
 */
public class EventDAO {

    /** Returns all events, most recently added first is not required - ordered by date. */
    public List<Event> getAllEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY event_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                events.add(mapRow(rs));
            }
        }
        return events;
    }

    public Event getEventById(int id) throws SQLException {
        String sql = "SELECT * FROM events WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /**
     * Adds a new event AND creates its dedicated registration table.
     * Table name is derived from the event name (sanitized + unique suffix).
     */
    public boolean addEvent(Event event) throws SQLException {
        String tableName = generateTableName(event.getName());
        event.setTableName(tableName);

        String insertSql = "INSERT INTO events (name, description, event_date, venue, image_url, table_name) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, event.getName());
                ps.setString(2, event.getDescription());
                ps.setString(3, event.getEventDate());
                ps.setString(4, event.getVenue());
                ps.setString(5, event.getImageUrl());
                ps.setString(6, tableName);
                int rows = ps.executeUpdate();

                if (rows > 0) {
                    // Create the dedicated registration table for this event.
                    // Columns exactly match the registration form fields.
                    try (Statement stmt = conn.createStatement()) {
                        String createTableSql =
                            "CREATE TABLE IF NOT EXISTS `" + tableName + "` (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "name VARCHAR(100) NOT NULL," +
                            "email VARCHAR(100) NOT NULL," +
                            "phone VARCHAR(15) NOT NULL," +
                            "department VARCHAR(50) NOT NULL," +
                            "year VARCHAR(20) NOT NULL," +
                            "registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "UNIQUE KEY unique_email_per_event (email)" +
                            ")";
                        stmt.executeUpdate(createTableSql);
                    }
                    conn.commit();
                    return true;
                }
                conn.rollback();
                return false;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /** Deletes the event row AND drops its dedicated registration table. */
    public boolean deleteEvent(int id) throws SQLException {
        Event event = getEventById(id);
        if (event == null) return false;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DROP TABLE IF EXISTS `" + event.getTableName() + "`");
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM events WHERE id = ?")) {
                ps.setInt(1, id);
                int rows = ps.executeUpdate();
                conn.commit();
                return rows > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /** Converts an event name into a safe, unique MySQL table name, e.g. "Annual Day 2026" -> reg_annual_day_2026 */
    private String generateTableName(String eventName) {
        String base = "reg_" + eventName.toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
        return base + "_" + System.currentTimeMillis() % 100000; // suffix keeps names unique
    }

    private Event mapRow(ResultSet rs) throws SQLException {
        return new Event(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("event_date").toString(),
                rs.getString("venue"),
                rs.getString("image_url"),
                rs.getString("table_name")
        );
    }
}
