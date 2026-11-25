package vista; // [TP4 Parte C / Manual de Estudio] Paquete de la interfaz; también podés ponerlo en un paquete principal o app.

import javax.swing.SwingUtilities; // [TP4 Parte C / Clase 29 (repaso Unidad 12)] Ejecuta la creación de la ventana en el hilo de eventos de Swing (EDT).

public class Main { // [TP4 / Clase 8] Clase de arranque: coordina el inicio de la aplicación sin mezclar SQL ni lógica.

    public static void main(String[] args) { // [TP4 / Clase 8] Punto de entrada estándar de Java (contexto estático).
        SwingUtilities.invokeLater(() -> { // [TP4 Parte C] Garantiza que los componentes Swing se creen y muestren de forma segura.
            VentanaInstituto ventana = new VentanaInstituto(); // [TP4 Parte C / Clase 8] Instancia la ventana principal (que carga los DAOs y datos iniciales).
            ventana.setVisible(true); // [TP4 Parte C] Hace visible la ventana en pantalla.
        });
    }
}
