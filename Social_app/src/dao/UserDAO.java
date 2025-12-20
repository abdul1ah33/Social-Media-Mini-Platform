package dao;

import model.User;
import util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO {

    public boolean createUser(User user) {
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
            e.printStackTrace();
            return false;
        }
    }
}
