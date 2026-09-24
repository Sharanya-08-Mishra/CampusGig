package com.campusgig.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/upload")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 10 * 1024 * 1024,
        maxRequestSize = 12 * 1024 * 1024
)
public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Part filePart = request.getPart("portfolio");

        if (filePart == null || filePart.getSize() == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(
                    "{\"success\":false,\"message\":\"No file uploaded\"}"
            );
            return;
        }

        String originalFileName = Paths
                .get(filePart.getSubmittedFileName())
                .getFileName()
                .toString();

        String extension = "";

        int dotIndex = originalFileName.lastIndexOf('.');

        if (dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);
        }

        String storedFileName =
                UUID.randomUUID() + extension;

        String uploadDirectory =
                getServletContext().getInitParameter(
                        "portfolioUploadDirectory"
                );

        if (uploadDirectory == null || uploadDirectory.isBlank()) {
            uploadDirectory = "uploads";
        }

        Path uploadPath =
                Paths.get(uploadDirectory).toAbsolutePath();

        Files.createDirectories(uploadPath);

        Path targetFile =
                uploadPath.resolve(storedFileName);

        try (InputStream inputStream = filePart.getInputStream();
             OutputStream outputStream =
                     Files.newOutputStream(targetFile)) {

            inputStream.transferTo(outputStream);
        }

        response.setStatus(HttpServletResponse.SC_CREATED);

        response.getWriter().print(
                "{"
                + "\"success\":true,"
                + "\"message\":\"File uploaded successfully\","
                + "\"fileName\":\"" + storedFileName + "\","
                + "\"filePath\":\"" + targetFile.toString().replace("\\", "\\\\") + "\""
                + "}"
        );
    }
}
