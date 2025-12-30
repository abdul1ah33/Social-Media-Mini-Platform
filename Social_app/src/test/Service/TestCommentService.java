package test.Service;

import model.User;
import service.CommentService;
import model.Comment;
import model.Post;
import util.DBConnection;
import dao.PostDAO;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TestCommentService {
    public static void main(String[] args) {
        CommentService commentService = new CommentService();

        int userId = 14;
        int postId = 0;
        int commentId = 0;

        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("=== CREATE TEST POST ===");
            PostDAO postDAO = new PostDAO();
            Post newPost = new Post();
            newPost.setUserID(userId);
            newPost.setText("Test post for comments");
            newPost.setImagePath("test.jpg");
            newPost.setPostCategory("General");
            newPost.setPostCreationDate(LocalDateTime.now());

            boolean postAdded = postDAO.add(conn, newPost);
            System.out.println("Test post created: " + postAdded);

            ArrayList<Post> posts = postDAO.getPostsByUser(conn, userId);
            if (!posts.isEmpty()) {
                postId = posts.get(0).getPostID();
            }

            System.out.println("\n=== ADD COMMENT VIA SERVICE ===");
            Comment comment = new Comment();
            User user = new User();
            user.setID(userId);
            comment.setPostID(postId);
            comment.setContent("Service test comment");
            comment.setUser(user);
            comment.setCommentTime();

            commentService.CreateComment(comment);
            commentId = comment.getCommentID();
            System.out.println("Comment created, ID: " + commentId);

            System.out.println("\n=== CHECK COMMENT EXISTS VIA SERVICE ===");
            boolean exists = commentService.commentExists(commentId);
            System.out.println("Comment exists: " + exists);

            System.out.println("\n=== DELETE TEST POST ===");
            boolean postDeleted = postDAO.delete(conn, postId);
            System.out.println("Test post deleted: " + postDeleted);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
