package dao;

import model.User;
import util.DBConnection;

import java.sql.*;

public class UserDAO {

    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, firstname, lastname, email, password, birthdate, bio) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPassword());
            stmt.setDate(6, Date.valueOf(user.getBirthDate()));
            stmt.setString(7, user.getBio());

            int numberOfRows = stmt.executeUpdate();
            return numberOfRows > 0;
        }
        catch (SQLException e){
//            e.printStackTrace();
            System.out.println("Could not add user");
            System.out.println("SQLException: " + e.getMessage());
            return false;
        }
    }

    public User getUser(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserName(rs.getString("username"));
                user.setFirstName(rs.getString("firstname"));
                user.setLastName(rs.getString("lastname"));
                user.setEmail(rs.getString("email"));
                user.setBio(rs.getString("bio"));
                user.setBirthDate(rs.getDate("birthdate").toLocalDate());
                return user;
            }

            System.out.println("No user with id " + id + " was found");
            return null;

        }
        catch (SQLException e){
//            e.printStackTrace();
            System.out.println("NO USER FOUND");
            return null;
        }
    }
}
