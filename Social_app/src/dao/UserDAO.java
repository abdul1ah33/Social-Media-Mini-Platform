package dao;

import model.User;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;

public class UserDAO implements DAOInterface<User> {

    // Add a user to the system
    public boolean add(User user) {
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

    // get a user from the system
    public User getAccountDetails(int id) {
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

    public boolean update(User user, int id) {
        StringBuilder sql = new StringBuilder("UPDATE users SET ");
        ArrayList<Object> values = new ArrayList<>();

        if (user.getUserName() != null) {
            sql.append("username=?, ");
            values.add(user.getUserName());
        }

        if (user.getFirstName() != null) {
            sql.append("firstname=?, ");
            values.add(user.getFirstName());
        }

        if (user.getLastName() != null) {
            sql.append("lastname=?, ");
            values.add(user.getLastName());
        }

        if (user.getEmail() != null) {
            sql.append("email=?, ");
            values.add(user.getEmail());
        }

        if (user.getPassword() != null) {
            sql.append("password=?, ");
            values.add(user.getPassword());
        }

        if (user.getBirthDate() != null) {
            sql.append("birthdate=?, ");
            values.add(user.getBirthDate());
        }

        if (user.getBio() != null) {
            sql.append("bio=?, ");
            values.add(user.getBio());
        }

        if (values.size() <= 0) {
            return false;
        }

        sql.setLength(sql.length() - 2); // remove ", "
        sql.append(" WHERE id = ?");
        values.add(id);

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }

            int numberOfRows = stmt.executeUpdate();
            return numberOfRows > 0;
        }

        catch(SQLException e){
            System.out.println("Could not update user");
            System.out.println("SQLException: " + e.getMessage());
        }

        return false;
    }
}
