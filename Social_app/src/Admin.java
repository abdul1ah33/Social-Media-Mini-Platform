import java.io.PipedOutputStream;

public class Admin extends Account {
    private int adminID;

    public int getAdminID() { return adminID; }

    public void deleteUser(User user) {}

    public void deletePost(User user, Post post) {}
}
