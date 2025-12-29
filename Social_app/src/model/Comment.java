package model;

import java.time.LocalDateTime;

public class Comment {

    private static int commentCounter = 0;
    private String content;
    private User user;
    private int postID;
    private int commentID;
    private LocalDateTime commentTime;

    public Comment(String content, User user, int postID) {
        this.content = content;
        this.user = user;
        this.postID = postID;
        this.commentID = commentCounter++;
        this.commentTime = LocalDateTime.now();
    }

    public String getContent() {return content;}
    public User getUser() {return user;}
    public int getPostID() {return postID;}
    public int getCommentID() {return commentID;}
    public LocalDateTime getCommentTime() {return commentTime;}
    public int getCommentCounter() {return commentCounter;}

    public void setContent(String content) {this.content = content;}
    public void setUser(User user) {this.user = user;}
    public void setPostID(int postID) {this.postID = postID;}
    public void setCommentTime(){this.commentTime = LocalDateTime.now();}
    public void setCommentID() {this.commentID = commentCounter++;}

    public void deleteComment(){}
}
