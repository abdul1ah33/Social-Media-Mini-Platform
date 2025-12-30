package model;

import java.time.LocalDate;
import java.util.ArrayList;

public class User extends Account {

    // Attributes
    private String bio;
    private ArrayList<User> Followers;
    private ArrayList<User> Followings;
    private int followersCount;
    private int followingsCount;
    private ArrayList<Post> posts;


    public User() {
        Followers = new ArrayList<>();
        Followings = new ArrayList<>();
        posts = new ArrayList<>();
    }

    public User(String userName, String password, String firstName, String lastName, String email, LocalDate birthDate) {
        this();
        this.setUserName(userName);
        this.setPassword(password);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setBirthDate(birthDate);
    }


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
    public void addBio(String biotext) {}

    public void makePost(String postTest, String imagePath){}
    public void deletePost(Post post){}
}
