package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Post;
import service.PostService;
import util.SessionManager;

import java.io.File;
import java.net.URL;

public class EditPostModal {

    private final Stage parentStage;
    private final Post post;
    private final Runnable onPostUpdated;
    private final PostService postService;
    private Stage modalStage;
    private String selectedImagePath;
    private ImageView imagePreview;

    public EditPostModal(Stage parentStage, Post post, Runnable onPostUpdated) {
        this.parentStage = parentStage;
        this.post = post;
        this.onPostUpdated = onPostUpdated;
        this.postService = new PostService();
        this.selectedImagePath = post.getImagePath();
    }

    public void show() {
        modalStage = new Stage();
        modalStage.initOwner(parentStage);
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Edit Post");

        VBox root = new VBox(15);
        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(25));
        root.getStyleClass().add("compose-area");

        // Header
        Text title = new Text("Edit Post");
        title.getStyleClass().add("title-text");
        title.setStyle("-fx-font-size: 22px;");

        // Content Area
        TextArea contentArea = new TextArea(post.getText());
        contentArea.setPromptText("What's on your mind?");
        contentArea.setPrefRowCount(6);
        contentArea.setWrapText(true);
        contentArea.getStyleClass().add("compose-text-area");

        // Image Preview
        imagePreview = new ImageView();
        imagePreview.setFitWidth(400);
        imagePreview.setFitHeight(250);
        imagePreview.setPreserveRatio(true);
        imagePreview.setVisible(false);
        
        if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
            try {
                File imageFile = new File(selectedImagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imagePreview.setImage(image);
                    imagePreview.setVisible(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Image Controls
        HBox imageControls = new HBox(10);
        imageControls.setAlignment(Pos.CENTER_LEFT);
        
        Button changeImageBtn = new Button("📷 Change Image");
        changeImageBtn.getStyleClass().add("secondary-button");
        changeImageBtn.setOnAction(e -> selectImage());
        
        Button removeImageBtn = new Button("✖ Remove");
        removeImageBtn.getStyleClass().add("link-button");
        removeImageBtn.setVisible(selectedImagePath != null && !selectedImagePath.isEmpty());
        removeImageBtn.setOnAction(e -> {
            selectedImagePath = null;
            imagePreview.setVisible(false);
            removeImageBtn.setVisible(false);
        });
        
        imageControls.getChildren().addAll(changeImageBtn, removeImageBtn);

        // Action Buttons
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER);
        
        Button saveBtn = new Button("Save Changes");
        saveBtn.getStyleClass().add("primary-button");
        saveBtn.setPrefWidth(200);
        saveBtn.setOnAction(e -> handleSave(contentArea.getText()));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("secondary-button");
        cancelBtn.setPrefWidth(200);
        cancelBtn.setOnAction(e -> modalStage.close());
        
        actionButtons.getChildren().addAll(saveBtn, cancelBtn);

        root.getChildren().addAll(title, contentArea, imagePreview, imageControls, actionButtons);

        Scene scene = new Scene(root, 550, 500);
        
        URL styleResource = getClass().getResource("styles.css");
        if (styleResource != null) {
            scene.getStylesheets().add(styleResource.toExternalForm());
        }

        // Store reference for buttons
        final Button finalRemoveBtn = removeImageBtn;
        
        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File selectedFile = fileChooser.showOpenDialog(modalStage);
        if (selectedFile != null) {
            selectedImagePath = selectedFile.getAbsolutePath();
            try {
                Image image = new Image(selectedFile.toURI().toString());
                imagePreview.setImage(image);
                imagePreview.setVisible(true);
                
                // Show remove button
                VBox root = (VBox) modalStage.getScene().getRoot();
                HBox imageControls = (HBox) root.getChildren().get(3);
                Button removeBtn = (Button) imageControls.getChildren().get(1);
                removeBtn.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleSave(String content) {
        if (content == null || content.trim().isEmpty()) {
            return;
        }

        try {
            post.setText(content);
            post.setImagePath(selectedImagePath);
            
            int userId = SessionManager.getInstance().getCurrentUser().getID();
            postService.updatePost(post.getPostID(), post, userId);
            
            if (onPostUpdated != null) {
                onPostUpdated.run();
            }
            modalStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
