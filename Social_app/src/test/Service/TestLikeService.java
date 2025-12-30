package test.Service;

import service.LikeService;

public class TestLikeService {

    public static void main(String[] args) {

        LikeService likeService = new LikeService();

        int postId = 10;   // make sure this exists
        int userId = 14;   // make sure this exists

        System.out.println("=== Like Post ===");
        boolean liked = likeService.likePost(postId, userId);
        System.out.println("Liked: " + liked);

        System.out.println("\n=== Like Same Post Again (should fail) ===");
        boolean likedAgain = likeService.likePost(postId, userId);
        System.out.println("Liked again: " + likedAgain);

        System.out.println("\n=== Has User Liked Post ===");
        System.out.println(likeService.hasUserLikedPost(postId, userId));

        System.out.println("\n=== Likes Count ===");
        System.out.println(likeService.getLikesCount(postId));

        System.out.println("\n=== Users Who Liked Post ===");
        System.out.println(likeService.getUsersWhoLikedPost(postId));

        System.out.println("\n=== Unlike Post ===");
        boolean unliked = likeService.unlikePost(postId, userId);
        System.out.println("Unliked: " + unliked);

        System.out.println("\n=== Unlike Again (should fail) ===");
        boolean unlikedAgain = likeService.unlikePost(postId, userId);
        System.out.println("Unliked again: " + unlikedAgain);

        System.out.println("\n=== Final Likes Count ===");
        System.out.println(likeService.getLikesCount(postId));
    }
}
