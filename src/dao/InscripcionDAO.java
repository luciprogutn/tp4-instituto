package dao;

import conexion.Conector;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;

public class InscripcionDAO {

    public void inscribirAlumno(int idAlumno, String codigoCurso) throws Exception {
        String sqlCupo = "SELECT c.cupo, COUNT(i.id) AS inscriptos " +
                "FROM cursos c LEFT JOIN inscripciones i ON c.codigo = i.codigo_curso " +
                "WHERE c.codigo = ? GROUP BY c.codigo FOR UPDATE";

        String sqlInsert = "INSERT INTO inscripciones (id_alumno, codigo_curso, fecha, cuota_paga) " +
                "VALUES (?, ?, ?, FALSE)";

        Connection conn = null;
        try {
            conn = Conector.conectar();
            conn.setAutoCommit(false); // Transacción manual (Punto B.6)

            try (PreparedStatement psCupo = conn.prepareStatement(sqlCupo)) {
                psCupo.setString(1, codigoCurso);
                try (ResultSet rs = psCupo.executeQuery()) {
                    if (rs.next()) {
                        int cupo = rs.getInt("cupo");
                        int inscriptos = rs.getInt("inscriptos");

                        if (inscriptos >= cupo) {
                            throw new Exception("El curso " + codigoCurso + " está completo (no quedan vacantes).");
                        }
                    } else {
                        throw new Exception("El curso seleccionado no existe.");
                    }
                }
            }

            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, idAlumno);
                psInsert.setString(2, codigoCurso);
                psInsert.setDate(3, Date.valueOf(LocalDate.now()));
                psInsert.executeUpdate();
            }

            conn.commit(); // Confirma validación + inserción juntas
        } catch (SQLIntegrityConstraintViolationException e) {
            if (conn != null) conn.rollback();
            throw new Exception("El alumno ya se encuentra inscripto en este curso.");
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw AlumnoDAO.traducirErrorSQL(e);
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    // Se evita ocultar la excepción principal
                }
            }
        }
    }
}