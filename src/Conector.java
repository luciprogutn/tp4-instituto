import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;

public class Conector {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().directory("C:\\Users\\DELL\\Desktop\\tp4\\src\\sources\\").load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USUARIO = dotenv.get("DB_USER");
    private static final String CONTRASENIA = dotenv.get("DB_PASSWORD");

    public static Connection conectar() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USUARIO, CONTRASENIA);
            System.out.println("Conectado exitosamente");
        } catch (SQLException e) {
            System.out.println("Error de conexión - Código: " + e.getErrorCode() + " | Mensaje: " + e.getMessage());
        }
        return con;
    }


    public static void main(String[] args) {

        try(Connection con = Conector.conectar()){
            System.out.println("Se pudo conectar");
        } catch(SQLException e){
            System.out.println(e.getErrorCode());
        }
    }
}
