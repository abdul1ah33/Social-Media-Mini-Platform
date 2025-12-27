package service;

import dao.UserDAO;
import model.User;
import dao.FollowDAO;
import java.util.ArrayList;

public class FollowService {

    private final FollowDAO followDAO;
    private final UserDAO userDAO;

    public FollowService() {
        this.followDAO = new FollowDAO();
        this.userDAO = new UserDAO();
    }

    public ArrayList<User> getFollowers(int userID) {

        if (!userDAO.exist(userID)) { throw new IllegalArgumentException("User does not exist"); }

        ArrayList<Integer> followerIDs = followDAO.getFollowerIDs(userID);
        return userDAO.getUsersByIds(followerIDs);
    }


    public ArrayList<User> getFollowings(int userID) {

        if (!userDAO.exist(userID)) {throw new IllegalArgumentException("User does not exist");}

        ArrayList<Integer> followingIDs = followDAO.getFollowingIDs(userID);
        return userDAO.getUsersByIds(followingIDs);
    }

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

    public boolean isFollowing(int followerID, int followingID) {
        return followDAO.existFollow(followerID, followingID);
    }
}
