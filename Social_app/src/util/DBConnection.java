package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/* el database connection
    string url w user w pass
    Connection class: responsible for the connection between DB and app
    DriverManger: responsible for establishing the connection itself
 */
public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/social_media?useSSL=false&serverTimezone=Africa/Cairo";

    private static final String USER = "admin";
    private static final String PASS = "admin";

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
