import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO {
    public List<Curso> listar() throws SQLException {
        String sql = "SELECT cupo FROM curso";
        try (Connection con = Conector.conectar(); // <-- El DAO llama a Conector
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
        }
        return new ArrayList<Curso>(30);
    }
}