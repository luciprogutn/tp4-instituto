import javax.swing.*;
import java.sql.SQLException;
import java.sql.SQLException;

public class VentanaInstituto extends JFrame {

    // 1. Atributos de la ventana (componentes + DAO)
    private CursoDAO cursoDAO = new CursoDAO();
    // Si la ventana también trabaja con alumnos o inscripciones, podés tener acá:
    // private AlumnoDAO alumnoDAO = new AlumnoDAO();
    // private InscripcionDAO inscripcionDAO = new InscripcionDAO();

    public VentanaInstituto() {
        super("Instituto de Idiomas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // ... armás paneles, layouts, botones, tabla ...[cite: 38, 40, 41]

        // 2. Al armar la ventana, podés llamar al método que carga los datos desde la base
        cargarCursos();

        setVisible(true); // siempre al final, cuando ya tiene todo adentro[cite: 39, 42]
    }

    private void cargarCursos() {
        try {
            // 3. Usás el objeto DAO que declaraste arriba
            for (Curso c : cursoDAO.listar()) {
                // ... agregás filas al modelo de la JTable ...[cite: 35, 70]
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los cursos"); //[cite: 70]
        }
    }

    private void guardarAlgo() {
        try {
            // 4. Otro método de la misma ventana también usa cursoDAO[cite: 70]
            //cursoDAO.insertar();
            cargarCursos(); // refrescás la tabla para que coincida con la base
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar"); //[cite: 70]
        }
    }
}
