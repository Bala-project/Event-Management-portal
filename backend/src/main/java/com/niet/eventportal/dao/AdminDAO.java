package com.niet.eventportal.dao;

import com.niet.eventportal.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    /** Checks the admin table for a matching username/password pair. */
    public boolean validateAdmin(String username, String password) throws SQLException {
        String sql = "SELECT id FROM admin WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password); // NOTE: for production, store & compare hashed passwords (e.g. BCrypt)
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
