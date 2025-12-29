package dao;

import model.User;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;

/* I know you will get frustrated from this file ya nour but dw
    first 2 methods are like helper methods that will be used within the class to avoid redundant code
    so that's why they are private
    take a look at them
    the rest are public methods and easy to guess quickly what are they doing and I put comments before each one
 */
public class UserDAO implements CRUDInterface<User> {

    // it is used in many methods when we get data from db into user
    private User mapRowToUser(ResultSet rs) throws SQLException {

        User user = new User();
        user.setUserName(rs.getString("username"));
        user.setFirstName(rs.getString("firstname"));
        user.setLastName(rs.getString("lastname"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setBirthDate(rs.getDate("birthdate").toLocalDate());
        user.setBio(rs.getString("bio"));

        return user;
    }

    // generate question marks that will be used in getUsersByIds(List<Integer> ids)
    private String generatePlaceholders(int size) {
        if (size <= 0) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append("?");
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }


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
        }

        return false;
    }

    // get a user from the system (just ONE user)
    public User getDetails(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRowToUser(rs);
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

    /* Update a user on the system
       it updates based on demand for example:
       if Nour wants to change her username to Malak so the sql statement will be: UPDATE users SET username=Malak WHERE id=2;
       but if Nour wants to change her pass so the sql statement will be: UPDATE users SET password=nour123 WHERE id=2;

       so the update method is adapting based on the parameters itself
     */
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

        if (values.isEmpty()) {
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

    // Deletes a user from the system
    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int numberOfRows = stmt.executeUpdate();
            return numberOfRows > 0;
        }
        catch(SQLException e){
            System.out.println("Could not delete user");
        }

        return false;
    }

    // check if a user exists on the system
    public boolean exist(Connection conn, int id) {
        String sql = "SELECT id FROM users WHERE id = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return true;
            }
        }
        catch(SQLException e){
//          e.printStackTrace();
            System.out.println("Unable to execute query");
        }

        return false;
    }

    // Returns array of User(S) based on an array of IDs
    public ArrayList<User> getUsersByIds(Connection conn, ArrayList<Integer> ids) {

        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        String placeholders = generatePlaceholders(ids.size());

        String sql = "SELECT * FROM users WHERE id IN (" + placeholders + ")";

        ArrayList<User> users = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < ids.size(); i++) {
                stmt.setInt(i + 1, ids.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        }
        catch (SQLException e){
//            e.printStackTrace();
            System.out.println("Unable to execute query");
            throw new RuntimeException("Failed to fetch users by IDs", e);
        }

        return users;
    }
}

