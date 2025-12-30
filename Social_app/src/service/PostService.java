package service;

import dao.PostDAO;
import dao.UserDAO;
import model.Post;
import util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class PostService {

    private final PostDAO postDAO;
    private final UserDAO userDAO;

    public PostService() {
        this.postDAO = new PostDAO();
        this.userDAO = new UserDAO();
    }


    public void createPost(Post post) {

        if (post.getText() == null || post.getText().isEmpty()) {
            throw new IllegalArgumentException("Post text cannot be empty");
        }

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            if (!userDAO.exist(conn, post.getUserID())) {
                throw new IllegalArgumentException("User does not exist");
            }

            boolean success = postDAO.add(post);
            if (!success) {
                throw new RuntimeException("Failed to create post");
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Database error while creating post", e);
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }


    public Post getPost(int postId) {
        if (postId <= 0) {
            throw new IllegalArgumentException("Invalid post ID");
        }

        try (Connection conn = DBConnection.getConnection()) {
            Post post = postDAO.getDetails(postId);
            if (post == null) {
                throw new IllegalArgumentException("Post not found with ID: " + postId);
            }
            return post;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while fetching post", e);
        }
    }


    public void updatePost(int postId, Post updatedPost, int requesterUserId) {
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            Post existing = postDAO.getDetails(postId);
            if (existing == null) {
                throw new IllegalArgumentException("Post not found");
            }

            if (existing.getUserID() != requesterUserId) {
                throw new IllegalArgumentException("User not authorized to update this post");
            }

            boolean success = postDAO.update(updatedPost, postId);
            if (!success) {
                throw new RuntimeException("No changes applied to the post");
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Database error while updating post", e);
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public void deletePost(int postId, int requesterUserId) {
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            Post existing = postDAO.getDetails(postId);
            if (existing == null) {
                throw new IllegalArgumentException("Post not found");
            }

            if (existing.getUserID() != requesterUserId) {
                throw new IllegalArgumentException("User not authorized to delete this post");
            }

            boolean success = postDAO.delete(postId);
            if (!success) {
                throw new RuntimeException("Failed to delete post");
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Database error while deleting post", e);
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
