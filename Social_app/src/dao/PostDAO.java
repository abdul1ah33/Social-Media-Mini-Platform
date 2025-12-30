package dao;

import model.Post;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PostDAO implements CRUDInterface<Post> {


    private Post mapRowToPost(ResultSet rs) throws SQLException {

        Post post = new Post();
        post.setPostID(rs.getInt("id"));
        post.setUserID(rs.getInt("user_id"));
        post.setText(rs.getString("text"));
        post.setImagePath(rs.getString("image_path"));
        post.setPostCategory(rs.getString("post_category"));

        Timestamp ts = rs.getTimestamp("post_creation_date");
        if (ts != null) {
            post.setPostCreationDate(ts.toLocalDateTime());
        }

        return post;
    }


    @Override
    public boolean add(Connection conn, Post post) {

        String sql = """
            INSERT INTO posts
            (user_id, text, image_path, post_category, post_creation_date)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, post.getUserID());
            stmt.setString(2, post.getText());
            stmt.setString(3, post.getImagePath());
            stmt.setString(4, post.getPostCategory());

            if (post.getPostCreationDate() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(post.getPostCreationDate()));
            } else {
                stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            }

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to add post", e);
        }
    }


    @Override
    public Post getDetails(Connection conn, int id) {

        String sql = "SELECT * FROM posts WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRowToPost(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get post with id " + id, e);
        }
    }


    @Override
    public boolean update(Connection conn, Post post, int id) {

        StringBuilder sql = new StringBuilder("UPDATE posts SET ");
        ArrayList<Object> values = new ArrayList<>();

        if (post.getText() != null) {
            sql.append("text = ?, ");
            values.add(post.getText());
        }

        if (post.getImagePath() != null) {
            sql.append("image_path = ?, ");
            values.add(post.getImagePath());
        }

        if (post.getPostCategory() != null) {
            sql.append("post_category = ?, ");
            values.add(post.getPostCategory());
        }

        if (values.isEmpty()) {
            return false;
        }

        sql.setLength(sql.length() - 2); // remove last ", "
        sql.append(" WHERE id = ?");
        values.add(id);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update post with id " + id, e);
        }
    }


    @Override
    public boolean delete(Connection conn, int id) {

        String sql = "DELETE FROM posts WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete post with id " + id, e);
        }
    }


    public ArrayList<Post> getPostsByUser(Connection conn, int userId) {

        String sql = """
            SELECT *
            FROM posts
            WHERE user_id = ?
            ORDER BY post_creation_date DESC
        """;

        ArrayList<Post> posts = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                posts.add(mapRowToPost(rs));
            }

            return posts;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch posts for user " + userId, e);
        }
    }


    public int getPostsCountByUser(Connection conn, int userId) {

        String sql = "SELECT COUNT(*) FROM posts WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to count posts for user " + userId, e);
        }
    }
}
