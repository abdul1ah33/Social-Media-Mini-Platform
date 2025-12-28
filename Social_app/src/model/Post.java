package model;

import javax.xml.stream.events.Comment;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class Post {
    private static int postCounter = 0;
    // attributes
    private String text;
    private String imagePath;
    private int likes;
    private User user;
    private LocalDate postCreationDate;
    private String postCategory;
    private ArrayList<Comment> comments;
    private int postID;

    // Constructor
    public Post() {}
    public Post(String text, String imagePath, int likes, User user, String postCategory) {
        this.text = text;
        this.imagePath = imagePath;
        this.likes = likes;
        this.user = user;
        this.postCategory = postCategory;
        this.postCreationDate = LocalDate.now();
        this.postID = postCounter++;
    }

    // Getters
    public String getText() {return text;}
    public String getImagePath() {return imagePath;}
    public int getLikes() {return likes;}
    public User getUser() {return user;}
    public LocalDate getPostCreationDate() {return postCreationDate;}
    public String getPostCategory() {return postCategory;}
    public int getPostID() {return postID;}
    public int getPostCounter() {return postCounter;}

    // Setters
    public void setText(String text) {this.text = text;}
    public void setImagePath(String imagePath) {this.imagePath = imagePath;}
    public void setLikes(int likes) {this.likes = likes;}
    public void setUser(User user) {this.user = user;}
    public void setPostCategory(String postCategory) {this.postCategory = postCategory;}
    public void setPostID() {this.postID = postCounter++;}
    public void setPostCreationDate(LocalDate date){this.postCreationDate = date;}

    // Methods
    public void addComment(String commentContent, User user) {}

    public void removeComment(Comment comment) {}

    public void addLike() { this.likes++; }

    public void removeLike() { this.likes--; }

    public void deletePost() {}
}
