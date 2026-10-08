package com.campusgig.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.campusgig.dao.ApplicationDAO;
import com.campusgig.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/applications")
public class DashboardApiServlet extends HttpServlet {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "").replace("\t", " ");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String sql = "SELECT a.application_id, a.gig_id, a.applicant_id, a.pitch_text, "
                + "a.portfolio_path, a.status, g.title, g.description, g.budget, g.deadline, "
                + "g.status AS gig_status, g.poster_id, s.name AS student_name, s.email AS student_email "
                + "FROM Applications a LEFT JOIN Gigs g ON a.gig_id = g.gig_id "
                + "LEFT JOIN Students s ON a.applicant_id = s.student_id "
                + "ORDER BY a.application_id DESC";

        StringBuilder json = new StringBuilder("[");
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                first = false;
                json.append("{\"id\":").append(rs.getInt("application_id"))
                    .append(",\"gigId\":").append(rs.getInt("gig_id"))
                    .append(",\"gigTitle\":\"").append(esc(rs.getString("title"))).append("\"")
                    .append(",\"gigDescription\":\"").append(esc(rs.getString("description"))).append("\"")
                    .append(",\"gigBudget\":\"").append(rs.getDouble("budget")).append("\"")
                    .append(",\"gigDeadline\":\"").append(esc(String.valueOf(rs.getDate("deadline")))).append("\"")
                    .append(",\"gigStatus\":\"").append(esc(rs.getString("gig_status"))).append("\"")
                    .append(",\"posterId\":").append(rs.getInt("poster_id"))
                    .append(",\"studentName\":\"").append(esc(rs.getString("student_name"))).append("\"")
                    .append(",\"studentEmail\":\"").append(esc(rs.getString("student_email"))).append("\"")
                    .append(",\"applicantId\":").append(rs.getInt("applicant_id"))
                    .append(",\"pitch\":\"").append(esc(rs.getString("pitch_text"))).append("\"")
                    .append(",\"portfolio\":\"").append(esc(rs.getString("portfolio_path"))).append("\"")
                    .append(",\"status\":\"").append(esc(rs.getString("status"))).append("\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().print("{\"error\":\"Could not load applications\"}");
            return;
        }
        json.append("]");
        resp.getWriter().print(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");
        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().print("{\"success\":false,\"message\":\"Invalid id\"}");
            return;
        }

        String status;
        if ("accept".equals(action)) status = "HIRED";
        else if ("reject".equals(action)) status = "REJECTED";
        else {
            resp.setStatus(400);
            resp.getWriter().print("{\"success\":false,\"message\":\"Invalid action\"}");
            return;
        }

        boolean ok = applicationDAO.updateApplicationStatus(id, status);
        if (!ok) resp.setStatus(500);
        resp.getWriter().print("{\"success\":" + ok + ",\"status\":\"" + status + "\"}");
    }
}
