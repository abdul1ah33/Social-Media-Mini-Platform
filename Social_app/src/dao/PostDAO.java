package dao;

import model.Post;
import model.User;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;

public class PostDAO implements CRUDInterface<Post>{

    private Post mapRowToPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setText(rs.getString("text"));
        post.setImagePath(rs.getString("imagePath"));
        post.setPostCategory(rs.getString("category"));
        post.setLikes(rs.getInt("likes"));
        LocalDate localDate = rs.getDate("date").toLocalDate();
        post.setPostCreationDate(localDate);

        return post;
    }

    private String generatePlaceholders(int size) {
        if (size <= 0) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append("?");
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public boolean add(Post post) {
        String sql = "INSERT INTO posts (text, imagepath, likes, user, creationDate, category) VALUES (?, ?, ?, ?, ?, ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            java.sql.Date sqlDate = java.sql.Date.valueOf(post.getPostCreationDate());

            stmt.setString(1, post.getText());
            stmt.setString(2, post.getImagePath());
            stmt.setInt(3, post.getLikes());
            stmt.setInt(4, post.getUserID());
            stmt.setDate(5, sqlDate);
            stmt.setString(6, post.getPostCategory());

            int numberOfRows = stmt.executeUpdate();
            return numberOfRows > 0;
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(Post entity, int id) {
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public Post getDetails(int id) {
        return null;
    }
}
