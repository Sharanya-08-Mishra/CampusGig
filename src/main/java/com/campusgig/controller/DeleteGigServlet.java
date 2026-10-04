package com.campusgig.controller;

import java.io.IOException;
import java.io.PrintWriter;

import com.campusgig.dao.GigDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/delete-gig")
public class DeleteGigServlet extends HttpServlet {

    private final GigDAO gigDAO = new GigDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processDelete(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processDelete(request, response);
    }

    private void processDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idText = request.getParameter("id");
        if (idText == null || idText.isBlank()) {
            idText = request.getParameter("gigId");
        }

        boolean isAjax = "true".equalsIgnoreCase(request.getParameter("ajax"))
                || "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));

        int gigId = 0;
        try {
            gigId = Integer.parseInt(idText);
        } catch (Exception e) {
            if (isAjax) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().print("{\"success\":false,\"message\":\"Invalid gig ID\"}");
            } else {
                response.sendRedirect("browse-gigs.html");
            }
            return;
        }

        boolean success = gigDAO.deleteGig(gigId);

        if (isAjax) {
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            if (success) {
                out.print("{\"success\":true,\"message\":\"Gig deleted successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\":false,\"message\":\"Failed to delete gig\"}");
            }
        } else {
            response.sendRedirect("browse-gigs.html");
        }
    }
}
