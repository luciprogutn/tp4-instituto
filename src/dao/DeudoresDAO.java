package dao;

import conexion.Conector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeudoresDAO {

    public List<Object[]> listarDeudores() throws Exception {
        String sqlDeudores = "SELECT a.apellido, a.nombre, a.dni, c.codigo, c.valor_cuota " +
                "FROM inscripciones i " +
                "INNER JOIN alumnos a ON i.id_alumno = a.id " +
                "INNER JOIN cursos c ON i.codigo_curso = c.codigo " +
                "WHERE i.cuota_paga = FALSE " +
                "ORDER BY a.apellido ASC, a.nombre ASC";

        List<Object[]> deudores = new ArrayList<>();

        try (Connection conn = Conector.conectar();
             PreparedStatement ps = conn.prepareStatement(sqlDeudores);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                deudores.add(new Object[]{
                        rs.getString("apellido"),
                        rs.getString("nombre"),
                        rs.getString("dni"),
                        rs.getString("codigo"),
                        rs.getDouble("valor_cuota")
                });
            }

            return deudores;
        } catch (SQLException e) {
            throw traducirErrorSQL(e);
        }
    }

    private static Exception traducirErrorSQL(SQLException e) {
        if (e.getSQLState() != null && e.getSQLState().startsWith("08")) {
            return new Exception("No se puede conectar con la base de datos. Verificá que el servidor esté encendido.");
        }
        return new Exception("Error al consultar deudores: " + e.getMessage());
    }
}