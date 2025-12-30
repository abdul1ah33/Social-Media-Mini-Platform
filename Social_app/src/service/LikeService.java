package service;

import dao.LikeDAO;
import util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class LikeService {

    private final LikeDAO likeDAO = new LikeDAO();
    private final PostService postService = new PostService();

    public boolean likePost(int postId, int userId) {

        if (!postService.postExists(postId)) {
            return false;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            if (likeDAO.existLike(conn, postId, userId)) {
                conn.rollback();
                return false;
            }

            boolean inserted = likeDAO.addLike(conn, postId, userId);
            conn.commit();
            return inserted;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to like post", e);
        }
    }

    public boolean unlikePost(int postId, int userId) {

        if (!postService.postExists(postId)) {
            return false;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            if (!likeDAO.existLike(conn, postId, userId)) {
                conn.rollback();
                return false;
            }

            boolean removed = likeDAO.removeLike(conn, postId, userId);
            conn.commit();
            return removed;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to unlike post", e);
        }
    }


    public boolean hasUserLikedPost(int postId, int userId) {

        try (Connection conn = DBConnection.getConnection()) {
            return likeDAO.existLike(conn, postId, userId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check like existence", e);
        }
    }

    public int getLikesCount(int postId) {

        try (Connection conn = DBConnection.getConnection()) {
            return likeDAO.getLikesCountByPost(conn, postId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get likes count", e);
        }
    }

    public ArrayList<Integer> getUsersWhoLikedPost(int postId) {

        try (Connection conn = DBConnection.getConnection()) {
            return likeDAO.getUserIdsWhoLikedPost(conn, postId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch users who liked post", e);
        }
    }
}
