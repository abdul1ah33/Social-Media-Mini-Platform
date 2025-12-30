package ui;

import dao.UserDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.Post;
import model.User;
import util.DBConnection;
import util.SessionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class PostCard extends VBox {

    private final Post post;
    private final UserDAO userDAO;


    private final service.LikeService likeService;
    private final service.CommentService commentService;
    private final User currentUser;
    
    private Button likeBtn;
    private Button commentBtn; 
    private boolean isLiked;
    private int likeCount;

    public PostCard(Post post) {
        this.post = post;
        this.userDAO = new UserDAO();
        this.likeService = new service.LikeService();
        this.commentService = new service.CommentService();
        this.currentUser = SessionManager.getInstance().getCurrentUser();
        
        initializeUI();
    }

    private void initializeUI() {
        this.getStyleClass().add("post-card");
        this.setSpacing(15);
        
        // Load initial state
        try {
            isLiked = likeService.hasUserLikedPost(post.getPostID(), currentUser.getID());
            likeCount = likeService.getLikesCount(post.getPostID());
        } catch(Exception e) { e.printStackTrace(); }

        // Fetch Author
        User author = fetchAuthor();
        String authorName = (author != null) ? author.getUserName() : "Unknown User";

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Circle avatar = new Circle(18, Color.web("#667EEA"));
        
        VBox metaBox = new VBox(2);
        Text nameText = new Text(authorName);
        nameText.getStyleClass().add("post-header-text");
        
        String dateStr = post.getPostCreationDate() != null 
                ? post.getPostCreationDate().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"))
                : "Just now";
        Text dateText = new Text(dateStr);
        dateText.getStyleClass().add("post-date-text");

        metaBox.getChildren().addAll(nameText, dateText);

        header.getChildren().addAll(avatar, metaBox);

        // Content
        Text contentText = new Text(post.getText());
        contentText.getStyleClass().add("post-content-text");
        contentText.setWrappingWidth(550);

        // Image (if present)
        javafx.scene.image.ImageView postImage = null;
        if (post.getImagePath() != null && !post.getImagePath().isEmpty()) {
            try {
                java.io.File imageFile = new java.io.File(post.getImagePath());
                if (imageFile.exists()) {
                    javafx.scene.image.Image image = new javafx.scene.image.Image(imageFile.toURI().toString());
                    postImage = new javafx.scene.image.ImageView(image);
                    postImage.setFitWidth(550);
                    postImage.setFitHeight(350);
                    postImage.setPreserveRatio(true);
                    postImage.setStyle("-fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Actions (Like, Comment, Edit, Delete) 
        HBox actions = new HBox(20);
        actions.setPadding(new Insets(10, 0, 0, 0));
        
        likeBtn = new Button();
        updateLikeButton();
        likeBtn.getStyleClass().add("post-action-button");
        likeBtn.setOnAction(e -> handleLike());
        
        commentBtn = new Button("Comment (" + getCommentCount() + ")");
        commentBtn.getStyleClass().add("post-action-button");
        commentBtn.setOnAction(e -> showComments());
        
        actions.getChildren().addAll(likeBtn, commentBtn);
        
        // Add Edit/Delete if owner or admin
        boolean isOwner = (post.getUserID() == currentUser.getID());
        boolean isAdmin = SessionManager.getInstance().isAdmin();
        
        if (isOwner) {
            Button editBtn = new Button("✏ Edit");
            editBtn.getStyleClass().add("post-action-button");
            editBtn.setStyle("-fx-text-fill: #60a5fa;");
            editBtn.setOnAction(e -> handleEdit());
            actions.getChildren().add(editBtn);
        }
        
        if (isOwner || isAdmin) {
            Button deleteBtn = new Button("🗑 Delete");
            deleteBtn.getStyleClass().add("post-action-button");
            deleteBtn.setStyle("-fx-text-fill: #f87171;");
            deleteBtn.setOnAction(e -> handleDelete());
            actions.getChildren().add(deleteBtn);
        }

        if (postImage != null) {
            this.getChildren().addAll(header, contentText, postImage, actions);
        } else {
            this.getChildren().addAll(header, contentText, actions);
        }
    }
    
    private void handleEdit() {
        javafx.application.Platform.runLater(() -> 
            new EditPostModal((javafx.stage.Stage) getScene().getWindow(), post, () -> {
                // Refresh the card after edit
                refreshCard();
            }).show()
        );
    }
    
    private void handleDelete() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Post");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This action cannot be undone.");
        alert.getDialogPane().getStyleClass().add("dialog-pane");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    int userId = currentUser.getID();
                    service.PostService ps = new service.PostService();
                    ps.deletePost(post.getPostID(), userId);
                    
                    // Remove this card from parent
                    javafx.scene.Parent parent = this.getParent();
                    if (parent instanceof javafx.scene.layout.VBox) {
                        ((javafx.scene.layout.VBox) parent).getChildren().remove(this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    private void refreshCard() {
        // Reload post content
        this.getChildren().clear();
        initializeUI();
    }
    
    private void handleLike() {
        try {
            if (isLiked) {
                likeService.unlikePost(post.getPostID(), currentUser.getID());
                isLiked = false;
                likeCount--;
            } else {
                likeService.likePost(post.getPostID(), currentUser.getID());
                isLiked = true;
                likeCount++;
            }
            updateLikeButton();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateLikeButton() {
        likeBtn.setText(isLiked ? "Liked (" + likeCount + ")" : "Like (" + likeCount + ")");
        if (isLiked) {
            likeBtn.setStyle("-fx-text-fill: #E53E3E;"); // Red
        } else {
            likeBtn.setStyle(""); // Default
        }
    }
    
    private int getCommentCount() {
        try {
             return commentService.getCommentCountByPost(post.getPostID());
        } catch (Exception e) { return 0; }
    }
    
    private void showComments() {
        // Open Comment Modal
         javafx.application.Platform.runLater(() -> 
                new CommentModal((javafx.stage.Stage) getScene().getWindow(), post.getPostID()).show()
         );
    }

    private User fetchAuthor() {
        try (Connection conn = DBConnection.getConnection()) {
            return userDAO.getDetails(conn, post.getUserID());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
