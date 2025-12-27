package dao;

import model.User;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;

/* if you understands userdao so this class will be easy
    basically it manage all follow functions to and from db
 */
public class FollowDAO {

    // insert a follow relation between follower and following
    public boolean insertFollow(int followerID, int followingID) {
        String sql = "INSERT INTO follows (follower_id, following_id) VALUES (?, ?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, followerID);
            stmt.setInt(2, followingID);

            int numberOfRows = stmt.executeUpdate();
            return numberOfRows > 0;
        }
        catch (SQLException e){
//            e.printStackTrace();
            System.out.println("Could not add FollowerID and FollowingID");
            System.out.println("SQLException: " + e.getMessage());
            return false;
        }
    }

    // Delete
    public boolean deleteFollow(int followerID, int followingID) {
        String sql = "DELETE FROM follows WHERE follower_id = ? AND following_id = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, followerID);
            stmt.setInt(2, followingID);

            int numberOfRows = stmt.executeUpdate();
            return numberOfRows > 0;
        }
        catch(SQLException e){
            System.out.println("Could not delete FollowerID and FollowingID");
        }

        return false;
    }

    // returns an array of IDs of a specific user followings
    public ArrayList<Integer> getFollowingIDs(int userID) {
        String sql = "SELECT following_id FROM follows WHERE follower_id = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            ArrayList<Integer> followingIDs = new ArrayList<>();
            while(rs.next()) {
                followingIDs.add(rs.getInt("following_id"));
            }
            return followingIDs;
        }
        catch (SQLException e){
//            e.printStackTrace();
            System.out.println("No followings with UserID " + userID + " was found");
        }

        return null;
    }

    // returns an array of IDs of a specific user followers
    public ArrayList<Integer> getFollowerIDs(int userID) {
        String sql = "SELECT follower_id FROM follows WHERE following_id = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            ArrayList<Integer> followerIDs = new ArrayList<>();
            while (rs.next()) {
                followerIDs.add(rs.getInt("follower_id"));
            }
            return followerIDs;
        }
        catch (SQLException e){
//            e.printStackTrace();
            System.out.println("No followings with UserID " + userID + " was found");
        }

        return null;
    }

    /* THE FOLLOWING PART IS COMMENTED BEC OF BAD DESIGN PATTERN
       INSTEAD I PUT IT IN FOLLOWSERVICE IN SERVICE FOLDER
     */

//    public ArrayList<User> getFollowings(int userID) {
//        String sql = "SELECT u.* FROM users u JOIN follows f ON u.id = f.following_id WHERE f.follower_id = ?;";
//
//        try(Connection conn = DBConnection.getConnection();
//            PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setInt(1, userID);
//            ResultSet rs = stmt.executeQuery();
//
//            ArrayList<User> followings = new ArrayList<>();
//            while(rs.next()) {
//                User user = new User();
//                user.setUserName(rs.getString("username"));
//                user.setFirstName(rs.getString("firstname"));
//                user.setLastName(rs.getString("lastname"));
//                user.setEmail(rs.getString("email"));
//                user.setBio(rs.getString("bio"));
//                user.setBirthDate(rs.getDate("birthdate").toLocalDate());
//                followings.add(user);
//           }
//            return followings;
//        }
//        catch (SQLException e){
////            e.printStackTrace();
//            System.out.println("No followings with UserID " + userID + " was found");
//        }
//
//        return null;
//    }
//
//
//    public ArrayList<User> getFollowers(int userID) {
//        String sql = "SELECT u.* FROM users u JOIN follows f ON u.id = f.follower_id WHERE f.following_id = ?;";
//
//        try(Connection conn = DBConnection.getConnection();
//            PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setInt(1, userID);
//            ResultSet rs = stmt.executeQuery();
//
//            ArrayList<User> followers = new ArrayList<>();
//            while (rs.next()) {
//                User user = new User();
//                user.setUserName(rs.getString("username"));
//                user.setFirstName(rs.getString("firstname"));
//                user.setLastName(rs.getString("lastname"));
//                user.setEmail(rs.getString("email"));
//                user.setBio(rs.getString("bio"));
//                user.setBirthDate(rs.getDate("birthdate").toLocalDate());
//                followers.add(user);
//            }
//            return followers;
//        }
//        catch (SQLException e){
////            e.printStackTrace();
//            System.out.println("No followings with UserID " + userID + " was found");
//        }
//
//        return null;
//    }

    // checks if a follow relation exists
    public boolean existFollow(int followerID, int followingID) {
        String sql = "SELECT * FROM follows WHERE follower_id = ? AND following_id = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, followerID);
            stmt.setInt(2, followingID);
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
}