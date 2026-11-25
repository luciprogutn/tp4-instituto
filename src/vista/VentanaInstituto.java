package vista;

import dao.AlumnoDAO;
import dao.CursoDAO;
import dao.DeudoresDAO;
import dao.InscripcionDAO;
import modelo.Alumno;
import modelo.Curso;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaInstituto extends JFrame {

    private final CursoDAO cursoDAO = new CursoDAO();
    private final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();
    private final DeudoresDAO deudoresDAO = new DeudoresDAO();

    private DefaultTableModel modeloCursos;
    private JTable tablaCursos;

    private JComboBox<Alumno> comboAlumnos;
    private JComboBox<Curso> comboCursos;

    private DefaultTableModel modeloDeudores;
    private JTable tablaDeudores;

    public VentanaInstituto() {
        setTitle("Instituto de Idiomas - Gestión de Cursos e Inscripciones");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 620);
        setLocationRelativeTo(null);

        inicializarComponentes();
        actualizarTablas();
    }

    private void inicializarComponentes() {
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Cursos e Inscripciones", crearPanelCursosEInscripciones());
        pestanas.addTab("Alta de Alumnos", crearPanelAltaAlumno());
        pestanas.addTab("Deudores", crearPanelDeudores());

        add(pestanas, BorderLayout.CENTER);
    }

    private JPanel crearPanelCursosEInscripciones() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"Código", "Idioma", "Nivel", "Turno", "Cupo", "Inscriptos", "Vacantes", "Cuota"};
        modeloCursos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 4, 5, 6 -> Integer.class;
                    case 7 -> Double.class;
                    default -> String.class;
                };
            }
        };

        tablaCursos = new JTable(modeloCursos);
        tablaCursos.setAutoCreateRowSorter(true);
        panel.add(new JScrollPane(tablaCursos), BorderLayout.CENTER);

        JPanel panelInscribir = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        comboAlumnos = new JComboBox<>();
        comboCursos = new JComboBox<>();
        JButton btnInscribir = new JButton("Inscribir alumno");
        JButton btnActualizar = new JButton("Actualizar");

        btnInscribir.addActionListener(e -> inscribirDesdePantalla());
        btnActualizar.addActionListener(e -> actualizarTablas());

        panelInscribir.add(new JLabel("Alumno:"));
        panelInscribir.add(comboAlumnos);
        panelInscribir.add(new JLabel("Curso:"));
        panelInscribir.add(comboCursos);
        panelInscribir.add(btnInscribir);
        panelInscribir.add(btnActualizar);

        panel.add(panelInscribir, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelAltaAlumno() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtDni = new JTextField();
        JTextField txtCorreo = new JTextField();
        JButton btnGuardar = new JButton("Guardar alumno");

        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String dni = txtDni.getText().trim();
            String correo = txtCorreo.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Completá nombre, apellido y DNI antes de guardar.",
                        "Datos incompletos",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            try {
                Alumno nuevo = new Alumno(0, nombre, apellido, dni, correo.isEmpty() ? null : correo);
                alumnoDAO.insertar(nuevo);
                JOptionPane.showMessageDialog(this, "Alumno registrado correctamente.");
                txtNombre.setText("");
                txtApellido.setText("");
                txtDni.setText("");
                txtCorreo.setText("");
                actualizarTablas();
            } catch (Exception ex) {
                mostrarError(ex);
            }
        });

        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Apellido:"));
        panel.add(txtApellido);
        panel.add(new JLabel("DNI:"));
        panel.add(txtDni);
        panel.add(new JLabel("Correo (opcional):"));
        panel.add(txtCorreo);
        panel.add(new JLabel());
        panel.add(btnGuardar);

        return panel;
    }

    private JPanel crearPanelDeudores() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Coincide con las 5 columnas de tu DeudoresDAO: apellido, nombre, dni, codigo, valor_cuota
        String[] columnas = {"Apellido", "Nombre", "DNI", "Curso", "Cuota adeudada"};
        modeloDeudores = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 4 ? Double.class : String.class;
            }
        };

        tablaDeudores = new JTable(modeloDeudores);
        tablaDeudores.setAutoCreateRowSorter(true);
        panel.add(new JScrollPane(tablaDeudores), BorderLayout.CENTER);

        return panel;
    }

    private void inscribirDesdePantalla() {
        Alumno alumnoSel = (Alumno) comboAlumnos.getSelectedItem();
        Curso cursoSel = (Curso) comboCursos.getSelectedItem();

        if (alumnoSel == null || cursoSel == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccioná un alumno y un curso de la lista.",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            inscripcionDAO.inscribirAlumno(alumnoSel.getId(), cursoSel.getCodigo());
            JOptionPane.showMessageDialog(this, "Inscripción registrada.");
            actualizarTablas();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    public void actualizarTablas() {
        try {
            modeloCursos.setRowCount(0);
            List<Object[]> filasCursos = cursoDAO.listarFilasConVacantes();
            for (Object[] fila : filasCursos) {
                modeloCursos.addRow(fila);
            }

            comboAlumnos.removeAllItems();
            for (Alumno a : alumnoDAO.listar()) {
                comboAlumnos.addItem(a);
            }

            comboCursos.removeAllItems();
            for (Curso c : cursoDAO.listar()) {
                comboCursos.addItem(c);
            }

            modeloDeudores.setRowCount(0);
            for (Object[] filaDeudor : deudoresDAO.listarDeudores()) {
                modeloDeudores.addRow(filaDeudor);
            }
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void mostrarError(Exception ex) {
        String mensaje = (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage()
                : "Ocurrió un error inesperado.";
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.ERROR_MESSAGE);
    }
}