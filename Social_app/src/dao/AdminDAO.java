package dao;

import model.User;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    public User getAdmin(String username, String password) {
        // Assuming you have an 'admins' table. If not, this needs to be created in your DB.
        String sql = "SELECT * FROM admins WHERE username = ? AND password = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Map admin to User object for session consistency
                User adminUser = new User();
                adminUser.setID(rs.getInt("id"));
                adminUser.setUserName(rs.getString("username"));
                adminUser.setPassword(rs.getString("password"));
                adminUser.setFirstName(rs.getString("firstname"));
                adminUser.setLastName(rs.getString("lastname"));
                adminUser.setEmail(rs.getString("email"));
                adminUser.setBirthDate(rs.getDate("birthdate").toLocalDate());
                
                // Admin might not have a bio or profile picture in this table, set defaults
                adminUser.setBio("Admin Account");
                
                return adminUser;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
