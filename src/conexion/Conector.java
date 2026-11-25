package conexion;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Conector {
    private static final String ARCHIVO_CONFIG = "config.properties";

    public static Connection conectar() throws SQLException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(ARCHIVO_CONFIG)) {
            props.load(fis);
        } catch (IOException e) {
            throw new SQLException("No se pudo leer " + ARCHIVO_CONFIG + " en la raíz del proyecto.", e);
        }

        String url = props.getProperty("DB_URL");
        String usuario = props.getProperty("DB_USER");
        String contrasenia = props.getProperty("DB_PASSWORD");

        if (url == null || url.isBlank() || usuario == null || usuario.isBlank()) {
            throw new SQLException("Revisá DB_URL y DB_USER en " + ARCHIVO_CONFIG + ".");
        }
        if (contrasenia == null) {
            contrasenia = "";
        }

        return DriverManager.getConnection(url, usuario, contrasenia);
    }

    public static void main(String[] args) {
        try (Connection con = Conector.conectar()) {
            System.out.println("Se pudo conectar");
        } catch (SQLException e) {
            System.out.println("Error de conexión - Código: " + e.getErrorCode() + " | Mensaje: " + e.getMessage());
        }
    }
}