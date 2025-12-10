import java.io.PipedOutputStream;
import java.util.ArrayList;

public class User extends Account {
    private String bio;
    private ArrayList<User> Followers;
    private ArrayList<User> Followings;
    private int followersCount;
    private int followingsCount;
//  it will be uncommented when we create the Post class
//  private ArrayList<Post> posts;

    // Getters
    public ArrayList<User> getFollowers() { return Followers; }
    public ArrayList<User> getFollowings() { return Followings; }
    public int getFollowersCount() { return followersCount; }
    public int getFollowingsCount() { return followingsCount; }
    public String getBio() { return bio; }

    // Setters
    public void setFollowers(ArrayList<User> followers) { this.Followers = followers; }
    public void setFollowings(ArrayList<User> followings) { this.Followings = followings; }
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }
    public void setFollowingsCount(int followingsCount) { this.followingsCount = followingsCount; }
    public void setBio(String bio) { this.bio = bio; }

    // followers and following handling functions
    public void addFollower(User user) {
        Followers.add(user);
        followersCount++;
    }
    public void removeFollower(User user) {
        Followers.remove(user);
        followersCount--;
    }
    public void addFollowing(User user) {
        Followings.add(user);
        followingsCount++;
    }
    public void removeFollowing(User user) {
        Followings.remove(user);
        followingsCount--;
    }

    //still didn't decide how to handle these functions
    public void makePost(String postTest, String imagePath){}

    public void addBio(String biotext) {}

//  it will be uncommented when we create the Post class
//    public void deletePost(Post post){}
}
