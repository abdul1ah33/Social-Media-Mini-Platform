package dao;

import model.Like;
import java.sql.*;
import java.util.ArrayList;

public class LikeDAO {

    public boolean addLike(Connection conn, int postId, int userId) throws SQLException {
        String sql = "INSERT INTO post_likes (post_id, user_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean removeLike(Connection conn, int postId, int userId) throws SQLException {
        String sql = "DELETE FROM post_likes WHERE post_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean existLike(Connection conn, int postId, int userId) throws SQLException {
        String sql = "SELECT 1 FROM post_likes WHERE post_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public int getLikesCountByPost(Connection conn, int postId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM post_likes WHERE post_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
            return 0;
        }
    }

    public ArrayList<Integer> getUserIdsWhoLikedPost(Connection conn, int postId) {

        String sql = "SELECT user_id FROM post_likes WHERE post_id = ?";
        ArrayList<Integer> userIds = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                userIds.add(rs.getInt("user_id"));
            }

            return userIds;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get user IDs who liked post " + postId, e);
        }
    }


    public ArrayList<Like> getLikesByPost(Connection conn, int postId) throws SQLException {
        String sql = "SELECT * FROM post_likes WHERE post_id = ?";
        ArrayList<Like> likes = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Like like = new Like();
                like.setId(rs.getInt("id"));
                like.setPostId(rs.getInt("post_id"));
                like.setUserId(rs.getInt("user_id"));
                likes.add(like);
            }
            return likes;
        }
    }
}
