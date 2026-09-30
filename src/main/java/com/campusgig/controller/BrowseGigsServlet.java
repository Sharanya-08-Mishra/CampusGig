package com.campusgig.controller;

import java.io.IOException;
import java.util.List;

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

        try {

            List<Gig> gigs = gigDAO.getAllGigs();

            response.getWriter().println("<h1>CampusGig - Browse Gigs</h1>");

            if (search != null && !search.trim().isEmpty()) {
                search = search.trim().toLowerCase();

                response.getWriter().println(
                        "<p>Search results for: " + search + "</p>"
                );
            } else {
                response.getWriter().println(
                        "<p>Showing all available gigs.</p>"
                );
            }

            boolean found = false;

            response.getWriter().println("<table border='1'>");
            response.getWriter().println("<tr>");
            response.getWriter().println("<th>Gig ID</th>");
            response.getWriter().println("<th>Poster ID</th>");
            response.getWriter().println("<th>Title</th>");
            response.getWriter().println("<th>Description</th>");
            response.getWriter().println("<th>Budget</th>");
            response.getWriter().println("<th>Deadline</th>");
            response.getWriter().println("<th>Status</th>");
            response.getWriter().println("</tr>");

            for (Gig gig : gigs) {

                // Show only open gigs
                if (!"Open".equalsIgnoreCase(gig.getStatus())) {
                    continue;
                }

                // Apply search filter
                if (search != null && !search.trim().isEmpty()) {

                    String title = gig.getTitle() == null
                            ? ""
                            : gig.getTitle().toLowerCase();

                    String description = gig.getDescription() == null
                            ? ""
                            : gig.getDescription().toLowerCase();

                    if (!title.contains(search)
                            && !description.contains(search)) {
                        continue;
                    }
                }

                found = true;

                response.getWriter().println("<tr>");

                response.getWriter().println(
                        "<td>" + gig.getGigId() + "</td>"
                );

                response.getWriter().println(
                        "<td>" + gig.getPosterId() + "</td>"
                );

                response.getWriter().println(
                        "<td>" + gig.getTitle() + "</td>"
                );

                response.getWriter().println(
                        "<td>" + gig.getDescription() + "</td>"
                );

                response.getWriter().println(
                        "<td>" + gig.getBudget() + "</td>"
                );

                response.getWriter().println(
                        "<td>" + gig.getDeadline() + "</td>"
                );

                response.getWriter().println(
                        "<td>" + gig.getStatus() + "</td>"
                );

                response.getWriter().println("</tr>");
            }

            response.getWriter().println("</table>");

            if (!found) {
                response.getWriter().println(
                        "<p>No matching open gigs found.</p>"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println("<h1>Server Error</h1>");
            response.getWriter().println(
                    "<p>Could not load gigs from the database.</p>"
            );
        }
    }
}