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

    // Nuevos componentes para el listado y baja de alumnos
    private DefaultTableModel modeloAlumnos;
    private JTable tablaAlumnos;

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
        pestanas.addTab("Gestión de Alumnos", crearPanelGestionAlumnos());
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

    private JPanel crearPanelGestionAlumnos() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. NORTE: Formulario de Alta
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 10, 8));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Registrar Nuevo Alumno"));

        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtDni = new JTextField();
        JTextField txtCorreo = new JTextField();
        JButton btnGuardar = new JButton("Guardar alumno");

        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String dniTexto = txtDni.getText().trim();
            String correo = txtCorreo.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || dniTexto.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Completá nombre, apellido y DNI antes de guardar.",
                        "Datos incompletos",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            try {
                int dni = Integer.parseInt(dniTexto);
                Alumno nuevo = new Alumno(dni, nombre, apellido, correo.isEmpty() ? null : correo);
                alumnoDAO.insertar(nuevo);
                JOptionPane.showMessageDialog(this, "Alumno registrado correctamente.");
                txtNombre.setText("");
                txtApellido.setText("");
                txtDni.setText("");
                txtCorreo.setText("");
                actualizarTablas();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "El DNI debe contener solo números.",
                        "DNI inválido",
                        JOptionPane.WARNING_MESSAGE
                );
            } catch (Exception ex) {
                mostrarError(ex);
            }
        });

        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Apellido:"));
        panelFormulario.add(txtApellido);
        panelFormulario.add(new JLabel("DNI:"));
        panelFormulario.add(txtDni);
        panelFormulario.add(new JLabel("Correo (opcional):"));
        panelFormulario.add(txtCorreo);
        panelFormulario.add(new JLabel());
        panelFormulario.add(btnGuardar);

        panelPrincipal.add(panelFormulario, BorderLayout.NORTH);

        // 2. CENTRO: Listado de Alumnos dados de alta
        String[] columnasAlumnos = {"ID", "DNI", "Apellido", "Nombre", "Correo"};
        modeloAlumnos = new DefaultTableModel(columnasAlumnos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 0 || columnIndex == 1) ? Integer.class : String.class;
            }
        };

        tablaAlumnos = new JTable(modeloAlumnos);
        tablaAlumnos.setAutoCreateRowSorter(true);
        tablaAlumnos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollAlumnos = new JScrollPane(tablaAlumnos);
        scrollAlumnos.setBorder(BorderFactory.createTitledBorder("Listado de Alumnos Dados de Alta"));
        panelPrincipal.add(scrollAlumnos, BorderLayout.CENTER);

        // 3. SUR: Botón para dar de baja
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDarDeBaja = new JButton("Dar de baja alumno seleccionado");
        btnDarDeBaja.addActionListener(e -> darDeBajaAlumno());
        panelAcciones.add(btnDarDeBaja);

        panelPrincipal.add(panelAcciones, BorderLayout.SOUTH);

        return panelPrincipal;
    }

    private void darDeBajaAlumno() {
        int filaVista = tablaAlumnos.getSelectedRow();

        // Mensaje de error si intenta dar de baja sin seleccionar un alumno
        if (filaVista == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccioná un alumno de la tabla para darlo de baja.",
                    "Error de selección",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int filaModelo = tablaAlumnos.convertRowIndexToModel(filaVista);
        int idAlumno = (int) modeloAlumnos.getValueAt(filaModelo, 0);
        String apellido = (String) modeloAlumnos.getValueAt(filaModelo, 2);
        String nombre = (String) modeloAlumnos.getValueAt(filaModelo, 3);

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que querés dar de baja a " + nombre + " " + apellido + "?",
                "Confirmar baja",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                // Usamos tu método borrar(id) de AlumnoDAO
                boolean eliminado = alumnoDAO.borrar(idAlumno);
                if (eliminado) {
                    JOptionPane.showMessageDialog(this, "Alumno dado de baja correctamente.");
                    actualizarTablas();
                } else {
                    throw new Exception("No se pudo dar de baja al alumno porque ya no existe en la base de datos.");
                }
            } catch (Exception ex) {
                mostrarError(ex);
            }
        }
    }

    private JPanel crearPanelDeudores() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
            modeloAlumnos.setRowCount(0); // Limpiamos la tabla de alumnos
            for (Alumno a : alumnoDAO.listar()) {
                comboAlumnos.addItem(a);
// Cargamos cada alumno dado de alta en la nueva tabla
                modeloAlumnos.addRow(new Object[]{
                        a.getId(),
                        a.getDni(),
                        a.getApellido(),
                        a.getNombre(),
                        a.getCorreo() != null ? a.getCorreo() : "-"
                });
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
