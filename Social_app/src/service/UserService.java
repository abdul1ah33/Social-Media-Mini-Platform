package service;

import dao.UserDAO;
import model.User;
import util.DBConnection;

import java.sql.SQLException;
import java.time.LocalDate;

import java.sql.Connection;

public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final PostService postService = new PostService();
    private final FollowService followService = new FollowService();
    private final LikeService likeService = new LikeService();


    // Sign Up
    public boolean signup(String userName, String password, String firstName, String lastName, String email, LocalDate birthDate) {

        // basic validation
        if (userName == null || userName.isEmpty() ||
                password == null || password.isEmpty() ||
                firstName == null || firstName.isEmpty() ||
                lastName == null || lastName.isEmpty() ||
                email == null || email.isEmpty() ||
                birthDate == null) {
            return false;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            if (userDAO.existUsername(conn, userName)) {
                conn.rollback();
                return false; // username taken
            }

            if (userDAO.existEmail(conn, email)) {
                conn.rollback();
                return false; // email taken
            }

            boolean inserted = userDAO.add(conn, new User(userName, password, firstName, lastName, email, birthDate));
            conn.commit();
            return inserted;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { throw new RuntimeException(ex); }
            }
            throw new RuntimeException("Failed to sign up user", e);

        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { throw new RuntimeException("Failed to close connection", e); }
            }
        }
    }


    // Login
    public User login(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            User user = userDAO.getUserByUsername(conn, username);
            if (user != null && user.getPassword().equals(password)) {
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Login failed due to database error", e);
        }
        return null;
    }

    public java.util.ArrayList<User> searchUsers(String query) {
        try (Connection conn = DBConnection.getConnection()) {
            return userDAO.searchUsers(conn, query);
        } catch (SQLException e) {
            throw new RuntimeException("Search failed", e);
        }
    }
    public boolean updateBio(User user, String newBio) {
        try (Connection conn = DBConnection.getConnection()) {
            user.setBio(newBio);
            return userDAO.update(conn, user, user.getID());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update bio", e);
        }
    }
    public boolean updateProfilePicture(User user, String newPicturePath) {
        try (Connection conn = DBConnection.getConnection()) {
            user.setProfilePicturePath(newPicturePath);
            return userDAO.update(conn, user, user.getID());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update profile picture", e);
        }
    }
}
