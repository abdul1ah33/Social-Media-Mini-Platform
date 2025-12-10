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

    Date getBirthDate();
    void setBirthDate(Date date);

    Account login(String username, String password);
    void logout();
}
