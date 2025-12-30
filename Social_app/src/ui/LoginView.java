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
        card.setMaxSize(400, 400);
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

        // Admin checkbox
        javafx.scene.control.CheckBox adminCheckBox = new javafx.scene.control.CheckBox("Login as Admin");
        adminCheckBox.getStyleClass().add("subtitle-text");
        adminCheckBox.setStyle("-fx-text-fill: #94a3b8;");

        // Actions
        Button loginBtn = new Button("Login");
        loginBtn.getStyleClass().add("primary-button");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setOnAction(e ->
                handleLogin(usernameField.getText(), passwordField.getText(), adminCheckBox.isSelected())
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
                adminCheckBox,
                loginBtn,
                separator,
                createAccountBtn
        );

        root.getChildren().add(card);
        loginScene = new Scene(root, 900, 600);
        applyStyles(loginScene);
    }

    private javafx.scene.control.DatePicker birthDatePicker;
    
    private void createRegisterScene() {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        VBox card = new VBox(15);
        card.getStyleClass().add("card-pane");
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(450); // Wider for more fields

        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);
        Text title = new Text("Create Account");
        title.getStyleClass().add("title-text");
        Text subtitle = new Text("Join our community freely");
        subtitle.getStyleClass().add("subtitle-text");
        headerBox.getChildren().addAll(title, subtitle);

        // Fields
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        
        HBox nameBox = new HBox(10, firstNameField, lastNameField);
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");

        birthDatePicker = new javafx.scene.control.DatePicker();
        birthDatePicker.setPromptText("Birth Date");
        birthDatePicker.setMaxWidth(Double.MAX_VALUE);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        Button registerBtn = new Button("Sign Up");
        registerBtn.getStyleClass().add("primary-button");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setOnAction(e ->
                handleRegister(
                        usernameField.getText(), 
                        emailField.getText(),
                        passwordField.getText(),
                        confirmPasswordField.getText(),
                        firstNameField.getText(),
                        lastNameField.getText(),
                        birthDatePicker.getValue()
                )
        );

        Button backToLoginBtn = new Button("Already have an account? Login");
        backToLoginBtn.getStyleClass().add("link-button");
        backToLoginBtn.setOnAction(e -> switchToLogin());

        card.getChildren().addAll(
                headerBox,
                nameBox,
                usernameField,
                emailField,
                birthDatePicker,
                passwordField,
                confirmPasswordField,
                registerBtn,
                backToLoginBtn
        );
        root.getChildren().add(card);
        registerScene = new Scene(root, 900, 700); // Slightly taller
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

    private final service.UserService userService = new service.UserService();

    private void handleLogin(String username, String password, boolean isAdmin) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill all fields");
            return;
        }

        try {
            model.User user = null;
            
            if (isAdmin) {
                // Check against admins table
                dao.AdminDAO adminDAO = new dao.AdminDAO();
                user = adminDAO.getAdmin(username, password);
                
                if (user == null) {
                    // Fallback: check if 'admin'/'admin' credential hardcoded for first setup
                    if (username.equals("admin") && password.equals("admin")) {
                         // This is just a fallback if DB table is empty/missing
                         showAlert("Admin Login", "Using temporary admin credentials. Please set up 'admins' table.");
                         user = new model.User("admin", "admin", "System", "Admin", "admin@sys.com", java.time.LocalDate.now());
                         user.setID(9999); // Dummy ID
                    }
                }
            } else {
                // Regular user login
                user = userService.login(username, password);
            }

            if (user != null) {
                // Successful Login
                util.SessionManager.getInstance().setCurrentUser(user, isAdmin);
                
                // Switch to MainView
                MainView mainView = new MainView(primaryStage);
                mainView.show();
            } else {
                showAlert("Login Failed", isAdmin ? "Invalid Admin Credentials" : "Invalid Username or Password");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred during login");
        }
    }

    private void handleRegister(String username, String email, String password, String confirmPassword, String firstName, String lastName, java.time.LocalDate birthDate) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || birthDate == null) {
            showAlert("Error", "Please fill all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match");
            return;
        }

        try {
            boolean success = userService.signup(username, password, firstName, lastName, email, birthDate);
            if (success) {
                showAlert("Success", "Account created successfully! Please login.");
                switchToLogin();
            } else {
                showAlert("Error", "Registration failed. Username or Email might be taken.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred during registration");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
