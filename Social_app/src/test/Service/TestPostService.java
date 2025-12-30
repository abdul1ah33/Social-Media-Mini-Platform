package test.Service;

import service.PostService;
import model.Post;

import java.util.ArrayList;

public class TestPostService {

    public static void main(String[] args) {
        PostService postService = new PostService();

        int userId = 14;
        int postId = 0;

        System.out.println("=== ADD POST VIA SERVICE ===");
        Post post = new Post();
        post.setUserID(userId);
        post.setText("Service test post");
        post.setImagePath("service.jpg");
        post.setPostCategory("General");
        post.setPostCreationDate(null);

        postService.createPost(post);

        System.out.println("\n=== GET POSTS BY USER VIA SERVICE ===");
        ArrayList<Post> posts = postService.getUserPosts(userId);
        for (Post p : posts) {
            System.out.println("Post ID: " + p.getPostID() + ", Text: " + p.getText());
            postId = p.getPostID();
        }

        System.out.println("\n=== CHECK POST EXISTS VIA SERVICE ===");
        boolean exists = postService.postExists(postId);
        System.out.println("Post exists: " + exists);

        System.out.println("\n=== GET FEED POSTS (empty for now) ===");
        ArrayList<Post> feed = postService.getFeedPosts(userId);
        System.out.println("Feed posts size: " + feed.size());
    }
}
