package test;

import dao.UserDAO;
import model.User;

import java.time.LocalDate;

public class TestUserDAO {

    public static void main(String[] args) {

        // 1️⃣ Create a new User object
        User user = new User();
        user.setUserName("john_doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123"); // plain text for now
        user.setBirthDate(LocalDate.of(2000, 1, 15));
        user.setBio("Hello! I'm John.");

        // 2️⃣ Create UserDAO instance
        UserDAO userDAO = new UserDAO();

        // 3️⃣ Call createUser and check result
        boolean success = userDAO.createUser(user);

        // 4️⃣ Print result
        if (success) {
            System.out.println("✅ User was created successfully!");
        } else {
            System.out.println("❌ Failed to create user.");
        }
    }
}