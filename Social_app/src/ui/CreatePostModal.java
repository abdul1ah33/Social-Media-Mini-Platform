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

public class CreatePostModal {

    private final Stage parentStage;
    private final Runnable onPostCreated;
    private final PostService postService;
    private Stage modalStage;
    private String selectedImagePath;
    private ImageView imagePreview;

    public CreatePostModal(Stage parentStage, Runnable onPostCreated) {
        this.parentStage = parentStage;
        this.onPostCreated = onPostCreated;
        this.postService = new PostService();
    }

    public void show() {
        modalStage = new Stage();
        modalStage.initOwner(parentStage);
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Create New Post");

        VBox root = new VBox(15);
        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(25));
        root.getStyleClass().add("compose-area");

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Text title = new Text("Create Post");
        title.getStyleClass().add("title-text");
        title.setStyle("-fx-font-size: 22px;");
        
        header.getChildren().add(title);

        // Content Area
        TextArea contentArea = new TextArea();
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
        imagePreview.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 10;");

        // Image Controls
        HBox imageControls = new HBox(10);
        imageControls.setAlignment(Pos.CENTER_LEFT);
        
        Button addImageBtn = new Button("📷 Add Image");
        addImageBtn.getStyleClass().add("secondary-button");
        addImageBtn.setOnAction(e -> selectImage());
        
        Button removeImageBtn = new Button("✖ Remove");
        removeImageBtn.getStyleClass().add("link-button");
        removeImageBtn.setVisible(false);
        removeImageBtn.setOnAction(e -> {
            selectedImagePath = null;
            imagePreview.setVisible(false);
            removeImageBtn.setVisible(false);
        });
        
        imageControls.getChildren().addAll(addImageBtn, removeImageBtn);

        // Action Buttons
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER);
        
        Button submitBtn = new Button("Post");
        submitBtn.getStyleClass().add("primary-button");
        submitBtn.setPrefWidth(200);
        submitBtn.setOnAction(e -> handlePost(contentArea.getText()));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("secondary-button");
        cancelBtn.setPrefWidth(200);
        cancelBtn.setOnAction(e -> modalStage.close());
        
        actionButtons.getChildren().addAll(submitBtn, cancelBtn);

        root.getChildren().addAll(header, contentArea, imagePreview, imageControls, actionButtons);

        Scene scene = new Scene(root, 550, 500);
        
        // Add styles
        URL styleResource = getClass().getResource("styles.css");
        if (styleResource != null) {
            scene.getStylesheets().add(styleResource.toExternalForm());
        }

        // Store reference for image removal button
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
                
                // Find and show remove button
                VBox root = (VBox) modalStage.getScene().getRoot();
                HBox imageControls = (HBox) root.getChildren().get(3);
                Button removeBtn = (Button) imageControls.getChildren().get(1);
                removeBtn.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handlePost(String content) {
        if (content == null || content.trim().isEmpty()) {
            // Show error
            return;
        }

        try {
            int userId = SessionManager.getInstance().getCurrentUser().getID();
            Post newPost = new Post(content, selectedImagePath, userId, "General");
            
            postService.createPost(newPost);
            
            if (onPostCreated != null) {
                onPostCreated.run();
            }
            modalStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
