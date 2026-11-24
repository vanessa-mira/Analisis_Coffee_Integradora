package mx.edu.utez.coffeeclub.DB;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySqlConnect {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = MySqlConnect.class.getClassLoader().getResourceAsStream("db.properties")) {
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo leer el archivo db.properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver de MySQL. Revisa Maven.", e);
        }
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password"));
    }
}
