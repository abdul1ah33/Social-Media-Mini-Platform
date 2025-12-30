package ui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.Post;
import service.PostService;
import util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class HomeView {

    private final ScrollPane scrollPane;
    private final VBox feedContainer;
    private final PostService postService;

    public HomeView() {
        this.postService = new PostService();
        
        feedContainer = new VBox(20);
        feedContainer.setAlignment(Pos.TOP_CENTER);
        feedContainer.setPadding(new javafx.geometry.Insets(20));
        // feedContainer.setStyle("-fx-background-color: transparent;");

        scrollPane = new ScrollPane(feedContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("feed-scroll-pane");
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
    }

    public Parent getView() {
        return scrollPane;
    }

    public void refreshFeed() {
        feedContainer.getChildren().clear();
        ProgressIndicator pi = new ProgressIndicator();
        feedContainer.getChildren().add(pi);

        new Thread(() -> {
            try {
                int currentUserId = SessionManager.getInstance().getCurrentUser().getID();
                // Fetch feed
                ArrayList<Post> posts = postService.getFeedPosts(currentUserId);
                
                // If feed is empty (maybe new user follows no one), show their own posts or global?
                // For now, let's also fetch user's own posts if feed is empty or just show empty message
                if (posts.isEmpty()) {
                     // Fetch Global Recents if feed is empty
                     ArrayList<Post> recentPosts = postService.getRecentPosts(50);
                     posts.addAll(recentPosts);
                }

                Platform.runLater(() -> {
                    feedContainer.getChildren().clear();
                    if (posts.isEmpty()) {
                        Label emptyLabel = new Label("Welcome! Be the first to post something.");
                        emptyLabel.getStyleClass().add("subtitle-text");
                        feedContainer.getChildren().add(emptyLabel);
                    } else {
                        // Title for Feed
                        if (postService.getUserPostsCount(currentUserId) == 0 && posts.size() > 0) {
                             Label exploreLabel = new Label("Explore Recent Posts");
                             exploreLabel.getStyleClass().add("section-title");
                             feedContainer.getChildren().add(exploreLabel);
                        }
                        
                        for (Post p : posts) {
                            feedContainer.getChildren().add(new PostCard(p));
                        }
                    }
                });

            } catch (Exception e) {
                // If feed fails (e.g. admin user ID not in follows table), fall back to global posts
                try {
                    ArrayList<Post> recentPosts = postService.getRecentPosts(50);
                    Platform.runLater(() -> {
                         feedContainer.getChildren().clear();
                         Label exploreLabel = new Label("Explore Recent Posts");
                         exploreLabel.getStyleClass().add("section-title");
                         feedContainer.getChildren().add(exploreLabel);
                         
                         for (Post p : recentPosts) {
                             feedContainer.getChildren().add(new PostCard(p));
                         }
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        feedContainer.getChildren().clear();
                        Label errorLabel = new Label("Failed to load feed.");
                        errorLabel.getStyleClass().add("error-label");
                        feedContainer.getChildren().add(errorLabel);
                        ex.printStackTrace();
                    });
                }
            }
        }).start();
    }
}
