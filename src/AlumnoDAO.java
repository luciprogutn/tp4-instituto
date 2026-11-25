package dao;

import conexion.Conector;
import modelo.Alumno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AlumnoDAO {

    public List<Alumno> listar() throws Exception {
        String sql = "SELECT id, dni, nombre, apellido, correo FROM alumnos ORDER BY apellido ASC, nombre ASC";
        List<Alumno> alumnos = new ArrayList<>();

        try (Connection conn = Conector.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                alumnos.add(mapearAlumno(rs));
            }
            return alumnos;
        } catch (SQLException e) {
            throw traducirErrorSQL(e);
        }
    }

    public Alumno buscarPorId(int id) throws Exception {
        String sql = "SELECT id, dni, nombre, apellido, correo FROM alumnos WHERE id = ?";

        try (Connection conn = Conector.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearAlumno(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw traducirErrorSQL(e);
        }
    }

    public int insertar(Alumno alumno) throws Exception {
        String sql = "INSERT INTO alumnos (dni, nombre, apellido, correo) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conector.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, alumno.getDni());
            ps.setString(2, alumno.getNombre().trim());
            ps.setString(3, alumno.getApellido().trim());

            if (alumno.getCorreo() == null || alumno.getCorreo().isBlank()) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, alumno.getCorreo().trim());
            }

            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    int idGenerado = claves.getInt(1);
                    alumno.setId(idGenerado);
                    return idGenerado;
                }
            }
            return 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new Exception("Ya existe un alumno registrado con el DNI " + alumno.getDni() + ".");
        } catch (SQLException e) {
            throw traducirErrorSQL(e);
        }
    }

    public boolean modificar(Alumno alumno) throws Exception {
        String sql = "UPDATE alumnos SET dni = ?, nombre = ?, apellido = ?, correo = ? WHERE id = ?";

        try (Connection conn = Conector.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, alumno.getDni());
            ps.setString(2, alumno.getNombre().trim());
            ps.setString(3, alumno.getApellido().trim());

            if (alumno.getCorreo() == null || alumno.getCorreo().isBlank()) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, alumno.getCorreo().trim());
            }

            ps.setInt(5, alumno.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new Exception("Ya existe otro alumno registrado con el DNI " + alumno.getDni() + ".");
        } catch (SQLException e) {
            throw traducirErrorSQL(e);
        }
    }

    public boolean borrar(int id) throws Exception {
        String sql = "DELETE FROM alumnos WHERE id = ?";

        try (Connection conn = Conector.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new Exception("No se puede eliminar el alumno porque tiene inscripciones registradas.");
        } catch (SQLException e) {
            throw traducirErrorSQL(e);
        }
    }

    private Alumno mapearAlumno(ResultSet rs) throws SQLException {
        return new Alumno(
                rs.getInt("id"),
                rs.getInt("dni"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("correo")
        );
    }

    public static Exception traducirErrorSQL(SQLException e) {
        if (e.getSQLState() != null && e.getSQLState().startsWith("08")) {
            return new Exception("No se puede conectar con la base de datos. Verificá que el servidor esté encendido.");
        }
        return switch (e.getErrorCode()) {
            case 1045 -> new Exception("Usuario o contraseña de base de datos incorrectos.");
            case 1049 -> new Exception("La base de datos del instituto no existe. Ejecutá instituto.sql.");
            default -> new Exception("No se pudo completar la operación en la base de datos.");
        };
    }
}

