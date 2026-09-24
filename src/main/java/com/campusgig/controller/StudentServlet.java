package com.campusgig.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.campusgig.dao.StudentDAO;
import com.campusgig.model.Student;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();

    // GET /CampusGig/students?id=1
    // GET /CampusGig/students
    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {

            String idParam = request.getParameter("id");

            if (idParam != null && !idParam.isBlank()) {

                int studentId = Integer.parseInt(idParam);

                Student student =
                        studentDAO.getStudentById(studentId);

                if (student == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(
                            "{\"success\":false,\"message\":\"Student not found\"}"
                    );
                    return;
                }

                out.print(studentToJson(student));
                return;
            }

            List<Student> students =
                    studentDAO.getAllStudents();

            out.print("[");

            for (int i = 0; i < students.size(); i++) {

                out.print(studentToJson(students.get(i)));

                if (i < students.size() - 1) {
                    out.print(",");
                }
            }

            out.print("]");

        } catch (NumberFormatException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            out.print(
                    "{\"success\":false,\"message\":\"Invalid student ID\"}"
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            out.print(
                    "{\"success\":false,\"message\":\"Server error\"}"
            );
        }
    }

    // POST /CampusGig/students
    // Creates a new student
    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {

            String name = request.getParameter("name");
            String email = request.getParameter("email");

            if (name == null || name.isBlank()
                    || email == null || email.isBlank()) {

                response.setStatus(
                        HttpServletResponse.SC_BAD_REQUEST
                );

                out.print(
                        "{\"success\":false,\"message\":\"Name and email are required\"}"
                );

                return;
            }

            name = name.trim();
            email = email.trim();

            if (!email.contains("@") || !email.contains(".")) {

                response.setStatus(
                        HttpServletResponse.SC_BAD_REQUEST
                );

                out.print(
                        "{\"success\":false,\"message\":\"Enter a valid email address\"}"
                );

                return;
            }

            Student student = new Student();

            student.setName(name);
            student.setEmail(email);

            boolean success =
                    studentDAO.addStudent(student);

            if (success) {

                response.setStatus(
                        HttpServletResponse.SC_CREATED
                );

                out.print(
                        "{\"success\":true,\"message\":\"Student registered successfully\"}"
                );

            } else {

                response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );

                out.print(
                        "{\"success\":false,\"message\":\"Failed to register student\"}"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            out.print(
                    "{\"success\":false,\"message\":\"Server error\"}"
            );
        }
    }

    private String studentToJson(Student student) {

        return "{"
                + "\"studentId\":" + student.getStudentId() + ","
                + "\"name\":\""
                + escapeJson(student.getName()) + "\","
                + "\"email\":\""
                + escapeJson(student.getEmail()) + "\""
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
