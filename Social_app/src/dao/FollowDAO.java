package dao;

import model.User;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;

public class FollowDAO {

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
            System.out.println("NO follow relationship with FollowerID and FollowingID found");
        }

        return false;
    }
}