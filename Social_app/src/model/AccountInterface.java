package model;

import java.time.LocalDate;
import java.util.Date;

public interface AccountInterface {

    // Getters
    String getUserName();
    String getPassword();
    String getEmail();
    String getFirstName();
    String getLastName();
    LocalDate getBirthDate();

    // Setters
    void setUserName(String name);
    void setPassword(String password);
    void setEmail(String email);
    void setFirstName(String name);
    void setLastName(String name);
    void setBirthDate(LocalDate date);

    // Methods
    Account login(String username, String password);
    void logout();
    void createAccount(String username, String password, String email, String firstName, String lastName, Date birthDate);
}
