package com.campusgig.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.campusgig.util.DBConnection;

@WebServlet("/post-gig")
public class GigPostingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String posterIdText = request.getParameter("posterId");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String budgetText = request.getParameter("budget");
        String deadlineText = request.getParameter("deadline");

        try {
            int posterId = Integer.parseInt(posterIdText);
            double budget = Double.parseDouble(budgetText);
            LocalDate deadline = LocalDate.parse(deadlineText);

            if (posterId <= 0 || title == null || title.trim().isEmpty()
                    || description == null || description.trim().isEmpty()
                    || budget <= 0 || deadline.isBefore(LocalDate.now())) {

                response.getWriter().println("<h1>Invalid Gig Details</h1>");
                response.getWriter().println("<a href='post-gig.html'>Go Back</a>");
                return;
            }

            String sql = "INSERT INTO gigs (poster_id, title, description, budget, deadline) "
                       + "VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, posterId);
                stmt.setString(2, title);
                stmt.setString(3, description);
                stmt.setDouble(4, budget);
                stmt.setDate(5, java.sql.Date.valueOf(deadline));

                stmt.executeUpdate();
            }

            response.getWriter().println("<h1>Gig Posted Successfully!</h1>");
            response.getWriter().println("<p>Your gig has been saved.</p>");
            response.getWriter().println("<a href='browse-gigs.html'>Browse Gigs</a>");

        } catch (NumberFormatException | DateTimeParseException e) {

            response.getWriter().println("<h1>Invalid Gig Details</h1>");
            response.getWriter().println("<p>Please check your input.</p>");
            response.getWriter().println("<a href='post-gig.html'>Go Back</a>");

        } catch (Exception e) {

            e.printStackTrace();
            response.getWriter().println("<h1>Database Error</h1>");
            response.getWriter().println("<p>Could not save the gig.</p>");
            response.getWriter().println("<a href='post-gig.html'>Go Back</a>");
        }
    }
}