package util;
import java.sql.Connection;

public class TestDBConnection {

    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("✅ Connected to MySQL successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
