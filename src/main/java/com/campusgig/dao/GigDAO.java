package com.campusgig.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.campusgig.model.Gig;
import com.campusgig.util.DBConnection;

public class GigDAO {

    // Add a new gig
    public boolean addGig(Gig gig) {

        String sql = "INSERT INTO Gigs " +
                "(poster_id, title, description, budget, deadline, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, gig.getPosterId());
            statement.setString(2, gig.getTitle());
            statement.setString(3, gig.getDescription());
            statement.setDouble(4, gig.getBudget());
            statement.setDate(5, java.sql.Date.valueOf(gig.getDeadline()));
            statement.setString(6, gig.getStatus());

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get gig by ID
    public Gig getGigById(int gigId) {

        String sql = "SELECT * FROM Gigs WHERE gig_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, gigId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                Gig gig = new Gig();

                gig.setGigId(resultSet.getInt("gig_id"));
                gig.setPosterId(resultSet.getInt("poster_id"));
                gig.setTitle(resultSet.getString("title"));
                gig.setDescription(resultSet.getString("description"));
                gig.setBudget(resultSet.getDouble("budget"));
                gig.setDeadline(
                        resultSet.getDate("deadline").toLocalDate()
                );
                gig.setStatus(resultSet.getString("status"));

                return gig;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Get all gigs
    public List<Gig> getAllGigs() {

        List<Gig> gigs = new ArrayList<>();

        String sql = "SELECT * FROM Gigs ORDER BY gig_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Gig gig = new Gig();

                gig.setGigId(resultSet.getInt("gig_id"));
                gig.setPosterId(resultSet.getInt("poster_id"));
                gig.setTitle(resultSet.getString("title"));
                gig.setDescription(resultSet.getString("description"));
                gig.setBudget(resultSet.getDouble("budget"));
                gig.setDeadline(
                        resultSet.getDate("deadline").toLocalDate()
                );
                gig.setStatus(resultSet.getString("status"));

                gigs.add(gig);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return gigs;
    }

    // Update gig status
    public boolean updateGigStatus(int gigId, String status) {

        String sql = "UPDATE Gigs SET status = ? WHERE gig_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, gigId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}