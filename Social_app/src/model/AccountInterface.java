package model;

import java.time.LocalDate;
import java.util.Date;

public interface AccountInterface {
    String getUserName();
    void setUserName(String name);

    String getPassword();
    void setPassword(String password);

    String getEmail();
    void setEmail(String email);

    String getFirstName();
    void setFirstName(String name);

    String getLastName();
    void setLastName(String name);

    LocalDate getBirthDate();
    void setBirthDate(LocalDate date);

    Account login(String username, String password);
    void logout();
    void createAccount(String username, String password, String email, String firstName, String lastName, Date birthDate);
}
