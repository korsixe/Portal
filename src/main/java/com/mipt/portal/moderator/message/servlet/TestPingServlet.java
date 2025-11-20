// TestPingServlet.java
package com.mipt.portal.moderator.message.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/test-ping")
public class TestPingServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("✅ Ping received!");
        response.setContentType("text/plain");
        response.getWriter().write("PONG - " + System.currentTimeMillis());
    }
}