package com.niet.eventportal.servlet;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Very lightweight in-memory token store used to protect admin-only endpoints
 * (add event, delete event, view registrations). A real production system
 * should use JWTs or a proper session/auth framework - this keeps the
 * college-project scope simple while still requiring a valid login.
 */
public class AuthUtil {

    private static final Set<String> ACTIVE_TOKENS = ConcurrentHashMap.newKeySet();

    public static String issueToken() {
        String token = UUID.randomUUID().toString();
        ACTIVE_TOKENS.add(token);
        return token;
    }

    public static boolean isAdminAuthenticated(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return false;
        String token = header.substring("Bearer ".length()).trim();
        return ACTIVE_TOKENS.contains(token);
    }

    public static void revokeToken(String token) {
        ACTIVE_TOKENS.remove(token);
    }
}
