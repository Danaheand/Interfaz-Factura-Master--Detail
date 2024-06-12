package confi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexion {
    private static final String URL = "jdbc:mysql://localhost/factura?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = " ";

    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error al conectarse a la BD " + e);
        }
        return con;
    }

    public static void main(String[] args) {
        getConnection();
    }
}
