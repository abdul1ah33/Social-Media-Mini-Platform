package dao;

import model.Comment;
import model.User;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CommentDAO implements CRUDInterface<Comment> {

    private Comment mapRowToComment(ResultSet rs) throws SQLException {
        try {
            User user = new User();
            user.setID(rs.getInt("user_id"));
            user.setUserName(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setFirstName(rs.getString("firstname"));
            user.setLastName(rs.getString("lastname"));
            user.setPassword(rs.getString("password"));

            Date birthDate = rs.getDate("birthdate");
            if (birthDate != null) {
                user.setBirthDate(birthDate.toLocalDate());
            }

            //bio might be null
            user.setBio(rs.getString("bio"));

            String content = rs.getString("content");
            int postId = rs.getInt("post_id");
            int commentId = rs.getInt("id");

            Timestamp createdAt = rs.getTimestamp("created_at");
            LocalDateTime commentTime = (createdAt != null) ?
                    createdAt.toLocalDateTime() : LocalDateTime.now();

            Comment comment = new Comment(content, user, postId);
            comment.setCommentIDDirectly(commentId);
            comment.setCommentTimeDirectly(commentTime);

            return comment;

        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Error mapping result set to Comment", e);
        }
    }

    @Override
    public boolean add(Comment comment) {
        String sql = "INSERT INTO comments (post_id, user_id, content, created_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (comment.getUser() == null || comment.getUser().getID() <= 0) {
                throw new IllegalArgumentException("Comment must have a valid user with ID");
            }

            stmt.setInt(1, comment.getPostID());
            stmt.setInt(2, comment.getUser().getID());
            stmt.setString(3, comment.getContent());

            LocalDateTime commentTime = comment.getCommentTime();
            if (commentTime == null) {
                commentTime = LocalDateTime.now();
            }
            stmt.setTimestamp(4, Timestamp.valueOf(commentTime));

            int rowsAffected = stmt.executeUpdate();

            // Get generated ID
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        comment.setCommentIDDirectly(generatedKeys.getInt(1));
                    }
                }
            }

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add comment", e);
        }
    }

    @Override
    public Comment getDetails(int id) {
        String sql = "SELECT c.*, u.* FROM comments c JOIN users u ON c.user_id = u.id WHERE c.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRowToComment(rs);
            }

            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get comment with id " + id, e);
        }
    }

    @Override
    public boolean update(Comment comment, int id) {
        String sql = "UPDATE comments SET content = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, comment.getContent());
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update comment with id " + id, e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM comments WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete comment with id " + id, e);
        }
    }

    public ArrayList<Comment> getCommentsByPost(int postId) {
        String sql = "SELECT c.*, u.* FROM comments c JOIN users u ON c.user_id = u.id WHERE c.post_id = ? ORDER BY c.created_at ASC";

        ArrayList<Comment> comments = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                comments.add(mapRowToComment(rs));
            }

            return comments;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch comments for post " + postId, e);
        }
    }

    public ArrayList<Comment> getCommentsByUser(int userId) {
        String sql = "SELECT c.*, u.* FROM comments c JOIN users u ON c.user_id = u.id WHERE c.user_id = ? ORDER BY c.created_at DESC";

        ArrayList<Comment> comments = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                comments.add(mapRowToComment(rs));
            }

            return comments;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch comments for user " + userId, e);
        }
    }

    public int getCommentCountByPost(int postId) {
        String sql = "SELECT COUNT(*) FROM comments WHERE post_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to count comments for post " + postId, e);
        }
    }

    public boolean deleteCommentsByPost(int postId) {
        String sql = "DELETE FROM comments WHERE post_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete comments for post " + postId, e);
        }
    }
}