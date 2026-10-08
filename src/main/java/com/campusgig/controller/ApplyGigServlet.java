package com.campusgig.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.campusgig.dao.ApplicationDAO;
import com.campusgig.dao.GigDAO;
import com.campusgig.dao.StudentDAO;
import com.campusgig.model.Application;
import com.campusgig.model.Gig;
import com.campusgig.model.Student;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/apply-gig")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 10 * 1024 * 1024,
        maxRequestSize = 12 * 1024 * 1024
)
public class ApplyGigServlet extends HttpServlet {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final GigDAO gigDAO = new GigDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String gigIdText = request.getParameter("gigId");
        String applicantIdText = request.getParameter("applicantId");
        String pitchText = request.getParameter("pitchText");

        StringBuilder errors = new StringBuilder();

        int gigId = 0;
        int applicantId = 0;

        // Validate gigId
        try {
            gigId = Integer.parseInt(gigIdText);
            if (gigId <= 0) {
                errors.append("<p>&#8226; Gig ID must be a positive number.</p>");
            } else {
                Gig gig = gigDAO.getGigById(gigId);
                if (gig == null) {
                    errors.append("<p>&#8226; Gig with ID ").append(gigId).append(" does not exist.</p>");
                }
            }
        } catch (Exception e) {
            errors.append("<p>&#8226; Gig ID must be a valid number.</p>");
        }

        // Validate applicantId
        try {
            applicantId = Integer.parseInt(applicantIdText);
            if (applicantId <= 0) {
                errors.append("<p>&#8226; Applicant ID must be a positive number.</p>");
            } else {
                Student student = studentDAO.getStudentById(applicantId);
                if (student == null) {
                    errors.append("<p>&#8226; Applicant ID ").append(applicantId).append(" is not registered as a student.</p>");
                }
            }
        } catch (Exception e) {
            errors.append("<p>&#8226; Applicant ID must be a valid number.</p>");
        }

        // Validate pitchText
        if (pitchText == null || pitchText.trim().isEmpty()) {
            errors.append("<p>&#8226; Pitch text is required to explain your suitability.</p>");
        }

        if (errors.length() > 0) {
            writeErrorPage(out, errors.toString());
            return;
        }

        // Handle portfolio file upload if provided
        String portfolioFileName = null;
        try {
            Part filePart = request.getPart("portfolio");
            if (filePart != null && filePart.getSize() > 0) {
                String submittedName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String ext = "";
                int dot = submittedName.lastIndexOf('.');
                if (dot > 0) {
                    ext = submittedName.substring(dot);
                }
                portfolioFileName = UUID.randomUUID() + ext;

                Path uploadPath = Paths.get(DownloadResumeServlet.UPLOAD_DIR).toAbsolutePath();
                Files.createDirectories(uploadPath);

                Path targetFile = uploadPath.resolve(portfolioFileName);
                try (InputStream in = filePart.getInputStream();
                     OutputStream fileOut = Files.newOutputStream(targetFile)) {
                    in.transferTo(fileOut);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Application app = new Application();
        app.setGigId(gigId);
        app.setApplicantId(applicantId);
        app.setPitchText(pitchText.trim());
        app.setPortfolioPath(portfolioFileName);
        app.setStatus("Pending");

        boolean success = applicationDAO.addApplication(app);

        if (success) {
            writeSuccessPage(out, app);
        } else {
            writeErrorPage(out, "<p>&#8226; Database error: Failed to save application. Please try again.</p>");
        }
    }

    private void writeSuccessPage(PrintWriter out, Application app) {
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("    <title>Application Submitted - CampusGig</title>");
        out.println("    <link rel=\"stylesheet\" href=\"css/style.css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("    <!-- Navigation -->");
        out.println("    <nav class=\"navbar\">");
        out.println("        <a href=\"index.html\" class=\"logo\">Campus<span>Gig</span></a>");
        out.println("        <ul class=\"nav-links\">");
        out.println("            <li><a href=\"index.html\">Home</a></li>");
        out.println("            <li><a href=\"browse-gigs.html\">Browse Gigs</a></li>");
        out.println("            <li><a href=\"post-gig.html\" class=\"nav-button\">Post a Gig</a></li>");
        out.println("        </ul>");
        out.println("    </nav>");
        out.println("    <!-- Main Content -->");
        out.println("    <main class=\"container\">");
        out.println("        <div class=\"status-card\">");
        out.println("            <div class=\"status-icon status-icon-success\">");
        out.println("                <svg fill=\"none\" stroke=\"currentColor\" stroke-width=\"2.5\" viewBox=\"0 0 24 24\">");
        out.println("                    <path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M5 13l4 4L19 7\"></path>");
        out.println("                </svg>");
        out.println("            </div>");
        out.println("            <h1 class=\"status-title\">Application Submitted!</h1>");
        out.println("            <p class=\"status-subtitle\">Your application has been received and forwarded to the gig poster.</p>");
        out.println("            <div class=\"status-details\">");
        out.println("                <div class=\"status-details-row\">");
        out.println("                    <span class=\"status-details-label\">Target Gig</span>");
        out.println("                    <span class=\"status-details-value\">Gig #" + app.getGigId() + "</span>");
        out.println("                </div>");
        out.println("                <div class=\"status-details-row\">");
        out.println("                    <span class=\"status-details-label\">Applicant Student ID</span>");
        out.println("                    <span class=\"status-details-value\">Student #" + app.getApplicantId() + "</span>");
        out.println("                </div>");
        out.println("                <div class=\"status-details-row\">");
        out.println("                    <span class=\"status-details-label\">Resume / Portfolio</span>");
        out.println("                    <span class=\"status-details-value\">" + (app.getPortfolioPath() != null ? "&#128206; Attached" : "None") + "</span>");
        out.println("                </div>");
        out.println("                <div class=\"status-details-row\">");
        out.println("                    <span class=\"status-details-label\">Application Status</span>");
        out.println("                    <span class=\"status-details-value\"><span class=\"status-badge\" style=\"background:#fef3c7; color:#92400e;\">&#9679; Pending</span></span>");
        out.println("                </div>");
        out.println("            </div>");
        out.println("            <div class=\"status-actions\">");
        out.println("                <a href=\"browse-gigs.html\" class=\"btn btn-primary\">Browse Other Gigs</a>");
        out.println("                <a href=\"apply-gig.html\" class=\"btn btn-secondary\">Apply for Another</a>");
        out.println("            </div>");
        out.println("            <div style=\"margin-top: 22px;\">");
        out.println("                <a href=\"index.html\" style=\"color: var(--muted); text-decoration: none; font-size: 14px; font-weight: 600;\">&larr; Back to Home</a>");
        out.println("            </div>");
        out.println("        </div>");
        out.println("    </main>");
        out.println("    <!-- Footer -->");
        out.println("    <footer class=\"footer\">");
        out.println("        <p>&copy; 2026 CampusGig. Built by students, for students.</p>");
        out.println("    </footer>");
        out.println("</body>");
        out.println("</html>");
    }

    private void writeErrorPage(PrintWriter out, String errorHtml) {
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("    <title>Application Failed - CampusGig</title>");
        out.println("    <link rel=\"stylesheet\" href=\"css/style.css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("    <!-- Navigation -->");
        out.println("    <nav class=\"navbar\">");
        out.println("        <a href=\"index.html\" class=\"logo\">Campus<span>Gig</span></a>");
        out.println("        <ul class=\"nav-links\">");
        out.println("            <li><a href=\"index.html\">Home</a></li>");
        out.println("            <li><a href=\"browse-gigs.html\">Browse Gigs</a></li>");
        out.println("            <li><a href=\"post-gig.html\" class=\"nav-button\">Post a Gig</a></li>");
        out.println("        </ul>");
        out.println("    </nav>");
        out.println("    <!-- Main Content -->");
        out.println("    <main class=\"container\">");
        out.println("        <div class=\"status-card\">");
        out.println("            <div class=\"status-icon status-icon-danger\">");
        out.println("                <svg fill=\"none\" stroke=\"currentColor\" stroke-width=\"2.5\" viewBox=\"0 0 24 24\">");
        out.println("                    <path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z\"></path>");
        out.println("                </svg>");
        out.println("            </div>");
        out.println("            <h1 class=\"status-title\">Application Failed</h1>");
        out.println("            <p class=\"status-subtitle\">Could not submit your application. Please check the issues below.</p>");
        out.println("            <div class=\"status-error-box\">");
        out.println(errorHtml);
        out.println("            </div>");
        out.println("            <div class=\"status-actions\">");
        out.println("                <a href=\"javascript:history.back()\" class=\"btn btn-primary\">Go Back to Form</a>");
        out.println("                <a href=\"browse-gigs.html\" class=\"btn btn-secondary\">Back to Gigs</a>");
        out.println("            </div>");
        out.println("        </div>");
        out.println("    </main>");
        out.println("    <!-- Footer -->");
        out.println("    <footer class=\"footer\">");
        out.println("        <p>&copy; 2026 CampusGig. Built by students, for students.</p>");
        out.println("    </footer>");
        out.println("</body>");
        out.println("</html>");
    }
}
