package service;

import dao.CommentDAO;
import dao.PostDAO;
import dao.UserDAO;
import model.Comment;
import util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class CommentService {
    private final CommentDAO commentDAO;
    private final PostDAO postDAO;
    private final UserDAO userDAO;

    public CommentService() {
        this.commentDAO = new CommentDAO();
        this.postDAO = new PostDAO();
        this.userDAO = new UserDAO();
    }

    public void CreateComment(Comment comment) {
        if(comment.getContent()==null || comment.getContent().isEmpty()) {
            throw new IllegalArgumentException("Comment must have text");
        }
        Connection conn = null;

        try{
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            if(!postDAO.exist(conn, comment.getPostID())){
                throw new IllegalArgumentException("Post does not exist");
            }

            if(!userDAO.exist(conn, comment.getUser().getID())){
                throw new IllegalArgumentException("User does not exist");
            }

            boolean success = commentDAO.add(conn, comment);
            if(!success){
                throw new IllegalArgumentException("Failed to add comment");
            }

            conn.commit();
        }
        catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Database error while creating post", e);
        }
        finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean commentExists(int postId) {
        try (Connection conn = DBConnection.getConnection()) {
            Comment comment = commentDAO.getDetails(conn, postId);
            return comment != null;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while checking comment existence", e);
        }
    }
    public java.util.ArrayList<Comment> getCommentsByPost(int postId) {
        return commentDAO.getCommentsByPost(postId);
    }
    public int getCommentCountByPost(int postId) {
        return commentDAO.getCommentCountByPost(postId);
    }
    
    public void deleteComment(int commentId) {
        try (Connection conn = DBConnection.getConnection()) {
            commentDAO.delete(conn, commentId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete comment", e);
        }
    }
}
