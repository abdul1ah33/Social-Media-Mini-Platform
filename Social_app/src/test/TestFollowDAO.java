package test;

import dao.FollowDAO;

import java.util.ArrayList;

public class TestFollowDAO {

    public static void main(String[] args) {
        FollowDAO followDAO = new FollowDAO();

        // make sure they are valid user ids
        int followerId = 14;
        int followingId = 13;

        // 1 Test insertFollow
        boolean inserted = followDAO.insertFollow(followerId, followingId);
        System.out.println("insertFollow: " + inserted);

        // 2️ Test existFollow
        boolean exists = followDAO.existFollow(followerId, followingId);
        System.out.println("existFollow: " + exists);

        // 3️ Test getFollowingIDs
        ArrayList<Integer> following = followDAO.getFollowingIDs(followerId);
        System.out.println("getFollowingIDs for " + followerId + ": " + following);

        // 4️ Test getFollowerIDs
        ArrayList<Integer> followers = followDAO.getFollowerIDs(followingId);
        System.out.println("getFollowerIDs for " + followingId + ": " + followers);

        // 5️ Test deleteFollow
//        boolean deleted = followDAO.deleteFollow(followerId, followingId);
//        System.out.println("deleteFollow: " + deleted);

        // 6️ Verify deletion
//        exists = followDAO.existFollow(followerId, followingId);
//        System.out.println("existFollow after deletion: " + exists);
    }
}
