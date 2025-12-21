package test;

import dao.UserDAO;
import model.User;

import java.time.LocalDate;
import java.util.Scanner;

public class TestUserDAO {

    public static void main(String[] args) {

        System.out.println("Add user = 1 or show user = 2");
        Scanner input = new Scanner(System.in);
        if (input.nextInt() == 1) {
            // 1️⃣ Create a new User object
            User user = new User();
            user.setUserName("hamo_elgamed");
            user.setFirstName("Hamo");
            user.setLastName("elgamed");
            user.setEmail("hamo.elga7ed@example.com");
            user.setPassword("password123");
            user.setBirthDate(LocalDate.of(2000, 1, 15));
            user.setBio("Hello! I'm Hamo.");

            // 2️⃣ Create UserDAO instance
            UserDAO userDAO = new UserDAO();

            // 3️⃣ Call createUser and check result
            boolean success = userDAO.addUser(user);

            // 4️⃣ Print result
            if (success) {
                System.out.println("✅ User was created successfully!");
            } else {
                System.out.println("❌ Failed to create user.");
            }
        }
        else if (input.nextInt() == 2) {
            User user2 = new UserDAO().getUser(2);
            System.out.println(user2.getUserName() + " " + user2.getFirstName() + " " + user2.getLastName());
        }
    }
}