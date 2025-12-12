import javax.xml.stream.events.Comment;
import java.util.ArrayList;
import java.util.Date;

public class Post {
    private String text;
    private String imagePath;
    private int likes;
    private User user;
    private final Date postCreationDate;
    private String postCategory;
    //waiting till Comment class is created
//    private ArrayList<Comment> comments;

    public Post(String text, String imagePath, int likes, User user, String postCategory) {
        this.text = text;
        this.imagePath = imagePath;
        this.likes = likes;
        this.user = user;
        this.postCategory = postCategory;
        this.postCreationDate = new Date();
    }

    public String getText() {return text;}
    public void setText(String text) {this.text = text;}

    public String getImagePath() {return imagePath;}
    public void setImagePath(String imagePath) {this.imagePath = imagePath;}

    public int getLikes() {return likes;}
    public void setLikes(int likes) {this.likes = likes;}

    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}

    public Date getPostCreationDate() {return postCreationDate;}

    public String getPostCategory() {return postCategory;}
    public void setPostCategory(String postCategory) {this.postCategory = postCategory;}

    public void addComment(String commentContent, User user) {}

    public void removeComment(Comment comment) {}

    public void addLike() { this.likes++; }

    public void removeLike() { this.likes--; }

    public void deletePost() {}
}
