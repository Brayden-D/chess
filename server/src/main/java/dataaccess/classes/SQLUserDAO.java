package dataaccess.classes;

import dataaccess.DataAccessException;
import model.UserData;

import java.sql.*;

import dataaccess.DatabaseManager;
import org.mindrot.jbcrypt.BCrypt;

public class SQLUserDAO {

    public void create(UserData userData) {
        if (userData == null || userData.username() == null
        || userData.password() == null || userData.email() == null) {
            throw new RuntimeException("User data is null");
        }

        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userData.username());
            stmt.setString(2, BCrypt.hashpw(userData.password(), BCrypt.gensalt()));
            stmt.setString(3, userData.email());

            stmt.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error writing user data to database", e);
        }
    }

    public void clear() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DELETE FROM users");

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error: unable to clear users", e);
        }
    }

    public UserData findUser(String username) {
        String sql = "SELECT username, password, email FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String _username = rs.getString("username");
                    String password = rs.getString("password");
                    String email = rs.getString("email");

                    return new UserData(_username, password, email);
                } else {
                    return null;
                }
            }

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error finding user", e);
        }
    }

}
