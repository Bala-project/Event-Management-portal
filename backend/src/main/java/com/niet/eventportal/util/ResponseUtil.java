package com.niet.eventportal.util;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtil {

    private static final Gson gson = new Gson();

    public static void sendJson(HttpServletResponse resp, int status, Object payload) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(payload));
    }

    public static Gson gson() {
        return gson;
    }
}
