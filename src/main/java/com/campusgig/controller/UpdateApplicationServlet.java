package com.campusgig.controller;

import java.io.IOException;

import com.campusgig.dao.ApplicationDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/update-application")
public class UpdateApplicationServlet extends HttpServlet {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String applicationIdText = request.getParameter("applicationId");
        String status = request.getParameter("status");

        int applicationId;

        // Validate Application ID
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

        // Validate status
        if (status == null ||
                (!status.equals("HIRED") && !status.equals("REJECTED"))) {

            response.getWriter().println("<h1>Invalid Status</h1>");
            response.getWriter().println(
                    "<p>Status must be Hire or Reject.</p>"
            );
            return;
        }

        // Update status in database
        boolean success =
                applicationDAO.updateApplicationStatus(applicationId, status);

        if (success) {

            response.getWriter().println(
                    "<h1>Application Status Updated</h1>"
            );
            response.getWriter().println(
                    "<p>Application ID: " + applicationId + "</p>"
            );
            response.getWriter().println(
                    "<p>Status: " + status + "</p>"
            );

        } else {

            response.getWriter().println(
                    "<h1>Update Failed</h1>"
            );
            response.getWriter().println(
                    "<p>Application status could not be updated.</p>"
            );
        }
    }
}
