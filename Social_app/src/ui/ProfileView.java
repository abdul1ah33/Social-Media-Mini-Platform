package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Post;
import model.User;
import service.FollowService;
import service.PostService;
import util.SessionManager;

import java.util.ArrayList;

public class ProfileView {

    private final User user; // The user whose profile we are viewing
    private final ScrollPane scrollPane;
    private final VBox rootContainer;
    private final VBox postsContainer;
    
    private final PostService postService;
    private final FollowService followService;

    // Header Elements to update
    private Text followersCountText;
    private Text followingCountText;
    private Text postsCountText;

    public ProfileView(User user) {
        this.user = user;
        this.postService = new PostService();
        this.followService = new FollowService();

        rootContainer = new VBox();
        rootContainer.getStyleClass().add("main-container");
        
        // Header
        VBox header = createHeader();
        
        // Posts container
        postsContainer = new VBox(15);
        postsContainer.setPadding(new Insets(20));
        postsContainer.setAlignment(Pos.TOP_CENTER);
        
        rootContainer.getChildren().addAll(header, postsContainer);

        scrollPane = new ScrollPane(rootContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
    }

    private VBox createHeader() {
        VBox header = new VBox(20);
        header.getStyleClass().add("profile-header");
        header.setAlignment(Pos.CENTER);

        // Avatar with Profile Picture
        javafx.scene.layout.VBox avatarBox = new javafx.scene.layout.VBox(8);
        avatarBox.setAlignment(Pos.CENTER);
        
        javafx.scene.image.ImageView profilePic = new javafx.scene.image.ImageView();
        profilePic.setFitWidth(100);
        profilePic.setFitHeight(100);
        profilePic.setPreserveRatio(true);
        
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(50, 50, 50);
        profilePic.setClip(clip);
        
        boolean hasCustomPic = false;
        if (user.getProfilePicturePath() != null && !user.getProfilePicturePath().isEmpty()) {
            try {
                java.io.File imageFile = new java.io.File(user.getProfilePicturePath());
                if (imageFile.exists()) {
                    javafx.scene.image.Image image = new javafx.scene.image.Image(imageFile.toURI().toString());
                    profilePic.setImage(image);
                    hasCustomPic = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (hasCustomPic) {
            profilePic.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(255,255,255,0.4), 15, 0, 0, 3);");
            avatarBox.getChildren().add(profilePic);
        } else {
            Circle avatar = new Circle(50, Color.WHITE);
            avatar.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(255,255,255,0.4), 15, 0, 0, 3);");
            avatarBox.getChildren().add(avatar);
        }
        
        // Add edit button if viewing own profile
        if (SessionManager.getInstance().getCurrentUser().getID() == user.getID()) {
            javafx.scene.control.Button editPicBtn = new javafx.scene.control.Button("📷 Edit");
            editPicBtn.getStyleClass().add("link-button");
            editPicBtn.setStyle("-fx-text-fill: white;");
            editPicBtn.setOnAction(e -> handleEditProfilePicture());
            avatarBox.getChildren().add(editPicBtn);
        }

        // Info
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER);
        
        Text name = new Text(user.getUserName()); // Or First Last name
        name.getStyleClass().add("profile-name");


        Text bio = new Text(user.getBio() != null ? user.getBio() : "No bio yet (Click to edit)");
        bio.getStyleClass().add("profile-bio");
        
        // Allow editing if current user
        if (SessionManager.getInstance().getCurrentUser().getID() == user.getID()) {
             bio.setCursor(javafx.scene.Cursor.HAND);
             bio.setOnMouseClicked(e -> handleEditBio(bio));
        }
        
        infoBox.getChildren().addAll(name, bio);



        // Stats
        HBox statsBox = new HBox(40);
        statsBox.setAlignment(Pos.CENTER);
        
        postsCountText = new Text("0");
        postsCountText.getStyleClass().add("stat-label");
        VBox postsStat = createStatItem(postsCountText, "Posts", null);

        followersCountText = new Text("0");
        followersCountText.getStyleClass().add("stat-label");
        VBox followersStat = createStatItem(followersCountText, "Followers", e -> showFollowers());

        followingCountText = new Text("0");
        followingCountText.getStyleClass().add("stat-label");
        VBox followingStat = createStatItem(followingCountText, "Following", e -> showFollowing());

        statsBox.getChildren().addAll(postsStat, followersStat, followingStat);


        header.getChildren().addAll(avatarBox, infoBox, statsBox);
        return header;
    }

    private void handleEditProfilePicture() {
        new EditProfilePictureModal(
            (javafx.stage.Stage) rootContainer.getScene().getWindow(),
            user,
            () -> {
                // Refresh header
                VBox newHeader = createHeader();
                rootContainer.getChildren().set(0, newHeader);
            }
        ).show();
    }

    private VBox createStatItem(Text countText, String label, javafx.event.EventHandler<javafx.scene.input.MouseEvent> onClick) {
        VBox box = new VBox(2);
        box.setAlignment(Pos.CENTER);
        
        if (onClick != null) {
            box.setCursor(javafx.scene.Cursor.HAND);
            box.setOnMouseClicked(onClick);
        }
        
        Text labelText = new Text(label);
        labelText.getStyleClass().add("stat-desc");
        box.getChildren().addAll(countText, labelText);
        return box;
    }

    private void showFollowers() {
        new Thread(() -> {
            try {
                ArrayList<User> followers = followService.getFollowers(user.getID());
                Platform.runLater(() -> new UserListModal(((Stage) rootContainer.getScene().getWindow()), "Followers", followers, () -> {
                    // When a follow action happens in the modal, refresh stats
                    refreshPosts();
                }).show());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showFollowing() {
         new Thread(() -> {
            try {
                ArrayList<User> following = followService.getFollowings(user.getID());
                Platform.runLater(() -> new UserListModal(((Stage) rootContainer.getScene().getWindow()), "Following", following, () -> {
                    // When a follow action happens in the modal, refresh stats
                    refreshPosts(); 
                }).show());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleEditBio(Text bioText) {
        new EditBioModal(
            (javafx.stage.Stage) rootContainer.getScene().getWindow(),
            user,
            () -> bioText.setText(user.getBio())
        ).show();
    }

    public Parent getView() {
        return scrollPane;
    }

    public void refreshPosts() {
        postsContainer.getChildren().clear();
        ProgressIndicator pi = new ProgressIndicator();
        postsContainer.getChildren().add(pi);

        new Thread(() -> {
            try {
                // Fetch Stats
                int pCount = postService.getUserPostsCount(user.getID());
                int fCount = followService.getFollowersCount(user.getID());
                int fingCount = followService.getFollowingsCount(user.getID());

                // Fetch Posts
                ArrayList<Post> posts = postService.getUserPosts(user.getID());

                Platform.runLater(() -> {
                    // Update Stats
                    postsCountText.setText(String.valueOf(pCount));
                    followersCountText.setText(String.valueOf(fCount));
                    followingCountText.setText(String.valueOf(fingCount));

                    // Update Posts
                    postsContainer.getChildren().clear();
                    if (posts.isEmpty()) {
                        Label emptyLabel = new Label("No posts available.");
                        emptyLabel.getStyleClass().add("subtitle-text");
                        postsContainer.getChildren().add(emptyLabel);
                    } else {
                        // Reverse order to show newest first if not already sorted
                        for (int i = posts.size() - 1; i >= 0; i--) {
                            postsContainer.getChildren().add(new PostCard(posts.get(i)));
                        }
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    postsContainer.getChildren().clear();
                    e.printStackTrace();
                });
            }
        }).start();
    }
}
