package com.campusgig.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.campusgig.dao.ApplicationDAO;
import com.campusgig.model.Application;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/applications")
public class ApplicationServlet extends HttpServlet {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    // GET /CampusGig/applications?gigId=1
    // GET /CampusGig/applications?applicantId=1
    // GET /CampusGig/applications?id=1
    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {

            String idParam = request.getParameter("id");
            String gigIdParam = request.getParameter("gigId");
            String applicantIdParam = request.getParameter("applicantId");

            // Get one application
            if (idParam != null && !idParam.isBlank()) {

                int applicationId = Integer.parseInt(idParam);

                Application application =
                        applicationDAO.getApplicationById(applicationId);

                if (application == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(
                        "{\"success\":false,\"message\":\"Application not found\"}"
                    );
                    return;
                }

                out.print(applicationToJson(application));
                return;
            }

            // Get applications for a gig
            if (gigIdParam != null && !gigIdParam.isBlank()) {

                int gigId = Integer.parseInt(gigIdParam);

                List<Application> applications =
                        applicationDAO.getApplicationsByGigId(gigId);

                writeApplicationList(out, applications);
                return;
            }

            // Get applications submitted by a student
            if (applicantIdParam != null && !applicantIdParam.isBlank()) {

                int applicantId = Integer.parseInt(applicantIdParam);

                List<Application> applications =
                        applicationDAO.getApplicationsByApplicantId(applicantId);

                writeApplicationList(out, applications);
                return;
            }

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(
                "{\"success\":false,\"message\":\"Provide id, gigId or applicantId\"}"
            );

        } catch (NumberFormatException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(
                "{\"success\":false,\"message\":\"Invalid ID\"}"
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(
                "{\"success\":false,\"message\":\"Server error\"}"
            );
        }
    }

    // POST /CampusGig/applications
    // Creates a new application
    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {

            String gigIdParam = request.getParameter("gigId");
            String applicantIdParam = request.getParameter("applicantId");
            String pitchText = request.getParameter("pitchText");
            String portfolioPath = request.getParameter("portfolioPath");

            // Required fields
            if (gigIdParam == null || gigIdParam.isBlank()
                    || applicantIdParam == null || applicantIdParam.isBlank()
                    || pitchText == null || pitchText.isBlank()) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(
                    "{\"success\":false,\"message\":\"Gig ID, applicant ID and pitch are required\"}"
                );
                return;
            }

            int gigId = Integer.parseInt(gigIdParam);
            int applicantId = Integer.parseInt(applicantIdParam);

            if (gigId <= 0 || applicantId <= 0) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(
                    "{\"success\":false,\"message\":\"Invalid gig or applicant ID\"}"
                );
                return;
            }

            Application application = new Application();

            application.setGigId(gigId);
            application.setApplicantId(applicantId);
            application.setPitchText(pitchText.trim());
            application.setPortfolioPath(
                    portfolioPath == null ? null : portfolioPath.trim()
            );
            application.setStatus("Pending");

            boolean success = applicationDAO.addApplication(application);

            if (success) {

                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print(
                    "{\"success\":true,\"message\":\"Application submitted successfully\"}"
                );

            } else {

                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(
                    "{\"success\":false,\"message\":\"Failed to submit application\"}"
                );
            }

        } catch (NumberFormatException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(
                "{\"success\":false,\"message\":\"Invalid ID format\"}"
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(
                "{\"success\":false,\"message\":\"Server error\"}"
            );
        }
    }

    // PUT /CampusGig/applications
    // Updates application status
    @Override
    protected void doPut(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {

            String applicationIdParam =
                    request.getParameter("applicationId");

            String status =
                    request.getParameter("status");

            if (applicationIdParam == null
                    || applicationIdParam.isBlank()
                    || status == null
                    || status.isBlank()) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(
                    "{\"success\":false,\"message\":\"Application ID and status are required\"}"
                );
                return;
            }

            int applicationId =
                    Integer.parseInt(applicationIdParam);

            if (applicationId <= 0) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(
                    "{\"success\":false,\"message\":\"Invalid application ID\"}"
                );
                return;
            }

            // Allowed application statuses
            if (!status.equalsIgnoreCase("Pending")
                    && !status.equalsIgnoreCase("Accepted")
                    && !status.equalsIgnoreCase("Rejected")
                    && !status.equalsIgnoreCase("Hired")) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(
                    "{\"success\":false,\"message\":\"Invalid application status\"}"
                );
                return;
            }

            boolean success =
                    applicationDAO.updateApplicationStatus(
                            applicationId,
                            status
                    );

            if (success) {

                out.print(
                    "{\"success\":true,\"message\":\"Application status updated\"}"
                );

            } else {

                response.setStatus(
                        HttpServletResponse.SC_NOT_FOUND
                );

                out.print(
                    "{\"success\":false,\"message\":\"Application not found\"}"
                );
            }

        } catch (NumberFormatException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(
                "{\"success\":false,\"message\":\"Invalid application ID\"}"
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            out.print(
                "{\"success\":false,\"message\":\"Server error\"}"
            );
        }
    }

    private void writeApplicationList(
            PrintWriter out,
            List<Application> applications) {

        out.print("[");

        for (int i = 0; i < applications.size(); i++) {

            out.print(applicationToJson(applications.get(i)));

            if (i < applications.size() - 1) {
                out.print(",");
            }
        }

        out.print("]");
    }

    private String applicationToJson(Application application) {

        return "{"
                + "\"applicationId\":" + application.getApplicationId() + ","
                + "\"gigId\":" + application.getGigId() + ","
                + "\"applicantId\":" + application.getApplicantId() + ","
                + "\"pitchText\":\""
                + escapeJson(application.getPitchText()) + "\","
                + "\"portfolioPath\":\""
                + escapeJson(application.getPortfolioPath()) + "\","
                + "\"status\":\""
                + escapeJson(application.getStatus()) + "\""
                + "}";
    }

    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}