package test.DAO;

import dao.PostDAO;
import model.Post;
import util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TestPostDAO {

    public static void main(String[] args) {
        PostDAO postDAO = new PostDAO();

        int userId = 14; // must exist in users table
        int postId = 1;

        try (Connection conn = DBConnection.getConnection()) {

            System.out.println("=== ADD POST TEST ===");
            Post newPost = new Post();
            newPost.setUserID(userId);
            newPost.setText("Test post text");
            newPost.setImagePath("test.jpg");
            newPost.setPostCategory("General");
            newPost.setPostCreationDate(LocalDateTime.now());

            boolean added = postDAO.add(conn, newPost);
            System.out.println("Post added: " + added);

            System.out.println("\n=== GET POSTS BY USER TEST ===");
            ArrayList<Post> posts = postDAO.getPostsByUser(conn, userId);
            for (Post p : posts) {
                System.out.println("Post ID: " + p.getPostID() + ", Text: " + p.getText());
                postId = p.getPostID(); // get last postId for next tests
            }

            System.out.println("\n=== GET POSTS COUNT BY USER TEST ===");
            int count = postDAO.getPostsCountByUser(conn, userId);
            System.out.println("Posts count for user " + userId + ": " + count);

            System.out.println("\n=== GET POST DETAILS TEST ===");
            Post fetched = postDAO.getDetails(conn, postId);
            System.out.println("Fetched Post ID: " + fetched.getPostID() + ", Text: " + fetched.getText());

            System.out.println("\n=== UPDATE POST TEST ===");
            fetched.setText("Updated test post");
            boolean updated = postDAO.update(conn, fetched, postId);
            System.out.println("Post updated: " + updated);

            System.out.println("\n=== DELETE POST TEST ===");
            boolean deleted = postDAO.delete(conn, postId);
            System.out.println("Post deleted: " + deleted);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
