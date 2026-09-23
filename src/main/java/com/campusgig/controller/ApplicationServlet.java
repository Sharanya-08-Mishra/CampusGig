package com.campusgig.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/apply-gig")
@MultipartConfig
public class ApplicationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        String gigIdText = request.getParameter("gigId");
        String applicantIdText = request.getParameter("applicantId");
        String pitchText = request.getParameter("pitchText");

        Part portfolio = request.getPart("portfolio");

        StringBuilder errors = new StringBuilder();

        int gigId = 0;
        int applicantId = 0;

        // Validate Gig ID
        try {
            gigId = Integer.parseInt(gigIdText);

            if (gigId <= 0) {
                errors.append("<p>Gig ID must be greater than 0.</p>");
            }
        } catch (Exception e) {
            errors.append("<p>Gig ID must be a valid number.</p>");
        }

        // Validate Applicant ID
        try {
            applicantId = Integer.parseInt(applicantIdText);

            if (applicantId <= 0) {
                errors.append("<p>Applicant ID must be greater than 0.</p>");
            }
        } catch (Exception e) {
            errors.append("<p>Applicant ID must be a valid number.</p>");
        }

        // Validate pitch
        if (pitchText == null || pitchText.trim().isEmpty()) {
            errors.append("<p>Pitch is required.</p>");
        }

        // Validate portfolio
        if (portfolio == null || portfolio.getSize() == 0) {
            errors.append("<p>Portfolio or resume is required.</p>");
        }

        if (errors.length() > 0) {
            response.getWriter().println("<h1>Application Failed</h1>");
            response.getWriter().println(errors);
            response.getWriter().println("<a href='apply-gig.html'>Go Back</a>");
            return;
        }

        String fileName = portfolio.getSubmittedFileName();

        // Temporary success response.
        // Database insertion and file storage will be connected later.
        response.getWriter().println("<h1>Application Validated Successfully!</h1>");
        response.getWriter().println("<p>Gig ID: " + gigId + "</p>");
        response.getWriter().println("<p>Applicant ID: " + applicantId + "</p>");
        response.getWriter().println("<p>Pitch: " + pitchText + "</p>");
        response.getWriter().println("<p>Portfolio file: " + fileName + "</p>");
    }
}
