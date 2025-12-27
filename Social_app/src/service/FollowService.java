package service;

import dao.UserDAO;
import model.User;
import dao.FollowDAO;

import javax.swing.*;
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
public class FollowService {

    // objects of DAO
    private final FollowDAO followDAO;
    private final UserDAO userDAO;

    // Constructor
    public FollowService() {
        this.followDAO = new FollowDAO();
        this.userDAO = new UserDAO();
    }

    // It returns arraylist of type USERS (followers)
    public ArrayList<User> getFollowers(int userID) {

        if (!userDAO.exist(userID)) { throw new IllegalArgumentException("User does not exist"); }

        ArrayList<Integer> followerIDs = followDAO.getFollowerIDs(userID);
        return userDAO.getUsersByIds(followerIDs);
    }

    // It returns arraylist of type USERS (followings)
    public ArrayList<User> getFollowings(int userID) {

        if (!userDAO.exist(userID)) {throw new IllegalArgumentException("User does not exist");}

        ArrayList<Integer> followingIDs = followDAO.getFollowingIDs(userID);
        return userDAO.getUsersByIds(followingIDs);
    }

    // Set a follow connection, look how we are handling exceptions in this layer, and how we are calling DAO methods
    public void followUser(int followerID, int followingID) {

            if (followerID == followingID) throw new IllegalArgumentException("User cannot follow himself");
            if (!userDAO.exist(followerID)) throw new IllegalArgumentException("follower does not exist");
            if (!userDAO.exist(followingID)) throw new IllegalArgumentException("following does not exist");
            if (followDAO.existFollow(followerID, followingID)) throw new IllegalArgumentException("Following is already followed");

            boolean success = followDAO.insertFollow(followerID, followingID);
            if (!success) {
                throw new RuntimeException("Failed to follow user");
            }
    }

    // Remove the follow connection
    public void unfollowUser(int followerID, int followingID) {

            if (followerID == followingID) throw new IllegalArgumentException("User cannot unfollow himself");
            if (!userDAO.exist(followerID)) throw new IllegalArgumentException("follower does not exist");
            if (!userDAO.exist(followingID)) throw new IllegalArgumentException("following does not exist");
            if (!followDAO.existFollow(followerID, followingID)) throw new IllegalArgumentException("Following is already not followed");

            boolean success = followDAO.deleteFollow(followerID, followingID);
            if (!success) {
                throw new RuntimeException("Failed to unfollow user");
            }
    }

    // Checks if there is a connection or not
    public boolean isFollowing(int followerID, int followingID) {
        return followDAO.existFollow(followerID, followingID);
    }
}
