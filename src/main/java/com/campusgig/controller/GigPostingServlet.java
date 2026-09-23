package com.campusgig.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/post-gig")
public class GigPostingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        String posterIdText = request.getParameter("posterId");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String budgetText = request.getParameter("budget");
        String deadlineText = request.getParameter("deadline");

        StringBuilder errors = new StringBuilder();

        int posterId = 0;
        double budget = 0;
        LocalDate deadline = null;

        // Validate Poster ID
        try {
            posterId = Integer.parseInt(posterIdText);

            if (posterId <= 0) {
                errors.append("<p>Poster ID must be greater than 0.</p>");
            }

        } catch (Exception e) {
            errors.append("<p>Poster ID must be a valid number.</p>");
        }

        // Validate title
        if (title == null || title.trim().isEmpty()) {
            errors.append("<p>Gig title is required.</p>");
        }

        // Validate description
        if (description == null || description.trim().isEmpty()) {
            errors.append("<p>Gig description is required.</p>");
        }

        // Validate budget
        try {
            budget = Double.parseDouble(budgetText);

            if (budget <= 0) {
                errors.append("<p>Budget must be greater than 0.</p>");
            }

        } catch (Exception e) {
            errors.append("<p>Budget must be a valid number.</p>");
        }

        // Validate deadline
        try {
            deadline = LocalDate.parse(deadlineText);

            if (deadline.isBefore(LocalDate.now())) {
                errors.append("<p>Deadline cannot be in the past.</p>");
            }

        } catch (DateTimeParseException e) {
            errors.append("<p>Deadline must be a valid date.</p>");
        }

        // Display validation errors
        if (errors.length() > 0) {

            response.getWriter().println("<h1>Gig Posting Failed</h1>");
            response.getWriter().println(errors.toString());
            response.getWriter().println("<a href='post-gig.html'>Go Back</a>");

            return;
        }

        // Temporary success response.
        // Database insertion will be connected later through GigDAO.
        response.getWriter().println("<h1>Gig Details Validated Successfully!</h1>");
        response.getWriter().println("<p>Poster ID: " + posterId + "</p>");
        response.getWriter().println("<p>Title: " + title + "</p>");
        response.getWriter().println("<p>Description: " + description + "</p>");
        response.getWriter().println("<p>Budget: " + budget + "</p>");
        response.getWriter().println("<p>Deadline: " + deadline + "</p>");
    }
}