import java.sql.*;
//import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;
import java.util.Properties;

public class Conector {
    private static final Properties properties = Properties.configure().ignoreIfMissing().directory("C:\\Users\\DELL\\Desktop\\tp4\\src\\sources\\").load();
    private static final String URL = properties.get("DB_URL").toString();
    private static final String USUARIO = properties.get("DB_USER").toString();
    private static final String CONTRASENIA = properties.get("DB_PASSWORD").toString();

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


   //public static void main(String[] args) {
    //
    //        try(Connection con = Conector.conectar()){
    //            System.out.println("Se pudo conectar");
    //        } catch(SQLException e){
    //            System.out.println(e.getErrorCode());
    //        }
    //    }
}
