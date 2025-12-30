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
                // Fetch followed posts
                ArrayList<Post> followedPosts = postService.getFeedPosts(currentUserId);
                
                // Fetch all recent posts for exploration
                ArrayList<Post> recentPosts = postService.getRecentPosts(50);
                
                // Filter out followed posts from recent posts to avoid duplicates
                ArrayList<Integer> followedPostIds = new ArrayList<>();
                for(Post p : followedPosts) followedPostIds.add(p.getPostID());
                
                ArrayList<Post> otherPosts = new ArrayList<>();
                for(Post p : recentPosts) {
                    if(!followedPostIds.contains(p.getPostID())) {
                        otherPosts.add(p);
                    }
                }

                Platform.runLater(() -> {
                    feedContainer.getChildren().clear();
                    
                    if (followedPosts.isEmpty() && otherPosts.isEmpty()) {
                        Label emptyLabel = new Label("Welcome! Be the first to post something.");
                        emptyLabel.getStyleClass().add("subtitle-text");
                        feedContainer.getChildren().add(emptyLabel);
                    } else {
                        // Followed Section
                        if (!followedPosts.isEmpty()) {
                            Label followedLabel = new Label("From People You Follow");
                            followedLabel.getStyleClass().add("section-title");
                            followedLabel.setStyle("-fx-text-fill: #60a5fa; -fx-font-weight: bold;");
                            feedContainer.getChildren().add(followedLabel);
                            
                            for (Post p : followedPosts) {
                                feedContainer.getChildren().add(new PostCard(p));
                            }
                        }
                        
                        // Explore Section
                        if (!otherPosts.isEmpty()) {
                            Label exploreLabel = new Label(followedPosts.isEmpty() ? "Explore Posts" : "More to Explore");
                            exploreLabel.getStyleClass().add("section-title");
                            exploreLabel.setStyle("-fx-text-fill: #a78bfa; -fx-font-weight: bold; -fx-padding: 30 0 10 0;");
                            feedContainer.getChildren().add(exploreLabel);
                            
                            for (Post p : otherPosts) {
                                feedContainer.getChildren().add(new PostCard(p));
                            }
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
