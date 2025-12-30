package util;

import model.User;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private boolean isAdmin;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.isAdmin = false; // Default to regular user
    }
    
    public void setCurrentUser(User user, boolean isAdmin) {
        this.currentUser = user;
        this.isAdmin = isAdmin;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
        isAdmin = false;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public boolean isAdmin() {
        return isAdmin;
    }
}
