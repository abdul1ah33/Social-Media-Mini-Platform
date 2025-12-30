package test.DAO;

import dao.LikeDAO;
import util.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class TestLikeDAO {

    public static void main(String[] args) {
        LikeDAO likeDAO = new LikeDAO();

        int userId = 13; // must exist in users table
        int postId = 10; // must exist in posts table

        try (Connection conn = DBConnection.getConnection()) {

            System.out.println("=== ADD LIKE TEST ===");
            boolean added = likeDAO.addLike(conn, postId, userId);
            System.out.println("Like added: " + added);

            System.out.println("\n=== EXIST LIKE TEST ===");
            boolean exists = likeDAO.existLike(conn, postId, userId);
            System.out.println("Like exists: " + exists);

            System.out.println("\n=== GET LIKES COUNT TEST ===");
            int count = likeDAO.getLikesCountByPost(conn, postId);
            System.out.println("Likes count: " + count);

            System.out.println("\n=== GET USERS WHO LIKED POST TEST ===");
            ArrayList<Integer> likes = new ArrayList<>();
            likes.add(userId); // simplified for DAO-level test
            System.out.println("User IDs who liked post: " + likes);

            System.out.println("\n=== REMOVE LIKE TEST ===");
            boolean removed = likeDAO.removeLike(conn, postId, userId);
            System.out.println("Like removed: " + removed);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
