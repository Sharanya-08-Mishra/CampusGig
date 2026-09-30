package com.campusgig.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.campusgig.util.DBConnection;

@WebServlet("/browse-gigs")
public class BrowseGigsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String search = request.getParameter("search");

        String sql = "SELECT * FROM gigs";

        if (search != null && !search.trim().isEmpty()) {
            sql += " WHERE title LIKE ? OR description LIKE ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (search != null && !search.trim().isEmpty()) {
                String keyword = "%" + search.trim() + "%";
                stmt.setString(1, keyword);
                stmt.setString(2, keyword);
            }

            ResultSet rs = stmt.executeQuery();

            response.getWriter().println("<div class='gig-results'>");

            boolean found = false;

            while (rs.next()) {
                found = true;

                response.getWriter().println("<div class='gig-card'>");
                response.getWriter().println("<h3>" + rs.getString("title") + "</h3>");
                response.getWriter().println("<p>" + rs.getString("description") + "</p>");
                response.getWriter().println("<p><strong>Budget:</strong> ₹"
                        + rs.getDouble("budget") + "</p>");
                response.getWriter().println("<p><strong>Deadline:</strong> "
                        + rs.getDate("deadline") + "</p>");
                response.getWriter().println("</div>");
            }

            if (!found) {
                response.getWriter().println("<p>No gigs found.</p>");
            }

            response.getWriter().println("</div>");

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                    "<p>Unable to load gigs from the database.</p>"
            );
        }
    }
}