package com.campusgig.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

import com.campusgig.dao.GigDAO;
import com.campusgig.model.Gig;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/gigs")
public class GigServlet extends HttpServlet {

    private final GigDAO gigDAO = new GigDAO();

    // GET /CampusGig/gigs
    // GET /CampusGig/gigs?id=1
    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        String idParameter = request.getParameter("id");

        // Get one gig
        if (idParameter != null && !idParameter.isBlank()) {

            try {
                int gigId = Integer.parseInt(idParameter);

                Gig gig = gigDAO.getGigById(gigId);

                if (gig == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"success\":false,\"message\":\"Gig not found\"}");
                    return;
                }

                out.print(gigToJson(gig));

            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false,\"message\":\"Invalid gig ID\"}");
            }

            return;
        }

        // Get all gigs
        List<Gig> gigs = gigDAO.getAllGigs();

        out.print("[");
        for (int i = 0; i < gigs.size(); i++) {
            out.print(gigToJson(gigs.get(i)));

            if (i < gigs.size() - 1) {
                out.print(",");
            }
        }
        out.print("]");
    }

    // POST /CampusGig/gigs
    // Creates a new gig
    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            String posterIdParam = request.getParameter("posterId");
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String budgetParam = request.getParameter("budget");
            String deadlineParam = request.getParameter("deadline");

            // Basic validation
            if (posterIdParam == null || posterIdParam.isBlank()
                    || title == null || title.isBlank()
                    || description == null || description.isBlank()
                    || budgetParam == null || budgetParam.isBlank()
                    || deadlineParam == null || deadlineParam.isBlank()) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(
                    "{\"success\":false,\"message\":\"All fields are required\"}"
                );
                return;
            }

            int posterId = Integer.parseInt(posterIdParam);
            double budget = Double.parseDouble(budgetParam);
            LocalDate deadline = LocalDate.parse(deadlineParam);

            if (posterId <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(
                    "{\"success\":false,\"message\":\"Invalid poster ID\"}"
                );
                return;
            }

            if (budget <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(
                    "{\"success\":false,\"message\":\"Budget must be greater than zero\"}"
                );
                return;
            }

            Gig gig = new Gig();

            gig.setPosterId(posterId);
            gig.setTitle(title.trim());
            gig.setDescription(description.trim());
            gig.setBudget(budget);
            gig.setDeadline(deadline);
            gig.setStatus("Open");

            boolean success = gigDAO.addGig(gig);

            if (success) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print(
                    "{\"success\":true,\"message\":\"Gig created successfully\"}"
                );
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(
                    "{\"success\":false,\"message\":\"Failed to create gig\"}"
                );
            }

        } catch (NumberFormatException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(
                "{\"success\":false,\"message\":\"Invalid number format\"}"
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(
                "{\"success\":false,\"message\":\"Invalid input data\"}"
            );
        }
    }

    private String gigToJson(Gig gig) {

        return "{"
                + "\"gigId\":" + gig.getGigId() + ","
                + "\"posterId\":" + gig.getPosterId() + ","
                + "\"title\":\"" + escapeJson(gig.getTitle()) + "\","
                + "\"description\":\"" + escapeJson(gig.getDescription()) + "\","
                + "\"budget\":" + gig.getBudget() + ","
                + "\"deadline\":\"" + gig.getDeadline() + "\","
                + "\"status\":\"" + escapeJson(gig.getStatus()) + "\""
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