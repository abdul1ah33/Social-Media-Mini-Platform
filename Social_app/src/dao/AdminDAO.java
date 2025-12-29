package dao;

import model.Admin;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
/* Da el admin
   bosi 3la class el user ,and you will understand admin because it has same methods till now
*/
public class AdminDAO implements CRUDInterface<Admin>{

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
        }

        return false;
    }

    // get an admin from the system (just ONE Admin)
    public Admin getDetails(int id) {
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

    // Update data (look at UserDAO for better understanding)
    public boolean update(Admin admin, int id) {
        StringBuilder sql = new StringBuilder("UPDATE admins SET ");
        ArrayList<Object> values = new ArrayList<>();

        if (admin.getUserName() != null) {
            sql.append("username=?, ");
            values.add(admin.getUserName());
        }

        if (admin.getFirstName() != null) {
            sql.append("firstname=?, ");
            values.add(admin.getFirstName());
        }

        if (admin.getLastName() != null) {
            sql.append("lastname=?, ");
            values.add(admin.getLastName());
        }

        if (admin.getEmail() != null) {
            sql.append("email=?, ");
            values.add(admin.getEmail());
        }

        if (admin.getPassword() != null) {
            sql.append("password=?, ");
            values.add(admin.getPassword());
        }

        if (admin.getBirthDate() != null) {
            sql.append("birthdate=?, ");
            values.add(admin.getBirthDate());
        }

        if ( values.isEmpty() ) {
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
            System.out.println("Could not update admin");
            System.out.println("SQLException: " + e.getMessage());
        }

        return false;
    }

    // delete 3ady
    public boolean delete(int id) {
        String sql = "DELETE FROM admins WHERE id = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int numberOfRows = stmt.executeUpdate();
            return numberOfRows > 0;
        }
        catch(SQLException e){
            System.out.println("Could not delete admin");
        }

        return false;
    }
}
