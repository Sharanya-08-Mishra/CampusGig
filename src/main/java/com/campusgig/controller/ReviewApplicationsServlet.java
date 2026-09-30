package com.campusgig.controller;

import java.io.IOException;
import java.util.List;

import com.campusgig.dao.ApplicationDAO;
import com.campusgig.model.Application;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/review-applications")
public class ReviewApplicationsServlet extends HttpServlet {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String gigIdText = request.getParameter("gigId");

        if (gigIdText == null || gigIdText.isBlank()) {
            response.getWriter().println("<h1>Invalid Gig ID</h1>");
            response.getWriter().println("<p>Gig ID is required.</p>");
            return;
        }

        int gigId;

        try {
            gigId = Integer.parseInt(gigIdText);

            if (gigId <= 0) {
                response.getWriter().println("<h1>Invalid Gig ID</h1>");
                response.getWriter().println("<p>Gig ID must be greater than 0.</p>");
                return;
            }

        } catch (NumberFormatException e) {
            response.getWriter().println("<h1>Invalid Gig ID</h1>");
            response.getWriter().println("<p>Gig ID must be a valid number.</p>");
            return;
        }

        try {

            List<Application> applications =
                    applicationDAO.getApplicationsByGigId(gigId);

            response.getWriter().println("<h1>Applications for Gig " + gigId + "</h1>");

            if (applications.isEmpty()) {
                response.getWriter().println("<p>No applications found.</p>");
                return;
            }

            response.getWriter().println("<table border='1'>");
            response.getWriter().println("<tr>");
            response.getWriter().println("<th>Application ID</th>");
            response.getWriter().println("<th>Applicant ID</th>");
            response.getWriter().println("<th>Pitch</th>");
            response.getWriter().println("<th>Portfolio</th>");
            response.getWriter().println("<th>Status</th>");
            response.getWriter().println("</tr>");

            for (Application application : applications) {

                response.getWriter().println("<tr>");

                response.getWriter().println(
                        "<td>" + application.getApplicationId() + "</td>"
                );

                response.getWriter().println(
                        "<td>" + application.getApplicantId() + "</td>"
                );

                response.getWriter().println(
                        "<td>" + application.getPitchText() + "</td>"
                );

                response.getWriter().println(
                        "<td>" + application.getPortfolioPath() + "</td>"
                );

                response.getWriter().println(
                        "<td>" + application.getStatus() + "</td>"
                );

                response.getWriter().println("</tr>");
            }

            response.getWriter().println("</table>");

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println("<h1>Server Error</h1>");
            response.getWriter().println(
                    "<p>Could not load applications.</p>"
            );
        }
    }
}