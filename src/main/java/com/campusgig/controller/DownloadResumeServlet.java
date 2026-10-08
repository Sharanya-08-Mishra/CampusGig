package com.campusgig.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/resume")
public class DownloadResumeServlet extends HttpServlet {

    public static final String UPLOAD_DIR = "C:\\CampusGig\\uploads";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("file");
        if (name == null || name.isBlank()) {
            resp.sendError(400);
            return;
        }
        name = Paths.get(name).getFileName().toString(); // block path traversal

        Path file = Paths.get(UPLOAD_DIR).resolve(name);
        if (!Files.exists(file)) file = Paths.get("uploads").toAbsolutePath().resolve(name);
        if (!Files.exists(file)) {
            resp.sendError(404, "File not found");
            return;
        }

        String type = getServletContext().getMimeType(name);
        resp.setContentType(type != null ? type : "application/octet-stream");
        resp.setHeader("Content-Disposition", "inline; filename=\"" + name + "\"");
        resp.setContentLengthLong(Files.size(file));
        Files.copy(file, resp.getOutputStream());
    }
}
