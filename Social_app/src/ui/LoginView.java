package ui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;

public class LoginView extends Application {

    private Stage primaryStage;
    private Scene loginScene;
    private Scene registerScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Social Mini Platform");

        createLoginScene();
        createRegisterScene();

        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private void createLoginScene() {
        // Main container
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        // Card container for login form  which will be in Main container
        VBox card = new VBox(20);
        card.getStyleClass().add("card-pane");
        card.setAlignment(Pos.CENTER);

        // Header
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);

        Text title = new Text("Welcome Back");
        title.getStyleClass().add("title-text");

        Text subtitle = new Text("Sign in to continue");
        subtitle.getStyleClass().add("subtitle-text");

        headerBox.getChildren().addAll(title, subtitle);

        // Form Fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username or Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Actions
        Button loginBtn = new Button("Login");
        loginBtn.getStyleClass().add("primary-button");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setOnAction(e ->
                handleLogin(usernameField.getText(), passwordField.getText())
        );

        Label separator = new Label("OR");
        separator.getStyleClass().add("separator-label");

        Button createAccountBtn = new Button("Create New Account");
        createAccountBtn.getStyleClass().add("secondary-button");
        createAccountBtn.setMaxWidth(Double.MAX_VALUE);
        createAccountBtn.setOnAction(e -> switchToRegister());

        card.getChildren().addAll(
                headerBox,
                usernameField,
                passwordField,
                loginBtn,
                separator,
                createAccountBtn
        );

        root.getChildren().add(card);
        loginScene = new Scene(root, 900, 600);
        applyStyles(loginScene);
    }

    private void createRegisterScene() {
        // Main container
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        // Card container for register form which will be in Main container
        VBox card = new VBox(15);
        card.getStyleClass().add("card-pane");
        card.setAlignment(Pos.CENTER);

        // Header
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);

        Text title = new Text("Create Account");
        title.getStyleClass().add("title-text");

        Text subtitle = new Text("Join our community freely");
        subtitle.getStyleClass().add("subtitle-text");

        headerBox.getChildren().addAll(title, subtitle);

        // Form Fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        // Buttons Actions
        Button registerBtn = new Button("Sign Up");
        registerBtn.getStyleClass().add("primary-button");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setOnAction(e ->
                handleRegister(usernameField.getText(), emailField.getText())
        );

        Button backToLoginBtn = new Button("Already have an account? Login");
        backToLoginBtn.getStyleClass().add("link-button");
        backToLoginBtn.setOnAction(e -> switchToLogin());

        card.getChildren().addAll(
                headerBox,
                usernameField,
                emailField,
                passwordField,
                confirmPasswordField,
                registerBtn,
                backToLoginBtn
        );
        root.getChildren().add(card);
        registerScene = new Scene(root, 900, 600);
        applyStyles(registerScene);
    }

    private void applyStyles(Scene scene) {
        URL styleResource = getClass().getResource("styles.css");
        if (styleResource != null) {
            scene.getStylesheets().add(styleResource.toExternalForm());
        } else {
            System.err.println("Could not find style.css resource. Ensure it's in the same package/folder.");
        }
    }

    private void switchToRegister() {
        primaryStage.setScene(registerScene);
    }

    private void switchToLogin() {
        primaryStage.setScene(loginScene);
    }

    // didn't decide what to do here yet
    private void handleLogin(String username, String password) {

    }

    private void handleRegister(String username, String email) {
    }

    public static void main(String[] args) {
        launch(args);
    }
}
