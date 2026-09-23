package com.campusgig.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.campusgig.model.Application;
import com.campusgig.util.DBConnection;

public class ApplicationDAO {

    // Add a new application
    public boolean addApplication(Application application) {

        String sql = "INSERT INTO Applications " +
                "(gig_id, applicant_id, pitch_text, portfolio_path, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, application.getGigId());
            statement.setInt(2, application.getApplicantId());
            statement.setString(3, application.getPitchText());
            statement.setString(4, application.getPortfolioPath());
            statement.setString(5, application.getStatus());

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get an application by ID
    public Application getApplicationById(int applicationId) {

        String sql = "SELECT * FROM Applications WHERE application_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, applicationId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                Application application = new Application();

                application.setApplicationId(
                        resultSet.getInt("application_id"));
                application.setGigId(
                        resultSet.getInt("gig_id"));
                application.setApplicantId(
                        resultSet.getInt("applicant_id"));
                application.setPitchText(
                        resultSet.getString("pitch_text"));
                application.setPortfolioPath(
                        resultSet.getString("portfolio_path"));
                application.setStatus(
                        resultSet.getString("status"));

                return application;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Get all applications for a particular gig
    public List<Application> getApplicationsByGigId(int gigId) {

        List<Application> applications = new ArrayList<>();

        String sql = "SELECT * FROM Applications WHERE gig_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, gigId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                Application application = new Application();

                application.setApplicationId(
                        resultSet.getInt("application_id"));
                application.setGigId(
                        resultSet.getInt("gig_id"));
                application.setApplicantId(
                        resultSet.getInt("applicant_id"));
                application.setPitchText(
                        resultSet.getString("pitch_text"));
                application.setPortfolioPath(
                        resultSet.getString("portfolio_path"));
                application.setStatus(
                        resultSet.getString("status"));

                applications.add(application);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return applications;
    }

    // Get all applications submitted by a student
    public List<Application> getApplicationsByApplicantId(int applicantId) {

        List<Application> applications = new ArrayList<>();

        String sql = "SELECT * FROM Applications WHERE applicant_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, applicantId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                Application application = new Application();

                application.setApplicationId(
                        resultSet.getInt("application_id"));
                application.setGigId(
                        resultSet.getInt("gig_id"));
                application.setApplicantId(
                        resultSet.getInt("applicant_id"));
                application.setPitchText(
                        resultSet.getString("pitch_text"));
                application.setPortfolioPath(
                        resultSet.getString("portfolio_path"));
                application.setStatus(
                        resultSet.getString("status"));

                applications.add(application);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return applications;
    }

    // Update application status
    public boolean updateApplicationStatus(int applicationId, String status) {

        String sql = "UPDATE Applications SET status = ? " +
                "WHERE application_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, applicationId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}