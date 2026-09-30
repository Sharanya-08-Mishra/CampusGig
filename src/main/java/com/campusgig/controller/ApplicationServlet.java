package com.campusgig.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.campusgig.dao.ApplicationDAO;
import com.campusgig.model.Application;

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

    private final ApplicationDAO applicationDAO = new ApplicationDAO();

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

        // Stop if validation failed
        if (errors.length() > 0) {
            response.getWriter().println("<h1>Application Failed</h1>");
            response.getWriter().println(errors);
            response.getWriter().println("<a href='apply-gig.html'>Go Back</a>");
            return;
        }

        pitchText = pitchText.trim();

        // Get uploaded file name
        String fileName = portfolio.getSubmittedFileName();

        if (fileName == null || fileName.trim().isEmpty()) {
            response.getWriter().println("<h1>Application Failed</h1>");
            response.getWriter().println("<p>Portfolio file name is invalid.</p>");
            response.getWriter().println("<a href='apply-gig.html'>Go Back</a>");
            return;
        }

        // Keep only the actual file name
        fileName = new File(fileName).getName();

        // Validate portfolio file type
        String lowerFileName = fileName.toLowerCase();

        if (!lowerFileName.endsWith(".pdf")
                && !lowerFileName.endsWith(".jpg")
                && !lowerFileName.endsWith(".jpeg")
                && !lowerFileName.endsWith(".png")) {

            response.getWriter().println("<h1>Application Failed</h1>");
            response.getWriter().println(
                    "<p>Only PDF, JPG, JPEG and PNG files are allowed.</p>"
            );
            response.getWriter().println("<a href='apply-gig.html'>Go Back</a>");
            return;
        }

        // Read upload configuration from web.xml
        String uploadDir =
                getServletContext().getInitParameter("portfolioUploadDir");

        String maxFileSizeText =
                getServletContext().getInitParameter("maxFileSize");

        if (uploadDir == null || uploadDir.trim().isEmpty()) {
            response.getWriter().println("<h1>Upload Configuration Error</h1>");
            response.getWriter().println(
                    "<p>Upload directory is not configured.</p>"
            );
            return;
        }

        long maxFileSize;

        try {
            maxFileSize = Long.parseLong(maxFileSizeText);
        } catch (Exception e) {
            response.getWriter().println("<h1>Upload Configuration Error</h1>");
            response.getWriter().println(
                    "<p>Maximum file size configuration is invalid.</p>"
            );
            return;
        }

        // Check file size
        if (portfolio.getSize() > maxFileSize) {
            response.getWriter().println("<h1>Application Failed</h1>");
            response.getWriter().println(
                    "<p>Portfolio file is too large.</p>"
            );
            response.getWriter().println(
                    "<p>Maximum allowed size is 10 MB.</p>"
            );
            response.getWriter().println("<a href='apply-gig.html'>Go Back</a>");
            return;
        }

        // Get the deployed application's real path
        String realPath = getServletContext().getRealPath("/");

        if (realPath == null) {
            response.getWriter().println("<h1>File Upload Failed</h1>");
            response.getWriter().println(
                    "<p>Server upload path could not be determined.</p>"
            );
            return;
        }

        // Create upload directory
        File uploadDirectory = new File(realPath, uploadDir);

        if (!uploadDirectory.exists() && !uploadDirectory.mkdirs()) {
            response.getWriter().println("<h1>File Upload Failed</h1>");
            response.getWriter().println(
                    "<p>Could not create upload directory.</p>"
            );
            return;
        }

        // Create a unique file name
        String savedFileName =
                applicantId + "_" + System.currentTimeMillis() + "_" + fileName;

        File destinationFile =
                new File(uploadDirectory, savedFileName);

        // Save the uploaded file
        try (InputStream inputStream = portfolio.getInputStream()) {

            Files.copy(
                    inputStream,
                    destinationFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        // Create Application object
        Application application = new Application();

        application.setGigId(gigId);
        application.setApplicantId(applicantId);
        application.setPitchText(pitchText);

        // Store the relative portfolio path in database
        String portfolioPath = uploadDir + "/" + savedFileName;
        application.setPortfolioPath(portfolioPath);

        // Initial application status
        application.setStatus("Pending");

        // Save application to database
        boolean success = applicationDAO.addApplication(application);

        if (!success) {

            // Remove uploaded file if database insertion fails
            try {
                Files.deleteIfExists(destinationFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            response.getWriter().println("<h1>Application Failed</h1>");
            response.getWriter().println(
                    "<p>Application could not be saved to the database.</p>"
            );
            response.getWriter().println(
                    "<a href='apply-gig.html'>Go Back</a>"
            );
            return;
        }

        // Success response
        response.getWriter().println(
                "<h1>Application Submitted Successfully!</h1>"
        );
        response.getWriter().println(
                "<p>Gig ID: " + gigId + "</p>"
        );
        response.getWriter().println(
                "<p>Applicant ID: " + applicantId + "</p>"
        );
        response.getWriter().println(
                "<p>Pitch: " + pitchText + "</p>"
        );
        response.getWriter().println(
                "<p>Portfolio file: " + savedFileName + "</p>"
        );
        response.getWriter().println(
                "<p>Status: Pending</p>"
        );
    }
}