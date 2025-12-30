package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.User;
import util.SessionManager;

import java.net.URL;

public class MainView {

    private final Stage stage;
    private final User currentUser;
    private BorderPane rootLayout;
    private VBox sidebar;

    // View References
    private HomeView homeView;
    private ProfileView profileView;
    private NewsView newsView;
    
    // Navigation Buttons
    private Button homeBtn;
    private Button profileBtn;
    private Button newsBtn;
    private Button logoutBtn;

    public MainView(Stage stage) {
        this.stage = stage;
        this.currentUser = SessionManager.getInstance().getCurrentUser();
        initializeViews();
    }

    private void initializeViews() {
        homeView = new HomeView();
        profileView = new ProfileView(currentUser);
        newsView = new NewsView();
    }

    public void show() {
        rootLayout = new BorderPane();
        rootLayout.getStyleClass().add("main-container");

        createSidebar();
        rootLayout.setLeft(sidebar);
        
        // Default View
        showHome();

        Scene scene = new Scene(rootLayout, 1000, 700);
        applyStyles(scene);
        stage.setScene(scene);
        stage.setTitle("Social Mini Platform - " + currentUser.getUserName());
        stage.show();
    }

    private void createSidebar() {
        sidebar = new VBox(15);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setAlignment(Pos.TOP_LEFT);

        // User Info in Sidebar
        HBox userInfo = new HBox(10);
        userInfo.setAlignment(Pos.CENTER_LEFT);
        userInfo.setPadding(new Insets(0, 0, 20, 0));

        // Avatar
        javafx.scene.image.ImageView avatar = new javafx.scene.image.ImageView();
        avatar.setFitWidth(40);
        avatar.setFitHeight(40);
        avatar.setPreserveRatio(true);
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(20, 20, 20);
        avatar.setClip(clip);

        boolean hasPic = false;
        if (currentUser.getProfilePicturePath() != null && !currentUser.getProfilePicturePath().isEmpty()) {
            try {
                java.io.File file = new java.io.File(currentUser.getProfilePicturePath());
                if (file.exists()) {
                    avatar.setImage(new javafx.scene.image.Image(file.toURI().toString()));
                    hasPic = true;
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        if (!hasPic) {
            // Default Circle
            Circle defaultAvatar = new Circle(20, Color.web("#667EEA"));
            userInfo.getChildren().addAll(defaultAvatar);
        } else {
             userInfo.getChildren().add(avatar);
        }

        Text usernameText = new Text(currentUser.getUserName());
        usernameText.setStyle("-fx-font-weight: bold; -fx-fill: #f8fafc;");
        
        userInfo.getChildren().add(usernameText);

        // Navigation
        homeBtn = createNavButton("Home", "home-icon"); // Icon can be added later
        homeBtn.setOnAction(e -> showHome());

        profileBtn = createNavButton("My Profile", "user-icon");
        profileBtn.setOnAction(e -> showProfile());

        newsBtn = createNavButton("📰 News", "news-icon");
        newsBtn.setOnAction(e -> showNews());

        Button createPostBtn = createNavButton("Create Post", "plus-icon");
        createPostBtn.setOnAction(e -> showCreatePostModal());
        createPostBtn.getStyleClass().add("primary-button");
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        logoutBtn = createNavButton("Logout", "logout-icon");
        logoutBtn.setOnAction(e -> handleLogout());
        logoutBtn.setStyle("-fx-text-fill: #E53E3E;");

        // Search Bar
        javafx.scene.control.TextField searchField = new javafx.scene.control.TextField();
        searchField.setPromptText("Search users...");
        searchField.getStyleClass().add("search-field");
        searchField.setOnAction(e -> handleSearch(searchField.getText()));

        sidebar.getChildren().addAll(
                userInfo,
                searchField,
                homeBtn,
                profileBtn,
                newsBtn,
                createPostBtn,
                spacer,
                logoutBtn
        );
    }

    private void handleSearch(String query) {
        if (query == null || query.trim().isEmpty()) return;
        
        new Thread(() -> {
            service.UserService us = new service.UserService();
            java.util.ArrayList<User> results = us.searchUsers(query);
            javafx.application.Platform.runLater(() -> 
                new UserListModal(stage, "Search Results: " + query, results).show()
            );
        }).start();
    }

    private Button createNavButton(String text, String iconName) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private void setCenterWithAnimation(javafx.scene.Node node) {
        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(300), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        rootLayout.setCenter(node);
        ft.play();
    }

    private void showHome() {
        setActiveButton(homeBtn);
        setCenterWithAnimation(homeView.getView());
        homeView.refreshFeed(); // Refresh data
    }

    private void showProfile() {
        setActiveButton(profileBtn);
        setCenterWithAnimation(profileView.getView());
        profileView.refreshPosts();
    }

    private void showNews() {
        setActiveButton(newsBtn);
        setCenterWithAnimation(newsView.getView());
        newsView.refresh();
    }

    private void showCreatePostModal() {
        CreatePostModal modal = new CreatePostModal(stage, () -> {
            // Callback when post is created
            if (rootLayout.getCenter() == homeView.getView()) {
                homeView.refreshFeed();
            } else if (rootLayout.getCenter() == profileView.getView()) {
                profileView.refreshPosts();
            }
        });
        modal.show();
    }

    private void setActiveButton(Button activeBtn) {
        homeBtn.getStyleClass().remove("active");
        profileBtn.getStyleClass().remove("active");
        newsBtn.getStyleClass().remove("active");
        
        activeBtn.getStyleClass().add("active");
    }

    private void handleLogout() {
        SessionManager.getInstance().logout();
        try {
            new LoginView().start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyStyles(Scene scene) {
        URL styleResource = getClass().getResource("styles.css");
        if (styleResource != null) {
            scene.getStylesheets().add(styleResource.toExternalForm());
        }
    }
}
