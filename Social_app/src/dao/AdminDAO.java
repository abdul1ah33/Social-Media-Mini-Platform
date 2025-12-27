package dao;

import model.Admin;
import model.User;
import util.DBConnection;
import java.sql.*;

public class AdminDAO implements DAOInterface<Admin>{

    // Add an admin to the system
    @Override
    public boolean add(Admin admin) {
        String sql = "INSERT INTO users (username, firstname, lastname, email, password, birthdate, bio) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, admin.getUserName());
            stmt.setString(2, admin.getFirstName());
            stmt.setString(3, admin.getLastName());
            stmt.setString(4, admin.getEmail());
            stmt.setString(5, admin.getPassword());
            stmt.setDate(6, Date.valueOf(admin.getBirthDate()));

            int numberOfRows = stmt.executeUpdate();
            return numberOfRows > 0;
        }
        catch (SQLException e){
//            e.printStackTrace();
            System.out.println("Could not add admin");
            System.out.println("SQLException: " + e.getMessage());
            return false;
        }
    }

    public Admin getAccountDetails(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Admin Admin = new Admin();
                Admin.setUserName(rs.getString("username"));
                Admin.setFirstName(rs.getString("firstname"));
                Admin.setLastName(rs.getString("lastname"));
                Admin.setEmail(rs.getString("email"));
                Admin.setBirthDate(rs.getDate("birthdate").toLocalDate());
                return Admin;
            }

            System.out.println("No user with id " + id + " was found");
            return null;

        }
        catch (SQLException e){
//          e.printStackTrace();
            System.out.println("NO USER FOUND");
            return null;
        }
    }

    public boolean update(Admin admin, int id) {

        return false;
    }
}
