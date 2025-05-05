package Farag_Files_before_Update;
import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        System.out.println("MySQL JDBC Driver Registered!");
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/conpay", "root", "root123");
            System.out.println("Connection Established!");
            String query = "SELECT * FROM Users";
            String deleteQ = "DELETE FROM Users WHERE UserID = 9";
            java.sql.Statement stmt = con.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt(1);
                String username = rs.getString(2);
                System.out.println("Name: " + username + ", ID: " + id);

                    /*CREATE TABLE Users (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    Username VARCHAR(255) NOT NULL UNIQUE, -- Added UNIQUE constraint
    Email VARCHAR(255) NOT NULL UNIQUE,    -- Added UNIQUE constraint
    PassW VARCHAR(255) NOT NULL,           -- Consider storing a hash, not plain text
    Bio TEXT,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
*/
            }
            int x = stmt.executeUpdate(deleteQ);
            if(x>0){
                System.out.println("Deleted User with ID 7");
            }
        } catch (Exception e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}