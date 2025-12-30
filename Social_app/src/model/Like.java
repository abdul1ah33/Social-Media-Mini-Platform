package model;

import java.time.LocalDateTime;

public class Like {

    private int id;
    private int postId;
    private int userId;

    public Like() {}

    // Constructor
    public Like(int postId, int userId) {
        this.postId = postId;
        this.userId = userId;
    }

    // Getters
    public int getId() { return id; }
    public int getPostId() { return postId; }
    public int getUserId() { return userId; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setPostId(int postId) { this.postId = postId; }
    public void setUserId(int userId) { this.userId = userId; }
}
