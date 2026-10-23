package com.niet.eventportal.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Changed to jdbc:mysql:// and added verifyServerCertificate=false
    private static final String DB_URL = getEnv("DB_URL",
            "jdbc:mysql://mysql-d7f118d-project-4905.b.aivencloud.com:21547/defaultdb?ssl-mode=REQUIRED&verifyServerCertificate=false");
    
    // Extracted the user and password
    private static final String DB_USER = getEnv("DB_USER", "avnadmin");
    private static final String DB_PASSWORD = getEnv("DB_PASSWORD", ""); 

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    private static String getEnv(String key, String fallback) {
        String value = System.getenv(key);
        return (value != null && !value.isBlank()) ? value : fallback;
    }

    /** Returns a fresh JDBC connection. Caller is responsible for closing it. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}