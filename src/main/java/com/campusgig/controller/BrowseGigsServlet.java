package com.campusgig.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/browse-gigs")
public class BrowseGigsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        String search = request.getParameter("search");

        response.getWriter().println("<h1>CampusGig - Browse Gigs</h1>");

        if (search != null && !search.trim().isEmpty()) {
            response.getWriter().println(
                    "<p>Search request received: " + search + "</p>"
            );
        } else {
            response.getWriter().println(
                    "<p>Showing all available gigs.</p>"
            );
        }

        response.getWriter().println(
                "<p>Gig data will be loaded from the database next.</p>"
        );

        response.getWriter().println(
                "<a href='browse-gigs.html'>Back to Browse Gigs</a>"
        );
    }
}