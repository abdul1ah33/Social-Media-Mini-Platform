package test.Service;

import service.FollowService;
import model.User;

import java.util.ArrayList;

public class TestFollowService {

    public static void main(String[] args) {
        FollowService followService = new FollowService();

        // Make sure these are valid user IDs in your DB
        int followerId = 14;
        int followingId = 13;

        try {
            System.out.println("=== FOLLOW USER TEST ===");
            followService.followUser(followerId, followingId);
            System.out.println("followUser executed successfully");

            boolean isFollowing = followService.isFollowing(followerId, followingId);
            System.out.println("isFollowing: " + isFollowing);

            int followersCount = followService.getFollowersCount(followingId);
            int followingCount = followService.getFollowingsCount(followerId);
            System.out.println("Followers count for " + followingId + ": " + followersCount);
            System.out.println("Following count for " + followerId + ": " + followingCount);

            ArrayList<User> followersList = followService.getFollowers(followingId);
            ArrayList<User> followingList = followService.getFollowings(followerId);
            System.out.println("Followers list for " + followingId + ": " + followersList.size());
            System.out.println("Following list for " + followerId + ": " + followingList.size());

            System.out.println("\n=== UNFOLLOW USER TEST ===");
            followService.unfollowUser(followerId, followingId);
            System.out.println("unfollowUser executed successfully");

            isFollowing = followService.isFollowing(followerId, followingId);
            System.out.println("isFollowing after unfollow: " + isFollowing);

            followersCount = followService.getFollowersCount(followingId);
            followingCount = followService.getFollowingsCount(followerId);
            System.out.println("Followers count after unfollow for " + followingId + ": " + followersCount);
            System.out.println("Following count after unfollow for " + followerId + ": " + followingCount);

            followersList = followService.getFollowers(followingId);
            followingList = followService.getFollowings(followerId);
            System.out.println("Followers list after unfollow for " + followingId + ": " + followersList.size());
            System.out.println("Following list after unfollow for " + followerId + ": " + followingList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
