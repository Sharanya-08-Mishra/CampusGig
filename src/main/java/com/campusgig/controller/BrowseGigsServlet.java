package com.campusgig.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import com.campusgig.dao.GigDAO;
import com.campusgig.model.Gig;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/browse-gigs")
public class BrowseGigsServlet extends HttpServlet {

    private final GigDAO gigDAO = new GigDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String search = request.getParameter("search");
        String ajaxParam = request.getParameter("ajax");
        String secFetchDest = request.getHeader("Sec-Fetch-Dest");
        String xRequestedWith = request.getHeader("X-Requested-With");

        boolean isAjax = "true".equalsIgnoreCase(ajaxParam)
                || "XMLHttpRequest".equalsIgnoreCase(xRequestedWith)
                || "empty".equalsIgnoreCase(secFetchDest);

        List<Gig> gigs = gigDAO.getAllGigs();

        if (search != null && !search.trim().isEmpty()) {
            final String query = search.trim().toLowerCase();
            gigs = gigs.stream()
                    .filter(g -> (g.getTitle() != null && g.getTitle().toLowerCase().contains(query))
                            || (g.getDescription() != null && g.getDescription().toLowerCase().contains(query)))
                    .collect(Collectors.toList());
        }

        PrintWriter out = response.getWriter();

        if (isAjax) {
            out.print(renderGigCardsHtml(gigs, search));
        } else {
            out.print(renderFullPageHtml(gigs, search));
        }
    }

    private String renderGigCardsHtml(List<Gig> gigs, String search) {
        if (gigs == null || gigs.isEmpty()) {
            StringBuilder empty = new StringBuilder();
            empty.append("<div class=\"empty-state\" style=\"grid-column: 1 / -1; width: 100%; padding: 50px 20px;\">");
            empty.append("    <div style=\"font-size: 42px; margin-bottom: 12px;\">&#128269;</div>");
            if (search != null && !search.trim().isEmpty()) {
                empty.append("    <h3>No Gigs Found for \"").append(escapeHtml(search)).append("\"</h3>");
                empty.append("    <p>Try searching for different keywords or browse all opportunities.</p>");
                empty.append("    <a href=\"browse-gigs.html\" class=\"btn btn-secondary\" style=\"margin-top: 15px;\">Clear Search</a>");
            } else {
                empty.append("    <h3>No Campus Gigs Available Yet</h3>");
                empty.append("    <p>Be the first student to post an opportunity on campus!</p>");
                empty.append("    <a href=\"post-gig.html\" class=\"btn btn-primary\" style=\"margin-top: 15px;\">Post a Gig</a>");
            }
            empty.append("</div>");
            return empty.toString();
        }

        StringBuilder html = new StringBuilder();
        for (Gig gig : gigs) {
            html.append("<div class=\"gig-card\">");
            html.append("    <div style=\"display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;\">");
            html.append("        <span class=\"status-badge status-badge-open\">&#9679; ").append(escapeHtml(gig.getStatus() != null ? gig.getStatus() : "Open")).append("</span>");
            html.append("        <span style=\"font-size: 13px; color: var(--muted); font-weight: 600;\">Gig #").append(gig.getGigId()).append("</span>");
            html.append("    </div>");
            html.append("    <h3 style=\"margin-bottom: 10px; color: var(--dark); font-size: 20px;\">").append(escapeHtml(gig.getTitle())).append("</h3>");
            html.append("    <p style=\"color: var(--muted); font-size: 14px; margin-bottom: 16px; line-height: 1.5;\">").append(escapeHtml(gig.getDescription())).append("</p>");
            html.append("    <div class=\"gig-info\" style=\"border-top: 1px solid var(--border); padding-top: 12px; margin: 14px 0;\">");
            html.append("        <span class=\"gig-budget\" style=\"font-size: 16px; font-weight: 800; color: var(--success);\">&#8377;").append(String.format("%.2f", gig.getBudget())).append("</span>");
            if (gig.getDeadline() != null) {
                html.append("        <span class=\"gig-deadline\" style=\"font-size: 13px; color: var(--muted);\">&#128197; ").append(gig.getDeadline()).append("</span>");
            }
            html.append("    </div>");
            html.append("    <a href=\"apply-gig.html?gigId=").append(gig.getGigId()).append("\" class=\"btn btn-primary\" style=\"display: block; text-align: center; margin-top: 14px;\">Apply for this Gig</a>");
            html.append("</div>");
        }
        return html.toString();
    }

    private String renderFullPageHtml(List<Gig> gigs, String search) {
        StringBuilder page = new StringBuilder();
        page.append("<!DOCTYPE html>\n");
        page.append("<html lang=\"en\">\n<head>\n");
        page.append("    <meta charset=\"UTF-8\">\n");
        page.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        page.append("    <title>Browse Gigs - CampusGig</title>\n");
        page.append("    <link rel=\"stylesheet\" href=\"css/style.css\">\n");
        page.append("</head>\n<body>\n");
        page.append("    <nav class=\"navbar\">\n");
        page.append("        <a href=\"index.html\" class=\"logo\">Campus<span>Gig</span></a>\n");
        page.append("        <ul class=\"nav-links\">\n");
        page.append("            <li><a href=\"index.html\">Home</a></li>\n");
        page.append("            <li><a href=\"browse-gigs.html\">Browse Gigs</a></li>\n");
        page.append("            <li><a href=\"post-gig.html\" class=\"nav-button\">Post a Gig</a></li>\n");
        page.append("        </ul>\n");
        page.append("    </nav>\n");
        page.append("    <main class=\"container\">\n");
        page.append("        <div class=\"page-title\">\n");
        page.append("            <h1>Browse Campus Gigs</h1>\n");
        page.append("            <p>Find opportunities that match your skills and interests.</p>\n");
        page.append("        </div>\n");
        page.append("        <div class=\"search-box\">\n");
        page.append("            <form action=\"browse-gigs\" method=\"get\" class=\"search-form\">\n");
        page.append("                <input type=\"text\" id=\"search\" name=\"search\" value=\"").append(search != null ? escapeHtml(search) : "").append("\" placeholder=\"Search for gigs...\">\n");
        page.append("                <button type=\"submit\" class=\"btn btn-primary\">Search</button>\n");
        page.append("            </form>\n");
        page.append("        </div>\n");
        page.append("        <section>\n");
        page.append("            <div class=\"gig-grid\">\n");
        page.append(renderGigCardsHtml(gigs, search));
        page.append("            </div>\n");
        page.append("        </section>\n");
        page.append("    </main>\n");
        page.append("    <footer class=\"footer\">\n");
        page.append("        <p>&copy; 2026 CampusGig. Built by students, for students.</p>\n");
        page.append("    </footer>\n");
        page.append("</body>\n</html>");
        return page.toString();
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