package test.DAO;

import dao.FollowDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import util.DBConnection;

public class TestFollowDAO {

    public static void main(String[] args) {
        FollowDAO followDAO = new FollowDAO();

        int followerId = 14; // must exist in users table
        int followingId = 13; // must exist in users table

        try (Connection conn = DBConnection.getConnection()) {

            System.out.println("=== INSERT FOLLOW TEST ===");
            boolean inserted = followDAO.insertFollow(conn, followerId, followingId);
            System.out.println("insertFollow: " + inserted);

            System.out.println("\n=== EXIST FOLLOW TEST ===");
            boolean exists = followDAO.existFollow(conn, followerId, followingId);
            System.out.println("existFollow: " + exists);

            System.out.println("\n=== GET FOLLOWING IDs TEST ===");
            ArrayList<Integer> followingIDs = followDAO.getFollowingIDs(conn, followerId);
            System.out.println("getFollowingIDs for " + followerId + ": " + followingIDs);

            System.out.println("\n=== GET FOLLOWER IDs TEST ===");
            ArrayList<Integer> followerIDs = followDAO.getFollowerIDs(conn, followingId);
            System.out.println("getFollowerIDs for " + followingId + ": " + followerIDs);

            System.out.println("\n=== GET FOLLOWERS COUNT TEST ===");
            int followersCount = followDAO.getFollowersCount(conn, followingId);
            System.out.println("getFollowersCount for " + followingId + ": " + followersCount);

            System.out.println("\n=== GET FOLLOWINGS COUNT TEST ===");
            int followingsCount = followDAO.getFollowingsCount(conn, followerId);
            System.out.println("getFollowingsCount for " + followerId + ": " + followingsCount);

            System.out.println("\n=== DELETE FOLLOW TEST ===");
            boolean deleted = followDAO.deleteFollow(conn, followerId, followingId);
            System.out.println("deleteFollow: " + deleted);

            System.out.println("\n=== VERIFY DELETION TEST ===");
            exists = followDAO.existFollow(conn, followerId, followingId);
            System.out.println("existFollow after deletion: " + exists);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
