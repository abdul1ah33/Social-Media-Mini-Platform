package test.DAO;

import dao.CommentDAO;
import dao.PostDAO;
import model.Comment;
import model.Post;
import model.User;
import util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TestCommentDAO {

    public static void main(String[] args) {
        CommentDAO commentDAO = new CommentDAO();
        PostDAO postDAO = new PostDAO();

        int userId = 14; // must exist in users table
        int postId = 1;
        int commentId = 1;

        try (Connection conn = DBConnection.getConnection()) {

            System.out.println("=== CREATE TEST POST ===");
            Post newPost = new Post();
            newPost.setUserID(userId);
            newPost.setText("Test post for comments");
            newPost.setImagePath("test.jpg");
            newPost.setPostCategory("General");
            newPost.setPostCreationDate(LocalDateTime.now());

            boolean postAdded = postDAO.add(conn, newPost);
            System.out.println("Test post created: " + postAdded);

            // Get the post ID that was just created
            ArrayList<Post> posts = postDAO.getPostsByUser(conn, userId);
            if (!posts.isEmpty()) {
                postId = posts.get(0).getPostID();
            }

            System.out.println("\n=== ADD COMMENT TEST ===");
            User testUser = new User();
            testUser.setID(userId);

            Comment newComment = new Comment("Test comment text", testUser, postId);

            boolean added = commentDAO.add(conn, newComment);
            System.out.println("Comment added: " + added);

            System.out.println("\n=== GET COMMENTS BY POST TEST ===");
            ArrayList<Comment> comments = commentDAO.getCommentsByPost(postId);
            for (Comment c : comments) {
                System.out.println("Comment ID: " + c.getCommentID() + ", Content: " + c.getContent());
                commentId = c.getCommentID(); // get last commentId for next tests
            }

            System.out.println("\n=== GET COMMENT COUNT BY POST TEST ===");
            int count = commentDAO.getCommentCountByPost(postId);
            System.out.println("Comments count for post " + postId + ": " + count);

            System.out.println("\n=== GET COMMENT DETAILS TEST ===");
            Comment fetched = commentDAO.getDetails(conn, commentId);
            System.out.println("Fetched Comment ID: " + fetched.getCommentID() + ", Content: " + fetched.getContent());

            System.out.println("\n=== UPDATE COMMENT TEST ===");
            fetched.setContent("Updated test comment");
            boolean updated = commentDAO.update(conn, fetched, commentId);
            System.out.println("Comment updated: " + updated);

            System.out.println("\n=== DELETE COMMENT TEST ===");
            boolean deleted = commentDAO.delete(conn, commentId);
            System.out.println("Comment deleted: " + deleted);

            System.out.println("\n=== DELETE TEST POST ===");
            boolean postDeleted = postDAO.delete(conn, postId);
            System.out.println("Test post deleted: " + postDeleted);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}