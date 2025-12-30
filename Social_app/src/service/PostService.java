package service;

import dao.FollowDAO;
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

            boolean success = postDAO.add(conn, post);
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
            Post post = postDAO.getDetails(conn, postId);
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

            Post existing = postDAO.getDetails(conn, postId);
            if (existing == null) {
                throw new IllegalArgumentException("Post not found");
            }

            if (existing.getUserID() != requesterUserId) {
                throw new IllegalArgumentException("User not authorized to update this post");
            }

            boolean success = postDAO.update(conn, updatedPost, postId);
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

            Post existing = postDAO.getDetails(conn, postId);
            if (existing == null) {
                throw new IllegalArgumentException("Post not found");
            }

            if (existing.getUserID() != requesterUserId) {
                throw new IllegalArgumentException("User not authorized to delete this post");
            }

            boolean success = postDAO.delete(conn, postId);
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

    public ArrayList<Post> getUserPosts(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            if (!userDAO.exist(conn, userId)) {
                throw new IllegalArgumentException("User does not exist");
            }
            return postDAO.getPostsByUser(conn, userId);
        } catch (SQLException e) {
            throw new RuntimeException("Database error while fetching posts", e);
        }
    }

    public int getUserPostsCount(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            if (!userDAO.exist(conn, userId)) {
                throw new IllegalArgumentException("User does not exist");
            }
            return postDAO.getPostsCountByUser(conn, userId);
        } catch (SQLException e) {
            throw new RuntimeException("Database error while counting posts", e);
        }
    }


    /*
     this is the function we will use to get the feed of a certain user
     it sorts the post in descending order of time "recent ones first"
     --------------------------------------------------------------------
     FollowDAO is used directly to get followings.
     postDAO.getPostsByUser fetches posts per following.
     --------------------------------------------------------------------
     Empty feed if the user follows nobody.
     */
    public ArrayList<Post> getFeedPosts(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            if (!userDAO.exist(conn, userId)) {
                throw new IllegalArgumentException("User does not exist");
            }

            ArrayList<Integer> followingIds = new FollowDAO().getFollowingIDs(conn, userId);

            ArrayList<Post> feedPosts = new ArrayList<>();

            for (int i = 0; i < followingIds.size(); i++) {
                int followingId = followingIds.get(i);
                ArrayList<Post> posts = postDAO.getPostsByUser(conn, followingId);
                for (int j = 0; j < posts.size(); j++) {
                    feedPosts.add(posts.get(j));
                }
            }

            for (int i = 0; i < feedPosts.size() - 1; i++) {
                for (int j = i + 1; j < feedPosts.size(); j++) {
                    if (feedPosts.get(i).getPostCreationDate().isBefore(feedPosts.get(j).getPostCreationDate())) {
                        Post temp = feedPosts.get(i);
                        feedPosts.set(i, feedPosts.get(j));
                        feedPosts.set(j, temp);
                    }
                }
            }

            return feedPosts;

        } catch (SQLException e) {
            throw new RuntimeException("Database error while preparing feed for user " + userId, e);
        }
    }

    public boolean postExists(int postId) {
        try (Connection conn = DBConnection.getConnection()) {
            Post post = postDAO.getDetails(conn, postId);
            return post != null;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while checking post existence", e);
        }
    }

    public ArrayList<Post> getRecentPosts(int limit) {
        try (Connection conn = DBConnection.getConnection()) {
            return postDAO.getRecentPosts(conn, limit);
        } catch (SQLException e) {
            throw new RuntimeException("Database error while fetching recent posts", e);
        }
    }
}
