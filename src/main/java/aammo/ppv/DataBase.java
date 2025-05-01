//package aammo.ppv;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//
//public class DataBase {
//    private static HikariDataSource dataSource;
//    private String dbUrl;
//    private String dbUsername;
//    private String dbPassword;
//
//    public DataBase() {
//        loadDatabaseConfig();
//    }
//
//    private void loadDatabaseConfig() {
//        Properties props = new Properties();
//        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
//            props.load(fis);
//            dbUrl = props.getProperty("db.url");
//            dbUsername = props.getProperty("db.username");
//            dbPassword = props.getProperty("db.password");
//
//            HikariConfig config = new HikariConfig();
//            config.setJdbcUrl(dbUrl);
//            config.setUsername(dbUsername);
//            config.setPassword(dbPassword);
//            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
//            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.maxActive")));
//            config.setMinimumIdle(Integer.parseInt(props.getProperty("db.minIdle")));
//            config.setIdleTimeout(60000); // Optional
//            config.setConnectionTestQuery(props.getProperty("db.validationQuery"));
//
//            dataSource = new HikariDataSource(config);
//        } catch (IOException e) {
//            System.err.println("Error loading database configuration: " + e.getMessage());
//        }
//    }
//
//    public List<Person> fetchAllPersons() {
//        List<Person> data = new ArrayList<>();
//        String query = "SELECT * FROM OmarEmad.personalInfo";
//
//        try (Connection con = dataSource.getConnection();
//             Statement stmt = con.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            while (rs.next()) {
//                String name = rs.getString(1);
//                String location = rs.getString(2);
//                String phone = rs.getString(3);
//                data.add(new Person(name, location, phone));
//            }
//
//        } catch (SQLException e) {
//            System.err.println("Error fetching data: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        return data;
//    }
//}
