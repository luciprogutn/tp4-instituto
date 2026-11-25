package vista; // [TP4 Parte C / Manual de Estudio] Paquete de interfaz gráfica: muestra datos y atrapa errores; no escribe SQL.

import dao.AlumnoDAO; // [TP4 B.3 / Clase 29] DAO de alumnos para dar de alta y cargar el combo de selección.
import dao.CursoDAO; // [TP4 B.3, C.1 / Clase 29] DAO de cursos para llenar la tabla con vacantes y el combo de cursos.
import dao.DeudoresDAO; // [TP4 D.1 / Clase 29] DAO del informe de deudores que consulta directo con JOIN y WHERE cuota_paga = FALSE.
import dao.InscripcionDAO; // [TP4 B.6, C.3, C.4 / Clase 29] DAO de inscripciones para inscribir en transacción y dar de baja.
import modelo.Alumno; // [TP4 B.1, C.3 / Clase 8, 9] Entidad Alumno; se usa en JComboBox<Alumno> para elegir sin escribir IDs a mano.
import modelo.Curso; // [TP4 B.1, C.3 / Clase 8, 9] Entidad Curso; se usa en JComboBox<Curso> para elegir curso sin escribir código a mano.

import javax.swing.*; // [TP4 Parte C / Clase 29 (repaso Unidad 12)] Componentes Swing: JFrame, JTabbedPane, JTable, JComboBox, JOptionPane.
import javax.swing.table.DefaultTableModel; // [TP4 C.1 / Clase 29] Modelo de datos de la JTable para agregar, vaciar y ordenar filas.
import java.awt.*; // [TP4 Parte C / Clase 29 (repaso Unidad 12)] Layouts (BorderLayout, FlowLayout, GridLayout) para ubicar componentes.
import java.util.List; // [TP4 / Clase 29 (repaso Unidad 8)] Lista que devuelven los DAOs en lugar de devolver un ResultSet cerrado.

public class VentanaInstituto extends JFrame { // [TP4 Parte C / Manual de Estudio] Ventana principal de la aplicación de escritorio.

    private final CursoDAO cursoDAO = new CursoDAO(); // [TP4 B.3, C.1 / Clase 8, 29] Instancia única por ventana para pedir cursos a la capa de acceso a datos.
    private final AlumnoDAO alumnoDAO = new AlumnoDAO(); // [TP4 B.3, C.2 / Clase 8, 29] Instancia para registrar alumnos y listarlos en pantalla.
    private final InscripcionDAO inscripcionDAO = new InscripcionDAO(); // [TP4 B.6, C.3, C.4 / Clase 29] Instancia para inscribir y dar de baja inscripciones.
    private final DeudoresDAO deudoresDAO = new DeudoresDAO(); // [TP4 D.1 / Clase 29] Instancia para traer el informe de alumnos con cuota impaga.

    private DefaultTableModel modeloCursos; // [TP4 C.1, C.5 / Clase 29] Guarda las filas de cursos que se muestran y refrescan en la tabla principal.
    private JTable tablaCursos; // [TP4 C.1 / Manual de Estudio] Tabla principal que muestra cupo, inscriptos y vacantes ordenables como números.

    private JComboBox<Alumno> comboAlumnos; // [TP4 C.3 / Manual de Estudio] Selector visual de alumnos para inscribir sin teclear IDs a mano.
    private JComboBox<Curso> comboCursos; // [TP4 C.3 / Manual de Estudio] Selector visual de cursos para inscribir sin teclear códigos a mano.

    private DefaultTableModel modeloDeudores; // [TP4 D.1, C.5 / Clase 29] Guarda las filas del informe de deudores traídas desde la base.
    private JTable tablaDeudores; // [TP4 D.1 / Manual de Estudio] Tabla que muestra los alumnos con cuota impaga, su curso y el importe.

    public VentanaInstituto() { // [TP4 Parte C / Clase 8] Constructor: arma la ventana y carga los datos iniciales al abrir.
        setTitle("Instituto de Idiomas - Gestión de Cursos e Inscripciones"); // [TP4 / Consigna general] Título de la ventana del instituto.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // [TP4 Parte C] Cierra la aplicación al cerrar la ventana principal.
        setSize(900, 600); // [TP4 Parte C] Define el tamaño inicial de la ventana en píxeles.
        setLocationRelativeTo(null); // [TP4 Parte C] Centra la ventana en la pantalla.

        inicializarComponentes(); // [TP4 Parte C] Crea las pestañas, formularios, tablas y botones.
        actualizarDatos(); // [TP4 C.5 / Clase 29] Trae los datos actuales de la base al iniciar para que la vista coincida con la BD.
    }

    private void inicializarComponentes() { // [TP4 Parte C] Agrupa la construcción de las secciones de la ventana.
        JTabbedPane pestanas = new JTabbedPane(); // [TP4 Parte C] Organiza la ventana en pestañas para separar tareas con claridad.
        pestanas.addTab("Cursos e Inscripciones", crearPanelCursosEInscripciones()); // [TP4 C.1, C.3] Pestaña de cursos, vacantes e inscripción.
        pestanas.addTab("Alta de Alumnos", crearPanelAltaAlumno()); // [TP4 C.2] Pestaña con el formulario de alta y validación de alumnos.
        pestanas.addTab("Deudores", crearPanelDeudores()); // [TP4 D.1] Pestaña para el informe de alumnos con cuota impaga.

        add(pestanas, BorderLayout.CENTER); // [TP4 Parte C] Coloca el panel de pestañas ocupando el centro de la ventana.
    }

    private JPanel crearPanelCursosEInscripciones() { // [TP4 C.1, C.3] Construye la vista de cursos con vacantes y el bloque de inscripción.
        JPanel panel = new JPanel(new BorderLayout(10, 10)); // [TP4 Parte C] Contenedor principal con separación de 10 px entre zonas.
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // [TP4 Parte C] Margen interior alrededor del panel.

        String[] columnas = {"Código", "Idioma", "Nivel", "Turno", "Cupo", "Inscriptos", "Vacantes", "Cuota"}; // [TP4 C.1 / El enunciado] Encabezados de la tabla principal.
        modeloCursos = new DefaultTableModel(columnas, 0) { // [TP4 C.1 / Clase 29] Modelo vacío (0 filas iniciales) con las columnas definidas.
            @Override // [TP4 C.1] Sobrescribe comportamiento del modelo de tabla.
            public boolean isCellEditable(int row, int column) { // [TP4 C.1] Evita que el usuario edite celdas de la tabla a mano directamente.
                return false; // [TP4 C.1] Todas las celdas quedan de solo lectura desde la grilla.
            }

            @Override // [TP4 C.1 / Manual de Estudio] Indica el tipo real de cada columna para que el ordenamiento sea numérico.
            public Class<?> getColumnClass(int columnIndex) { // [TP4 C.1 / Manual de Estudio] Sin esto, JTable ordena números como texto ("10" antes que "2").
                return switch (columnIndex) { // [TP4 C.1] Selecciona la clase según el índice de columna.
                    case 4, 5, 6 -> Integer.class; // [TP4 C.1 / Manual de Estudio] Cupo, inscriptos y vacantes se ordenan como enteros.
                    case 7 -> Double.class; // [TP4 C.1 / Manual de Estudio] Cuota se ordena como número decimal.
                    default -> String.class; // [TP4 C.1] El resto de las columnas se ordenan como texto.
                };
            }
        };

        tablaCursos = new JTable(modeloCursos); // [TP4 C.1 / Clase 29] Crea la tabla visual vinculada a modeloCursos.
        tablaCursos.setAutoCreateRowSorter(true); // [TP4 C.1 / Manual de Estudio] Habilita ordenar por cualquier columna haciendo clic en su encabezado.
        panel.add(new JScrollPane(tablaCursos), BorderLayout.CENTER); // [TP4 C.1] Agrega barras de desplazamiento y coloca la tabla al centro.

        JPanel panelInscribir = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); // [TP4 C.3] Barra inferior para elegir alumno y curso e inscribir.
        comboAlumnos = new JComboBox<>(); // [TP4 C.3 / Manual de Estudio] Lista desplegable de objetos Alumno (no campo de texto manual).
        comboCursos = new JComboBox<>(); // [TP4 C.3 / Manual de Estudio] Lista desplegable de objetos Curso (no campo de texto manual).
        JButton btnInscribir = new JButton("Inscribir alumno"); // [TP4 C.3] Botón que dispara la inscripción del alumno y curso seleccionados.
        JButton btnActualizar = new JButton("Actualizar"); // [TP4 C.5] Botón para forzar recarga manual desde la base si se desea.

        btnInscribir.addActionListener(e -> inscribirDesdePantalla()); // [TP4 C.3 / Clase 29] Conecta el clic del botón con el método de inscripción.
        btnActualizar.addActionListener(e -> actualizarDatos()); // [TP4 C.5 / Clase 29] Conecta el clic con la recarga de datos desde los DAOs.

        panelInscribir.add(new JLabel("Alumno:")); // [TP4 C.3] Etiqueta para el combo de alumnos.
        panelInscribir.add(comboAlumnos); // [TP4 C.3] Agrega el combo de alumnos a la barra.
        panelInscribir.add(new JLabel("Curso:")); // [TP4 C.3] Etiqueta para el combo de cursos.
        panelInscribir.add(comboCursos); // [TP4 C.3] Agrega el combo de cursos a la barra.
        panelInscribir.add(btnInscribir); // [TP4 C.3] Agrega el botón de inscribir.
        panelInscribir.add(btnActualizar); // [TP4 C.5] Agrega el botón de actualizar.

        panel.add(panelInscribir, BorderLayout.SOUTH); // [TP4 C.3] Ubica la barra de inscripción en la parte inferior del panel.
        return panel; // [TP4 C.1, C.3] Devuelve el panel armado a la pestaña.
    }

    private JPanel crearPanelAltaAlumno() { // [TP4 C.2 / Manual de Estudio] Construye el formulario de alta de alumnos con validación previa.
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10)); // [TP4 C.2] Grilla de 5 filas por 2 columnas para etiquetas, campos y botón.
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // [TP4 C.2] Margen interior del formulario.

        JTextField txtNombre = new JTextField(); // [TP4 C.2 / El enunciado] Campo de texto para el nombre del alumno.
        JTextField txtApellido = new JTextField(); // [TP4 C.2 / El enunciado] Campo de texto para el apellido del alumno.
        JTextField txtDni = new JTextField(); // [TP4 C.2 / El enunciado] Campo de texto para el DNI único del alumno.
        JTextField txtCorreo = new JTextField(); // [TP4 C.2 / El enunciado] Campo de texto para el correo (puede quedar vacío -> NULL en BD).
        JButton btnGuardar = new JButton("Guardar alumno"); // [TP4 C.2] Botón para validar y guardar el nuevo alumno.

        btnGuardar.addActionListener(e -> { // [TP4 C.2 / Clase 29] Ejecuta la validación y el alta al pulsar "Guardar alumno".
            String nombre = txtNombre.getText().trim(); // [TP4 C.2] Lee el nombre y quita espacios extremos.
            String apellido = txtApellido.getText().trim(); // [TP4 C.2] Lee el apellido y quita espacios extremos.
            String dni = txtDni.getText().trim(); // [TP4 C.2] Lee el DNI y quita espacios extremos.
            String correo = txtCorreo.getText().trim(); // [TP4 C.2] Lee el correo y quita espacios extremos.

            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty()) { // [TP4 C.2 / Manual de Estudio] Valida campos obligatorios antes de ir a la BD.
                JOptionPane.showMessageDialog( // [TP4 C.2] Avisa con un diálogo si faltan datos obligatorios.
                        this, // [TP4 C.2] Ventana padre sobre la que se muestra el diálogo.
                        "Completá nombre, apellido y DNI antes de guardar.", // [TP4 C.2, D.2] Mensaje claro en español.
                        "Datos incompletos", // [TP4 C.2] Título del cuadro de diálogo.
                        JOptionPane.WARNING_MESSAGE // [TP4 C.2] Ícono de advertencia.
                );
                return; // [TP4 C.2] Corta acá para no llamar a la base con datos incompletos.
            }

            try { // [TP4 D.2 / Clase 29] Atrapa errores de base o restricciones (como DNI repetido o base apagada).
                Alumno nuevo = new Alumno(0, nombre, apellido, dni, correo.isEmpty() ? null : correo); // [TP4 B.1, C.2 / Manual de Estudio] Crea el Alumno; si correo está vacío pasa null.
                alumnoDAO.insertar(nuevo); // [TP4 B.3, C.2 / Clase 29] Envía el alumno a AlumnoDAO para guardarlo con PreparedStatement.
                JOptionPane.showMessageDialog(this, "Alumno registrado correctamente."); // [TP4 C.2] Confirma al usuario con un diálogo.
                txtNombre.setText(""); // [TP4 C.2] Limpia el campo nombre tras guardar.
                txtApellido.setText(""); // [TP4 C.2] Limpia el campo apellido tras guardar.
                txtDni.setText(""); // [TP4 C.2] Limpia el campo DNI tras guardar.
                txtCorreo.setText(""); // [TP4 C.2] Limpia el campo correo tras guardar.
                actualizarDatos(); // [TP4 C.5 / Clase 29] Actualiza combos y tablas para reflejar el nuevo estado en la base.
            } catch (Exception ex) { // [TP4 D.2, D.3 / Clase 29] Captura cualquier error sin mostrar rastro de pila ni cerrar la app.
                mostrarError(ex); // [TP4 D.2, D.3] Muestra el mensaje traducido en un diálogo comprensible.
            }
        });

        panel.add(new JLabel("Nombre:")); // [TP4 C.2] Etiqueta de nombre.
        panel.add(txtNombre); // [TP4 C.2] Campo de nombre.
        panel.add(new JLabel("Apellido:")); // [TP4 C.2] Etiqueta de apellido.
        panel.add(txtApellido); // [TP4 C.2] Campo de apellido.
        panel.add(new JLabel("DNI:")); // [TP4 C.2] Etiqueta de DNI.
        panel.add(txtDni); // [TP4 C.2] Campo de DNI.
        panel.add(new JLabel("Correo (opcional):")); // [TP4 C.2 / El enunciado] Indica que el correo puede faltar.
        panel.add(txtCorreo); // [TP4 C.2] Campo de correo.
        panel.add(new JLabel()); // [TP4 C.2] Celda vacía para alinear el botón a la columna derecha.
        panel.add(btnGuardar); // [TP4 C.2] Botón guardar.

        return panel; // [TP4 C.2] Devuelve el panel de alta de alumnos.
    }

    private JPanel crearPanelDeudores() { // [TP4 D.1 / Manual de Estudio] Construye la pestaña del informe de alumnos con cuota impaga.
        JPanel panel = new JPanel(new BorderLayout(10, 10)); // [TP4 D.1] Contenedor con layout de bordes.
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // [TP4 D.1] Margen interior del panel.

        String[] columnas = {"Nombre", "Apellido", "DNI", "Curso", "Cuota adeudada"}; // [TP4 D.1 / El enunciado] Columnas pedidas: alumno, curso e importe.
        modeloDeudores = new DefaultTableModel(columnas, 0) { // [TP4 D.1 / Clase 29] Modelo vacío para cargar los deudores desde la base.
            @Override // [TP4 D.1] Sobrescribe edición de celdas.
            public boolean isCellEditable(int row, int column) { // [TP4 D.1] Impide editar los datos del informe en la grilla.
                return false; // [TP4 D.1] Informe de solo lectura.
            }

            @Override // [TP4 C.1, D.1 / Manual de Estudio] Define tipo numérico para el importe de la cuota.
            public Class<?> getColumnClass(int columnIndex) { // [TP4 C.1, D.1] Permite ordenar el importe como número decimal.
                return columnIndex == 4 ? Double.class : String.class; // [TP4 C.1, D.1] Columna 4 es Double; las demás son String.
            }
        };

        tablaDeudores = new JTable(modeloDeudores); // [TP4 D.1] Tabla visual de deudores.
        tablaDeudores.setAutoCreateRowSorter(true); // [TP4 C.1, D.1] Permite ordenar el informe por cualquier columna.
        panel.add(new JScrollPane(tablaDeudores), BorderLayout.CENTER); // [TP4 D.1] Agrega scroll y ubica la tabla al centro.

        return panel; // [TP4 D.1] Devuelve el panel del informe de deudores.
    }

    private void inscribirDesdePantalla() { // [TP4 C.3, B.6 / Clase 29] Toma lo elegido en los combos e invoca la inscripción transaccional.
        Alumno alumnoSel = (Alumno) comboAlumnos.getSelectedItem(); // [TP4 C.3 / Manual de Estudio] Obtiene el objeto Alumno elegido en pantalla.
        Curso cursoSel = (Curso) comboCursos.getSelectedItem(); // [TP4 C.3 / Manual de Estudio] Obtiene el objeto Curso elegido en pantalla.

        if (alumnoSel == null || cursoSel == null) { // [TP4 C.3] Guarda por si no hay selección disponible.
            JOptionPane.showMessageDialog( // [TP4 C.3, D.2] Avisa si falta elegir alumno o curso.
                    this, // [TP4 C.3] Ventana padre.
                    "Seleccioná un alumno y un curso de la lista.", // [TP4 C.3, D.2] Mensaje claro en español.
                    "Selección requerida", // [TP4 C.3] Título del diálogo.
                    JOptionPane.WARNING_MESSAGE // [TP4 C.3] Ícono de advertencia.
            );
            return; // [TP4 C.3] Sale sin intentar inscribir.
        }

        try { // [TP4 D.2, D.3 / Clase 29] Atrapa fallos de cupo lleno, alumno ya inscripto o base apagada.
            inscripcionDAO.inscribirAlumno(alumnoSel.getId(), cursoSel.getCodigo()); // [TP4 B.6, C.3 / Manual de Estudio] Ejecuta la transacción en el DAO.
            JOptionPane.showMessageDialog(this, "Inscripción registrada."); // [TP4 C.3] Confirma que se guardó la inscripción.
            actualizarDatos(); // [TP4 C.5 / Clase 29] Refresca tabla de cursos (vacantes/inscriptos), combos y deudores desde la BD.
        } catch (Exception ex) { // [TP4 D.2, D.3 / Clase 29] No deja caer la app ni muestra stack trace.
            mostrarError(ex); // [TP4 D.2, D.3] Muestra el mensaje entendible al usuario.
        }
    }

    private void actualizarDatos() { // [TP4 C.5, D.1 / Clase 29] Recarga toda la interfaz desde la base después de cada cambio.
        try { // [TP4 D.2 / Clase 29] Atrapa si falla la lectura (por ejemplo, base apagada).
            modeloCursos.setRowCount(0); // [TP4 C.5 / Clase 29, Manual de Estudio] Vacía la tabla antes de recargar para no duplicar filas.
            List<Object[]> filasCursos = cursoDAO.listarFilasConVacantes(); // [TP4 C.1, B.3 / Clase 29] Consulta cursos, inscriptos y vacantes calculadas en SQL.
            for (Object[] fila : filasCursos) { // [TP4 C.1 / Clase 29] Recorre cada fila devuelta por el DAO.
                modeloCursos.addRow(fila); // [TP4 C.1 / Clase 29] Agrega la fila al modelo de la tabla principal.
            }

            comboAlumnos.removeAllItems(); // [TP4 C.3, C.5] Limpia el combo de alumnos antes de volver a cargarlo.
            for (Alumno a : alumnoDAO.listar()) { // [TP4 C.3, B.3 / Clase 29] Trae la lista actualizada de alumnos desde la BD.
                comboAlumnos.addItem(a); // [TP4 C.3 / Manual de Estudio] Carga cada Alumno en el combo usando su toString().
            }

            comboCursos.removeAllItems(); // [TP4 C.3, C.5] Limpia el combo de cursos antes de volver a cargarlo.
            for (Curso c : cursoDAO.listar()) { // [TP4 C.3, B.3 / Clase 29] Trae la lista actualizada de cursos desde la BD.
                comboCursos.addItem(c); // [TP4 C.3 / Manual de Estudio] Carga cada Curso en el combo usando su toString().
            }

            modeloDeudores.setRowCount(0); // [TP4 C.5, D.1 / Clase 29] Limpia el informe de deudores antes de llenarlo de nuevo.
            for (Object[] filaDeudor : deudoresDAO.listarDeudores()) { // [TP4 D.1 / Clase 29] Trae directo de SQL los impagos (no filtra en Java).
                modeloDeudores.addRow(filaDeudor); // [TP4 D.1 / Clase 29] Agrega cada deudor al modelo de la tabla.
            }
        } catch (Exception ex) { // [TP4 D.2, D.3 / Clase 29] Evita cierres inesperados o rastros de pila en consola.
            mostrarError(ex); // [TP4 D.2, D.3] Muestra un mensaje claro al usuario.
        }
    }

    private void mostrarError(Exception ex) { // [TP4 D.2, D.3 / Clase 29, Manual de Estudio] Centraliza la presentación de errores amigables.
        String mensaje = (ex.getMessage() != null && !ex.getMessage().isBlank()) // [TP4 D.2] Revisa si la excepción ya trae un mensaje claro.
                ? ex.getMessage() // [TP4 D.2, D.3] Usa el mensaje traducido (DNI repetido, curso completo, ya inscripto, base apagada).
                : "Ocurrió un error inesperado."; // [TP4 D.2] Respaldo genérico en palabras simples si el mensaje viniera vacío.
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.ERROR_MESSAGE); // [TP4 D.2 / Clase 29] Muestra el diálogo de error sin printStackTrace().
    }

    public static void main(String[] args) { // [TP4 / Clase 8] Punto de entrada estándar de la aplicación Java.
        SwingUtilities.invokeLater(() -> new VentanaInstituto().setVisible(true)); // [TP4 Parte C] Inicia la interfaz Swing en el hilo de eventos (EDT) y la hace visible.
    }
}