package ui;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.User;
import service.FollowService;
import util.SessionManager;

public class UserCard extends HBox {
    
    private final User user;
    private final User currentUser;
    private final FollowService followService;
    private boolean isFollowing;
    private final Runnable onActionComplete;

    public UserCard(User user, Runnable onActionComplete) {
        this.user = user;
        this.onActionComplete = onActionComplete;
        this.currentUser = SessionManager.getInstance().getCurrentUser();
        this.followService = new FollowService();
        
        initializeUI();
        animateIn();
    }

    private void initializeUI() {
        this.getStyleClass().add("user-card");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(15);

        // Avatar with gradient
        Circle avatar = new Circle(22);
        avatar.setFill(Color.web("#3b82f6"));
        avatar.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(59, 130, 246, 0.4), 5, 0, 0, 2);");
        
        // Info
        VBox infoBox = new VBox(3);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        
        Text nameText = new Text(user.getUserName());
        nameText.getStyleClass().add("user-card-name");
        
        Text bioText = new Text(user.getFirstName() + " " + user.getLastName());
        bioText.getStyleClass().add("user-card-detail");
        
        infoBox.getChildren().addAll(nameText, bioText);
        
        // Spacer
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        // Action Button
        Button actionBtn = new Button();
        actionBtn.getStyleClass().add("small-action-button");
        
        if (currentUser.getID() == user.getID()) {
            actionBtn.setText("You");
            actionBtn.setDisable(true);
            actionBtn.setStyle("-fx-opacity: 0.5;");
        } else {
            checkFollowStatus(actionBtn);
            
            actionBtn.setOnAction(e -> {
                animateButton(actionBtn);
                try {
                    if (isFollowing) {
                        followService.unfollowUser(currentUser.getID(), user.getID());
                        isFollowing = false;
                    } else {
                        followService.followUser(currentUser.getID(), user.getID());
                        isFollowing = true;
                    }
                    updateButtonState(actionBtn);
                    if (onActionComplete != null) onActionComplete.run();
                } catch (Exception ex) {
                    System.err.println("Error changing follow status: " + ex.getMessage());
                    // Re-sync state with database in case of mismatch
                    checkFollowStatus(actionBtn);
                }
            });
        }
        
        this.getChildren().addAll(avatar, infoBox, spacer, actionBtn);
    }
    
    private void checkFollowStatus(Button btn) {
        try {
           isFollowing = followService.isFollowing(currentUser.getID(), user.getID());
           updateButtonState(btn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateButtonState(Button btn) {
        if (isFollowing) {
            btn.setText("Following");
            btn.getStyleClass().add("following");
        } else {
            btn.setText("Follow");
            btn.getStyleClass().remove("following");
        }
    }
    
    private void animateIn() {
        FadeTransition ft = new FadeTransition(Duration.millis(300), this);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }
    
    private void animateButton(Button btn) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }
}
