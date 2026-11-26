# Trabajo Práctico 4 — Aplicación de Escritorio con Base de Datos (Instituto de Idiomas)

**Materia:** Programación II 
**Profesor:** Cesar Augusto Fernandez Carbonell  
**Integrantes:** [Carrasco Luciana], [Carrasco Joana]

---

## Descripción del proyecto

Aplicación de escritorio desarrollada en Java con interfaz gráfica en **Swing**
(`JFrame` y `JTable`) conectada mediante **JDBC** a una base de datos relacional en **MySQL**. 
Reemplaza las planillas manuales del instituto permitiendo gestionar alumnos, cursos, inscripciones
con control de cupo transaccional y consulta de alumnos deudores.

---

## Cómo crear la base de datos

1. Asegurarse de tener encendido el servidor **MySQL** (por ejemplo, desde MySQL Workbench o 
servicios del sistema).
2. Ejecutar el script `instituto.sql` ubicado en la raíz del repositorio:
    * **Desde MySQL Workbench:** abrir `instituto.sql` (`File > Open SQL Script`) y ejecutar 
    * todo el script.
    * **Desde consola:**
      ```bash
      mysql -u root -p < instituto.sql
      ```
3. El script crea la base de datos `instituto`, las tablas (`alumnos`, `cursos`, `inscripciones`) 
con sus claves primarias, foráneas y restricciones de unicidad, y carga los datos iniciales de
prueba. Puede ejecutarse dos veces seguidas sin errores.

---

## Configuración y cómo ejecutar el programa

1. Copiar el archivo de ejemplo `config.properties.example` en la raíz del proyecto 
y renombrarlo a `config.properties` (este archivo está excluido en `.gitignore` para no subir la
contraseña al repositorio).
2. Completar `config.properties` con los datos reales de conexión a MySQL:
   ```properties
   db.url=jdbc:mysql://localhost:3306/instituto
   db.user=root
   db.password=tu_contraseña
   ```
3. Verificar que el conector **MySQL Connector/J** (`mysql-connector-j-*.jar`) esté agregado
al classpath / dependencias del proyecto.
4. Ejecutar la clase principal que inicia la ventana (`Main` / `VentanaInstituto`) 
desde el IDE (IntelliJ IDEA) o por consola:
   ```bash
   javac -cp ".;mysql-connector-j.jar" -d out src/**/*.java
   java -cp ".;out;mysql-connector-j.jar" Main
   ```

---

## Respuestas a las preguntas del TP4

### 1. ¿Qué pasa si dos personas usan el programa al mismo tiempo y se inscriben al último lugar de un curso?
¿Qué hace su implementación?

La inscripción se ejecuta dentro de una transacción JDBC (`conn.setAutoCommit(false)`) en 
`InscripcionDAO`, verificando las vacantes disponibles antes de insertar el registro.
el motor bloquea la fila involucrada Durante la escritura y garantiza atomicidad: el primer usuario
en confirmar obtiene la ultima vacante y el segundo recibe un error controlado de curso completo,
sin quedar inscripciones a medias ni superar el cupo.

### 2. Muestren una consulta de su programa escrita con `PreparedStatement` y expliquen qué le 
pasaría si la hubieran armado concatenando lo que escribe el usuario.

En `AlumnoDAO.buscarPorId(int id)` utilizamos:
```java
String sql = "SELECT id, dni, nombre, apellido, correo FROM alumnos WHERE id = ?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setInt(1, id);
```
Si la consulta se armara concatenando texto (`"... WHERE nombre = '" + txtNombre + "'"`), 
un usuario que ingrese comillas o comandos SQL (inyección SQL, por ejemplo `' OR '1'='1`) 
rompería la sintaxis de la sentencia o alteraría la consulta original, exponiendo o modificando 
datos no autorizados.

### 3. ¿Dónde decidieron que viva la regla del cupo: en la ventana, en el acceso a datos o en la
base? Justifiquen la decisión.


Decidimos que la regla del cupo viva en el acceso a datos (InscripcionDAO) 
dentro de una transacción, apoyada en las restricciones de la base. Si la controláramos 
solo en la ventana, alguien podría inscribir desde otro lado o pisar el cupo al mismo tiempo 
y el curso se pasaría de gente. Dejándola en el DAO con transacción y en la base nos aseguramos
de que el cupo nunca se pase y la ventana solo muestre datos y pida confirmaciones.

---[WhatsApp Unknown 2026-11-26 at 12.29.16.zip](https://github.com/user-attachments/files/35471566/WhatsApp.Unknown.2026-11-26.at.12.29.16.zip)
