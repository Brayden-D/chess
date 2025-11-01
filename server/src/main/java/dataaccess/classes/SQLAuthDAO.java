package dataaccess.classes;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.AuthDAO;
import model.AuthData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {

    DatabaseManager databaseManager;

    public SQLAuthDAO() {
        try {
            DatabaseManager.createDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DELETE FROM auth");

        } catch (SQLException | DataAccessException ex) {
            throw new RuntimeException("Failed to clear auth table", ex);
        }
    }

    public AuthData createAuthData(String username) {
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(username, token);
        String sql = "INSERT INTO auth (username, token) VALUES (?,?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, token);

            stmt.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error writing auth data to database", e);
        }

        return authData;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth WHERE token = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token from database", e);
        }
    }

    public String getUsername(String authToken) {
        String sql = "SELECT username FROM auth WHERE token = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                } else {
                    return null; // Token not found
                }
            }

        } catch (SQLException | DataAccessException ignored) {
        }
        return null;
    }

    public boolean authTokenExists(String authToken) {
        String sql = "SELECT 1 FROM auth WHERE token = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true if at least one record exists
            }

        } catch (Exception ignored) {
        }
        return false;
    }

}
