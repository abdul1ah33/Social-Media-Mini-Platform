package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.User;

import java.net.URL;
import java.util.ArrayList;

public class UserListModal {

    private final Stage parentStage;
    private final String title;
    private ArrayList<User> users;
    private final boolean allowSearch;
    private Stage modalStage;
    private VBox listContainer;
    private final Runnable onUpdate;

    public UserListModal(Stage parentStage, String title, ArrayList<User> users) {
        this(parentStage, title, users, false, null);
    }
    
    public UserListModal(Stage parentStage, String title, ArrayList<User> users, Runnable onUpdate) {
        this(parentStage, title, users, false, onUpdate);
    }

    public UserListModal(Stage parentStage, String title, ArrayList<User> users, boolean allowSearch) {
        this(parentStage, title, users, allowSearch, null);
    }

    public UserListModal(Stage parentStage, String title, ArrayList<User> users, boolean allowSearch, Runnable onUpdate) {
        this.parentStage = parentStage;
        this.title = title;
        this.users = users;
        this.allowSearch = allowSearch;
        this.onUpdate = onUpdate;
    }

    public void show() {
        modalStage = new Stage();
        modalStage.initOwner(parentStage);
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle(title);

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.getStyleClass().add("dialog-pane");
        root.setPrefWidth(480);
        root.setPrefHeight(550);

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Text headerText = new Text(title);
        headerText.getStyleClass().add("title-text");
        headerText.setStyle("-fx-font-size: 22px;");
        
        Text countText = new Text("(" + (users != null ? users.size() : 0) + ")");
        countText.getStyleClass().add("subtitle-text");
        
        header.getChildren().addAll(headerText, countText);

        // Search (if enabled)
        TextField searchField = null;
        if (allowSearch) {
            searchField = new TextField();
            searchField.setPromptText("Search users...");
            searchField.getStyleClass().add("search-field");
            final TextField finalSearchField = searchField;
            searchField.textProperty().addListener((obs, old, newVal) -> filterUsers(newVal));
        }

        // List Container
        listContainer = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(listContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        loadUsers(users);

        // Close Button
        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("secondary-button");
        closeBtn.setMaxWidth(Double.MAX_VALUE);
        closeBtn.setOnAction(e -> modalStage.close());

        if (allowSearch && searchField != null) {
            root.getChildren().addAll(header, searchField, scrollPane, closeBtn);
        } else {
            root.getChildren().addAll(header, scrollPane, closeBtn);
        }

        Scene scene = new Scene(root);
        URL styleResource = getClass().getResource("styles.css");
        if (styleResource != null) {
            scene.getStylesheets().add(styleResource.toExternalForm());
        }

        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    private void loadUsers(ArrayList<User> userList) {
        listContainer.getChildren().clear();
        
        if (userList == null || userList.isEmpty()) {
            Label empty = new Label("No users found.");
            empty.setMaxWidth(Double.MAX_VALUE);
            empty.setAlignment(Pos.CENTER);
            empty.getStyleClass().add("subtitle-text");
            listContainer.getChildren().add(empty);
        } else {
            for (User u : userList) {
                listContainer.getChildren().add(new UserCard(u, () -> {
                    // Refresh if needed
                    if (onUpdate != null) onUpdate.run();
                }));
            }
        }
    }

    private void filterUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadUsers(users);
            return;
        }
        
        ArrayList<User> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (User u : users) {
            if (u.getUserName().toLowerCase().contains(lowerQuery) ||
                u.getFirstName().toLowerCase().contains(lowerQuery) ||
                u.getLastName().toLowerCase().contains(lowerQuery)) {
                filtered.add(u);
            }
        }
        
        loadUsers(filtered);
    }
}
