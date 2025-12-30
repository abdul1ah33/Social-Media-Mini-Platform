package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Comment;
import service.CommentService;
import util.SessionManager;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CommentModal {

    private final Stage parentStage;
    private final int postId;
    private final CommentService commentService;
    private Stage modalStage;
    private VBox commentsContainer;
    private final Runnable onUpdate; // Callback for parent to refresh count

    public CommentModal(Stage parentStage, int postId, Runnable onUpdate) {
        this.parentStage = parentStage;
        this.postId = postId;
        this.commentService = new CommentService();
        this.onUpdate = onUpdate;
    }

    public void show() {
        modalStage = new Stage();
        modalStage.initOwner(parentStage);
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Comments");

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("dialog-pane");
        root.setPrefWidth(450);
        root.setPrefHeight(600);

        // Header
        Text header = new Text("Comments");
        header.getStyleClass().add("title-text");
        header.setStyle("-fx-font-size: 20px;");

        // List
        commentsContainer = new VBox(15);
        ScrollPane scrollPane = new ScrollPane(commentsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        loadComments();

        // Input Area
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        
        TextArea inputField = new TextArea();
        inputField.setPromptText("Write a comment...");
        inputField.setPrefRowCount(2);
        inputField.setWrapText(true);
        inputField.getStyleClass().add("text-field"); // Reuse text-field style
        
        Button sendBtn = new Button("Send");
        sendBtn.getStyleClass().add("primary-button");
        sendBtn.setOnAction(e -> handleSend(inputField.getText(), inputField));

        inputBox.getChildren().addAll(inputField, sendBtn);

        // Close
        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("secondary-button");
        closeBtn.setMaxWidth(Double.MAX_VALUE);
        closeBtn.setOnAction(e -> modalStage.close());

        root.getChildren().addAll(header, scrollPane, inputBox, closeBtn);

        Scene scene = new Scene(root);
        URL styleResource = getClass().getResource("styles.css");
        if (styleResource != null) {
            scene.getStylesheets().add(styleResource.toExternalForm());
        }

        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    private void loadComments() {
        commentsContainer.getChildren().clear();
        ArrayList<Comment> comments = commentService.getCommentsByPost(postId);
        
        if (comments.isEmpty()) {
            Label empty = new Label("No comments yet.");
            empty.setMaxWidth(Double.MAX_VALUE);
            empty.setAlignment(Pos.CENTER);
            empty.setStyle("-fx-text-fill: #A0AEC0;");
            commentsContainer.getChildren().add(empty);
        } else {
            for (Comment c : comments) {
                commentsContainer.getChildren().add(createCommentRow(c));
            }
        }
    }

    private VBox createCommentRow(Comment c) {
        VBox row = new VBox(5);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: #4A5568; -fx-background-radius: 8;");
        
        HBox meta = new HBox(10);
        meta.setAlignment(Pos.CENTER_LEFT);
        
        Text name = new Text(c.getUser().getUserName());
        name.setStyle("-fx-font-weight: bold; -fx-fill: #E2E8F0;");
        
        String timeStr = c.getCommentTime() != null ? 
                c.getCommentTime().format(DateTimeFormatter.ofPattern("MMM dd HH:mm")) : "";
        Text time = new Text(timeStr);
        time.setStyle("-fx-font-size: 10px; -fx-fill: #A0AEC0;");
        
        meta.getChildren().addAll(name, time);
        
        // Spacer
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        meta.getChildren().add(spacer);
        
        // Delete button if owner or admin
        boolean isOwner = SessionManager.getInstance().getCurrentUser().getID() == c.getUser().getID();
        boolean isAdmin = SessionManager.getInstance().isAdmin();
        
        if (isOwner || isAdmin) {
             Button deleteBtn = new Button("X");
             deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #FC8181; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand;");
             deleteBtn.setOnAction(e -> handleDeleteComment(c.getCommentID()));
             meta.getChildren().add(deleteBtn);
        }
        
        Text content = new Text(c.getContent());
        content.setStyle("-fx-fill: white;");
        content.setWrappingWidth(380);
        
        row.getChildren().addAll(meta, content);
        return row;
    }

    private void handleDeleteComment(int commentId) {
        try {
            commentService.deleteComment(commentId);
            loadComments(); // Refresh list
            if (onUpdate != null) onUpdate.run(); // Notify parent
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSend(String text, TextArea inputField) {
        if (text == null || text.trim().isEmpty()) return;
        
        try {
            Comment comment = new Comment(text, SessionManager.getInstance().getCurrentUser(), postId);
            commentService.CreateComment(comment);
            inputField.clear();
            loadComments();
            if (onUpdate != null) onUpdate.run(); // Notify parent
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
