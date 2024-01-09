package com.example.hellofx;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getDBConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/universityregister", "root", "000000");

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

}
