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
            user.setUserName("nour");
            user.setFirstName("Hamo");
            user.setLastName("elgamed");
            user.setEmail("nour.ayman@example.com");
            user.setPassword("password123");
            user.setBirthDate(LocalDate.of(2000, 1, 15));
            user.setBio("Hello! I'm Hamo.");

            // 2️⃣ Create UserDAO instance
            UserDAO userDAO = new UserDAO();

            // 3️⃣ Call createUser and check result
            boolean success = userDAO.add(user);

            // 4️⃣ Print result
            if (success) {
                System.out.println("✅ User was created successfully!");
            } else {
                System.out.println("❌ Failed to create user.");
            }
        }
        else if (input.nextInt() == 2) {
            User user2 = new UserDAO().getAccountDetails(2);
            System.out.println(user2.getUserName() + " " + user2.getFirstName() + " " + user2.getLastName());
        }
        else if (input.nextInt() == 3) {
            User useredit = new User();
//            useredit.setUserName("nour_ayman");
//            useredit.setFirstName("Nour");
//            useredit.setLastName("Ayman");
//            useredit.setEmail("nour.ayman1@example.com");
//            useredit.setPassword("password123");
            useredit.setBirthDate(LocalDate.of(2005, 3, 15));
//            useredit.setBio("Hello! I'm Nour.");

            boolean success = new UserDAO().update(useredit, 12);

            if (success) {
                System.out.println("✅ User was updated successfully!");
            } else {
                System.out.println("❌ Failed to updated user.");
            }
        }

        else if (input.nextInt() == 4) {
            boolean success = new UserDAO().delete(12);

            if (success) {
                System.out.println("✅ User was deleted successfully!");
            } else {
                System.out.println("❌ Failed to deleted user.");
            }
        }
    }
}