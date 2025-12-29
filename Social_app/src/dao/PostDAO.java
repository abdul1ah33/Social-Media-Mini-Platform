package dao;

import model.Post;
import model.User;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class PostDAO implements CRUDInterface<Post>{

    private Post mapRowToPost(ResultSet rs) throws SQLException {

        LocalDate localDate = rs.getDate("date").toLocalDate();

        Post post = new Post();
        post.setText(rs.getString("text"));
        post.setImagePath(rs.getString("imagePath"));
        post.setPostCategory(rs.getString("category"));
        post.setLikes(rs.getInt("likes"));
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
    public Post getDetails(int id) {
        String sql = "SELECT * FROM posts WHERE id = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRowToPost(rs);
            }

            System.out.println("No user with id " + id + " was found");
            return null;

        }
        catch (SQLException e){
//            e.printStackTrace();
            throw new RuntimeException("unable to get post");
        }
    }

    @Override
    public boolean update(Post post, int id) {
        StringBuilder sql = new StringBuilder("UPDATE users SET ");
        ArrayList<Object> values = new ArrayList<>();

        if (post.getText() != null) {
            sql.append("firstname=?, ");
            values.add(post.getText());
        }

        if (post.getImagePath() != null) {
            sql.append("lastname=?, ");
            values.add(post.getImagePath());
        }

        if (post.getPostCategory() != null) {
            sql.append("email=?, ");
            values.add(post.getPostCategory());
        }

        if (values.isEmpty()) {
            return false;
        }

        sql.setLength(sql.length() - 2); // remove ", "
        sql.append(" WHERE id = ?");
        values.add(id);

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }

            int numberOfRows = stmt.executeUpdate();
            return numberOfRows > 0;
        }

        catch(SQLException e){
            throw new RuntimeException("unable to update post");
        }
    }


    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM posts WHERE id = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int numberOfRows = stmt.executeUpdate();
            return numberOfRows > 0;
        }
        catch(SQLException e){
            throw new RuntimeException("unable to delete post");
        }
    }



//    int getPostsCountByUser(Connection conn, int userId){
//
//    }


//    public ArrayList<Post> getUserPosts(Connection conn ,int userID) {
//        ArrayList<Post> posts = new ArrayList<>();
//        return posts;
//    }

//    public boolean addLike(int postID) {
//
//    }

//    public boolean removeLike(int postID){
//
//    }
}