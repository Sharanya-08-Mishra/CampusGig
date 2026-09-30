package com.campusgig.controller;

import java.io.IOException;

import com.campusgig.dao.ApplicationDAO;
import com.campusgig.model.Application;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/application-status")
public class ApplicationStatusServlet extends HttpServlet {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String applicationIdText = request.getParameter("applicationId");

        if (applicationIdText == null || applicationIdText.trim().isEmpty()) {
            response.getWriter().println("<h1>Application Status</h1>");
            response.getWriter().println("<p>Application ID is required.</p>");
            return;
        }

        int applicationId;

        try {
            applicationId = Integer.parseInt(applicationIdText);

            if (applicationId <= 0) {
                response.getWriter().println("<h1>Invalid Application ID</h1>");
                response.getWriter().println(
                        "<p>Application ID must be greater than 0.</p>"
                );
                return;
            }

        } catch (NumberFormatException e) {
            response.getWriter().println("<h1>Invalid Application ID</h1>");
            response.getWriter().println(
                    "<p>Application ID must be a valid number.</p>"
            );
            return;
        }

        try {

            Application application =
                    applicationDAO.getApplicationById(applicationId);

            if (application == null) {
                response.getWriter().println("<h1>Application Not Found</h1>");
                response.getWriter().println(
                        "<p>No application found with ID "
                                + applicationId + ".</p>"
                );
                return;
            }

            response.getWriter().println("<h1>Application Status</h1>");
            response.getWriter().println(
                    "<p>Application ID: "
                            + application.getApplicationId() + "</p>"
            );
            response.getWriter().println(
                    "<p>Gig ID: " + application.getGigId() + "</p>"
            );
            response.getWriter().println(
                    "<p>Applicant ID: "
                            + application.getApplicantId() + "</p>"
            );
            response.getWriter().println(
                    "<p>Status: " + application.getStatus() + "</p>"
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println("<h1>Server Error</h1>");
            response.getWriter().println(
                    "<p>Could not load application status.</p>"
            );
        }
    }
}