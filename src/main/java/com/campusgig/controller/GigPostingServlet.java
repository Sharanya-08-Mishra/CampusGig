package com.campusgig.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.campusgig.dao.GigDAO;
import com.campusgig.model.Gig;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/post-gig")
public class GigPostingServlet extends HttpServlet {

    private final GigDAO gigDAO = new GigDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

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

        // Stop if validation fails
        if (errors.length() > 0) {
            writeErrorPage(response.getWriter(), errors.toString());
            return;
        }

        // Create Gig object
        Gig gig = new Gig();

        gig.setPosterId(posterId);
        gig.setTitle(title.trim());
        gig.setDescription(description.trim());
        gig.setBudget(budget);
        gig.setDeadline(deadline);
        gig.setStatus("Open");

        // Insert gig into database
        boolean success = gigDAO.addGig(gig);

        if (success) {
            writeSuccessPage(response.getWriter(), gig);
        } else {
            writeErrorPage(response.getWriter(), "<p>&#8226; Could not save the gig to the database. Please verify that your Student ID is registered.</p>");
        }
    }

    private void writeSuccessPage(java.io.PrintWriter out, Gig gig) {
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("    <title>Gig Posted Successfully - CampusGig</title>");
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
        out.println("            <h1 class=\"status-title\">Gig Posted Successfully!</h1>");
        out.println("            <p class=\"status-subtitle\">Your gig has been added to the database and is now live for applicants.</p>");
        out.println("            <div class=\"status-details\">");
        out.println("                <div class=\"status-details-row\">");
        out.println("                    <span class=\"status-details-label\">Gig Title</span>");
        out.println("                    <span class=\"status-details-value\">" + escapeHtml(gig.getTitle()) + "</span>");
        out.println("                </div>");
        out.println("                <div class=\"status-details-row\">");
        out.println("                    <span class=\"status-details-label\">Budget Offered</span>");
        out.println("                    <span class=\"status-details-value\" style=\"color: var(--success); font-weight: 800; font-size: 16px;\">&#8377;" + String.format("%.2f", gig.getBudget()) + "</span>");
        out.println("                </div>");
        out.println("                <div class=\"status-details-row\">");
        out.println("                    <span class=\"status-details-label\">Deadline</span>");
        out.println("                    <span class=\"status-details-value\">&#128197; " + gig.getDeadline() + "</span>");
        out.println("                </div>");
        out.println("                <div class=\"status-details-row\">");
        out.println("                    <span class=\"status-details-label\">Status</span>");
        out.println("                    <span class=\"status-details-value\"><span class=\"status-badge status-badge-open\">&#9679; Open</span></span>");
        out.println("                </div>");
        out.println("            </div>");
        out.println("            <div class=\"status-actions\">");
        out.println("                <a href=\"browse-gigs.html\" class=\"btn btn-primary\">Browse All Gigs</a>");
        out.println("                <a href=\"post-gig.html\" class=\"btn btn-secondary\">Post Another Gig</a>");
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

    private void writeErrorPage(java.io.PrintWriter out, String errorHtml) {
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("    <title>Gig Posting Failed - CampusGig</title>");
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
        out.println("            <h1 class=\"status-title\">Gig Posting Failed</h1>");
        out.println("            <p class=\"status-subtitle\">We couldn't save your gig. Please review the errors below and try again.</p>");
        out.println("            <div class=\"status-error-box\">");
        out.println(errorHtml);
        out.println("            </div>");
        out.println("            <div class=\"status-actions\">");
        out.println("                <a href=\"post-gig.html\" class=\"btn btn-primary\">Go Back to Form</a>");
        out.println("                <a href=\"index.html\" class=\"btn btn-secondary\">Cancel &amp; Home</a>");
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

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
