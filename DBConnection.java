package clearancems;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String url = "jdbc:mysql://localhost:3306/cms";
    private static final String username = "root";
    private static final String password = "";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Database Connected Successfully!");
        } catch (SQLException ae) {
            System.out.println("Error while connecting to the database");
        }
        return conn;
    }

    public static void main(String[] args) {
        getConnection();
    }
}
