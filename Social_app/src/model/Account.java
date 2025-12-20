package model;

import java.time.LocalDate;
import java.util.Date;

public class Account implements AccountInterface {

    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private static int userCounts = 0;

    public Account() {}

    public Account(String userName, String password, String firstName, String lastName, String email, LocalDate birthDate) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        userCounts++;
    }

    public String getUserName() {return userName;}
    public String getPassword() {return password;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getEmail() {return email;}
    public LocalDate getBirthDate() {return birthDate;}

    public void setUserName(String userName) {this.userName = userName;}
    public void setPassword(String password) {this.password = password;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setEmail(String email) {this.email = email;}
    public void setBirthDate(LocalDate birthDate) {this.birthDate = birthDate;}

    public static int getUserCounts() {return userCounts;}

    public Account login(String username, String password) {
        return new Account();
    }

    public void logout() {}

    public void createAccount(String username, String password, String email, String firstName, String lastName, Date birthDate) {}
}
