package dao;

import conexion.Conector;
import modelo.Curso;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO {

    // C.1: Devuelve los cursos ordenados por código; los inscriptos y vacantes
    // se calculan en SQL (LEFT JOIN + COUNT + cupo - COUNT)
    public List<Curso> listar() throws Exception {
        String sql = "SELECT c.codigo, c.idioma, c.nivel, c.turno, c.cupo, c.cuota_mensual " +
                "FROM cursos c " +
                "ORDER BY c.codigo ASC";

        List<Curso> lista = new ArrayList<>();

        try (Connection conn = Conector.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Curso curso = new Curso(
                        rs.getString("codigo"),
                        rs.getString("idioma"),
                        rs.getString("nivel"),
                        rs.getString("turno"),
                        rs.getInt("cupo"),
                        rs.getDouble("cuota_mensual")
                );
                lista.add(curso);
            }
            return lista;

        } catch (SQLException e) {
            throw AlumnoDAO.traducirErrorSQL(e);
        }
    }

    // Devuelve filas con [codigo, idioma, nivel, turno, cupo, inscriptos, vacantes]
    // para cargar directo en la JTable del punto C.1
    public List<Object[]> listarFilasConVacantes() throws Exception {
        String sql = "SELECT c.codigo, c.idioma, c.nivel, c.turno, c.cupo, " +
                "COUNT(i.id) AS inscriptos, " +
                "(c.cupo - COUNT(i.id)) AS vacantes, " +
                "c.cuota_mensual " +
                "FROM cursos c " +
                "LEFT JOIN inscripciones i ON c.codigo = i.codigo_curso " +
                "GROUP BY c.codigo, c.idioma, c.nivel, c.turno, c.cupo, c.cuota_mensual " +
                "ORDER BY c.codigo ASC";

        List<Object[]> filas = new ArrayList<>();

        try (Connection conn = Conector.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                filas.add(new Object[]{
                        rs.getString("codigo"),
                        rs.getString("idioma"),
                        rs.getString("nivel"),
                        rs.getString("turno"),
                        rs.getInt("cupo"),
                        rs.getInt("inscriptos"),
                        rs.getInt("vacantes"),
                        rs.getDouble("cuota_mensual")
                });
            }
            return filas;

        } catch (SQLException e) {
            throw AlumnoDAO.traducirErrorSQL(e);
        }
    }

    public Curso buscarPorId(String codigo) throws Exception {
        String sql = "SELECT codigo, idioma, nivel, turno, cupo, cuota_mensual " +
                "FROM cursos WHERE codigo = ?";

        try (Connection conn = Conector.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Curso(
                            rs.getString("codigo"),
                            rs.getString("idioma"),
                            rs.getString("nivel"),
                            rs.getString("turno"),
                            rs.getInt("cupo"),
                            rs.getDouble("cuota_mensual")
                    );
                }
                return null;
            }

        } catch (SQLException e) {
            throw AlumnoDAO.traducirErrorSQL(e);
        }
    }

    // B.3: Insertar curso
    public void insertar(Curso curso) throws Exception {
        String sql = "INSERT INTO cursos (codigo, idioma, nivel, turno, cupo, cuota_mensual) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conector.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, curso.getCodigo());
            ps.setString(2, curso.getIdioma());
            ps.setString(3, curso.getNivel());
            ps.setString(4, curso.getTurno());
            ps.setInt(5, curso.getCupo());
            ps.setDouble(6, curso.getValorCuota());
            ps.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new Exception("Ya existe un curso con el código " + curso.getCodigo() + ".");
        } catch (SQLException e) {
            throw AlumnoDAO.traducirErrorSQL(e);
        }
    }

    // B.3: Modificar curso
    public boolean modificar(Curso curso) throws Exception {
        String sql = "UPDATE cursos SET idioma = ?, nivel = ?, turno = ?, cupo = ?, cuota_mensual = ? " +
                "WHERE codigo = ?";

        try (Connection conn = Conector.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, curso.getIdioma());
            ps.setString(2, curso.getNivel());
            ps.setString(3, curso.getTurno());
            ps.setInt(4, curso.getCupo());
            ps.setDouble(5, curso.getValorCuota());
            ps.setString(6, curso.getCodigo());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw AlumnoDAO.traducirErrorSQL(e);
        }
    }

    // B.3: Borrar curso por código
    public boolean borrar(String codigo) throws Exception {
        String sql = "DELETE FROM cursos WHERE codigo = ?";

        try (Connection conn = Conector.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codigo);
            return ps.executeUpdate() == 1;

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new Exception("No se puede eliminar el curso porque tiene inscripciones asociadas.");
        } catch (SQLException e) {
            throw AlumnoDAO.traducirErrorSQL(e);
        }
    }
}