package service;

import dao.NewBetaFollowDAO;
import dao.NewBetaUserDAO;
import dao.UserDAO;
import model.User;
import dao.FollowDAO;
import util.DBConnection;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/* here is the start of our intense backend logic
   this file acts as a layer between DAO and UI
   it is responsible for using functions we created in followDAO, handling them and like putting them in one
   single function to give them to the UI layer
    ----------------------------------------------------------------------------------------------------------

   OUR BACKEND WORK FLOW till now IS AS FOLLOWS:

   ACCESSING DB --> DAO (DATA ACCESS OBJECT) --> SERVICE --> UI

   DAO: it is the layer that is responsible to have direct contact with DB by accessing data making them objects
        and also accessing object and store them as data
        that's why it is called DATA ACCESS OBJECT
    -----------------------------------------------------------------------------------------------------------

    have a quick look at the first 6 lines:
        yes as you see they are objects of our classes in DAO and then a constructor to initialize them automatically
        these objects will be called in every method of this class.
    -----------------------------------------------------------------------------------------------------------
    I also want you to notice how we used many throw exceptions in this layer.
 */
public class NewBetaFollowService {

    // objects of DAO
    private final NewBetaFollowDAO followDAO;
    private final NewBetaUserDAO userDAO;

    // Constructor
    public NewBetaFollowService() {
        this.followDAO = new NewBetaFollowDAO();
        this.userDAO = new NewBetaUserDAO();
    }

//    // It returns arraylist of type USERS (followers)
//    public ArrayList<User> getFollowers(int userID) {
//
//        if (!userDAO.exist(userID)) { throw new IllegalArgumentException("User does not exist"); }
//
//        ArrayList<Integer> followerIDs = followDAO.getFollowerIDs(userID);
//        return userDAO.getUsersByIds(followerIDs);
//    }
//
//    // It returns arraylist of type USERS (followings)
//    public ArrayList<User> getFollowings(int userID) {
//
//        if (!userDAO.exist(userID)) {throw new IllegalArgumentException("User does not exist");}
//
//        ArrayList<Integer> followingIDs = followDAO.getFollowingIDs(userID);
//        return userDAO.getUsersByIds(followingIDs);
//    }

    // Set a follow connection, look how we are handling exceptions in this layer, and how we are calling DAO methods
    public void unfollowUser(int followerID, int followingID) {

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            if (followerID == followingID) throw new IllegalArgumentException("User cannot unfollow himself");
            if (!userDAO.exist(conn, followerID)) throw new IllegalArgumentException("follower does not exist");
            if (!userDAO.exist(conn, followingID)) throw new IllegalArgumentException("following does not exist");
            if (!followDAO.existFollow(conn, followerID, followingID))
                throw new IllegalArgumentException("Following is already not followed");

            boolean success = followDAO.deleteFollow(conn, followerID, followingID);
            if (!success) {
                throw new RuntimeException("Failed to unfollow user");
            }

            conn.commit();
        }
        catch(Exception e){
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException(e.getMessage(), e);
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //
    // Checks if there is a connection or not
    public boolean isFollowing(int followerID, int followingID) {
        try (Connection conn = DBConnection.getConnection())
        {
            return followDAO.existFollow(conn, followerID, followingID);
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }
}
