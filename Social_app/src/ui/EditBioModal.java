package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.User;
import service.UserService;
import util.SessionManager;

import java.net.URL;

public class EditBioModal {

    private final Stage parentStage;
    private final User user;
    private final Runnable onBioUpdated;
    private final UserService userService;
    private Stage modalStage;

    public EditBioModal(Stage parentStage, User user, Runnable onBioUpdated) {
        this.parentStage = parentStage;
        this.user = user;
        this.onBioUpdated = onBioUpdated;
        this.userService = new UserService();
    }

    public void show() {
        modalStage = new Stage();
        modalStage.initOwner(parentStage);
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Edit Bio");

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.getStyleClass().add("dialog-pane");
        root.setPrefWidth(450);

        // Header
        Text header = new Text("Update Your Bio");
        header.getStyleClass().add("title-text");
        header.setStyle("-fx-font-size: 22px;");

        Text subtitle = new Text("Tell people a bit about yourself");
        subtitle.getStyleClass().add("subtitle-text");

        // Bio Input
        TextArea bioArea = new TextArea(user.getBio());
        bioArea.setPromptText("Write your bio here...");
        bioArea.setPrefRowCount(5);
        bioArea.setWrapText(true);
        bioArea.getStyleClass().add("compose-text-area");

        Label charCount = new Label("0/150 characters");
        charCount.getStyleClass().add("subtitle-text");
        charCount.setStyle("-fx-font-size: 11px;");
        
        bioArea.textProperty().addListener((obs, old, newVal) -> {
            int len = newVal != null ? newVal.length() : 0;
            charCount.setText(len + "/150 characters");
            if (len > 150) {
                bioArea.setText(old);
            }
        });

        // Action Buttons
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);

        Button saveBtn = new Button("Save");
        saveBtn.getStyleClass().add("primary-button");
        saveBtn.setPrefWidth(180);
        saveBtn.setOnAction(e -> handleSave(bioArea.getText()));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("secondary-button");
        cancelBtn.setPrefWidth(180);
        cancelBtn.setOnAction(e -> modalStage.close());

        actions.getChildren().addAll(saveBtn, cancelBtn);

        root.getChildren().addAll(header, subtitle, bioArea, charCount, actions);

        Scene scene = new Scene(root);
        URL styleResource = getClass().getResource("styles.css");
        if (styleResource != null) {
            scene.getStylesheets().add(styleResource.toExternalForm());
        }

        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    private void handleSave(String newBio) {
        try {
            if (userService.updateBio(user, newBio)) {
                user.setBio(newBio);
                SessionManager.getInstance().setCurrentUser(user); // Update session
                if (onBioUpdated != null) {
                    onBioUpdated.run();
                }
                modalStage.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
