package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.User;
import service.UserService;
import util.SessionManager;

import java.io.File;
import java.net.URL;

public class EditProfilePictureModal {

    private final Stage parentStage;
    private final User user;
    private final Runnable onPictureUpdated;
    private final UserService userService;
    private Stage modalStage;
    private String selectedImagePath;
    private ImageView imagePreview;

    public EditProfilePictureModal(Stage parentStage, User user, Runnable onPictureUpdated) {
        this.parentStage = parentStage;
        this.user = user;
        this.onPictureUpdated = onPictureUpdated;
        this.userService = new UserService();
        this.selectedImagePath = user.getProfilePicturePath();
    }

    public void show() {
        modalStage = new Stage();
        modalStage.initOwner(parentStage);
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Edit Profile Picture");

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("dialog-pane");
        root.setPrefWidth(400);

        // Header
        Text header = new Text("Update Profile Picture");
        header.getStyleClass().add("title-text");
        header.setStyle("-fx-font-size: 22px;");

        // Preview
        imagePreview = new ImageView();
        imagePreview.setFitWidth(200);
        imagePreview.setFitHeight(200);
        imagePreview.setPreserveRatio(true);
        
        Circle clip = new Circle(100, 100, 100);
        imagePreview.setClip(clip);
        
        if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
            try {
                File imageFile = new File(selectedImagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imagePreview.setImage(image);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (imagePreview.getImage() == null) {
            // Default placeholder
            Circle placeholder = new Circle(100, Color.web("#3b82f6"));
            placeholder.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(59, 130, 246, 0.4), 10, 0, 0, 2);");
            root.getChildren().add(placeholder);
        } else {
            imagePreview.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 2);");
            root.getChildren().add(header);
            root.getChildren().add(imagePreview);
        }

        // Buttons
        Button selectBtn = new Button("📷 Choose Image");
        selectBtn.getStyleClass().add("primary-button");
        selectBtn.setPrefWidth(250);
        selectBtn.setOnAction(e -> selectImage());

        Button removeBtn = new Button("Remove Picture");
        removeBtn.getStyleClass().add("link-button");
        removeBtn.setVisible(selectedImagePath != null && !selectedImagePath.isEmpty());
        removeBtn.setOnAction(e -> {
            selectedImagePath = null;
            imagePreview.setImage(null);
            removeBtn.setVisible(false);
        });

        Button saveBtn = new Button("Save");
        saveBtn.getStyleClass().add("primary-button");
        saveBtn.setPrefWidth(250);
        saveBtn.setOnAction(e -> handleSave());

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("secondary-button");
        cancelBtn.setPrefWidth(250);
        cancelBtn.setOnAction(e -> modalStage.close());

        if (imagePreview.getImage() == null) {
            root.getChildren().addAll(header, selectBtn, cancelBtn);
        } else {
            root.getChildren().addAll(removeBtn, selectBtn, saveBtn, cancelBtn);
        }

        Scene scene = new Scene(root);
        URL styleResource = getClass().getResource("styles.css");
        if (styleResource != null) {
            scene.getStylesheets().add(styleResource.toExternalForm());
        }

        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File selectedFile = fileChooser.showOpenDialog(modalStage);
        if (selectedFile != null) {
            selectedImagePath = selectedFile.getAbsolutePath();
            try {
                Image image = new Image(selectedFile.toURI().toString());
                imagePreview.setImage(image);
                
                // Rebuild UI to show preview
                VBox root = (VBox) modalStage.getScene().getRoot();
                root.getChildren().clear();
                show(); // Rebuild
                modalStage.close(); // Close old
                show(); // Show new
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleSave() {
        try {
            user.setProfilePicturePath(selectedImagePath);
            if (userService.updateProfilePicture(user, selectedImagePath)) {
                SessionManager.getInstance().setCurrentUser(user, SessionManager.getInstance().isAdmin());
                if (onPictureUpdated != null) {
                    onPictureUpdated.run();
                }
                modalStage.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
