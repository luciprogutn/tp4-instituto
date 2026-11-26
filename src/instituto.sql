DROP DATABASE IF EXISTS instituto;
CREATE DATABASE instituto;
USE instituto;

CREATE TABLE alumnos (
     id INT AUTO_INCREMENT PRIMARY KEY,
     dni INT NOT NULL UNIQUE,
     nombre VARCHAR(50) NOT NULL,
     apellido VARCHAR(50) NOT NULL,
     correo VARCHAR(80)
);

CREATE TABLE cursos (
        codigo VARCHAR(20) PRIMARY KEY,
        idioma VARCHAR(30) NOT NULL,
        nivel VARCHAR(20) NOT NULL,
        turno VARCHAR(20) NOT NULL,
        cupo INT NOT NULL,
        cuota_mensual DECIMAL(10, 2) NOT NULL,
        valor_cuota DECIMAL(10, 2) GENERATED ALWAYS AS (cuota_mensual) STORED
);

CREATE TABLE inscripciones (
       id INT AUTO_INCREMENT PRIMARY KEY,
       id_alumno INT NOT NULL,
       codigo_curso VARCHAR(20) NOT NULL,
       fecha DATE NOT NULL,
       cuota_paga BOOLEAN NOT NULL DEFAULT FALSE,
       CONSTRAINT uq_alumno_curso UNIQUE (id_alumno, codigo_curso),
       CONSTRAINT fk_inscripcion_alumno FOREIGN KEY (id_alumno) REFERENCES alumnos(id),
       CONSTRAINT fk_inscripcion_curso FOREIGN KEY (codigo_curso) REFERENCES cursos(codigo)
);

-- Inserción de Cursos
INSERT INTO cursos (codigo, idioma, nivel, turno, cupo, cuota_mensual) VALUES
       ('ING-A1', 'Ingles', 'A1', 'Mañana', 2, 25000.00),
       ('POR-B1', 'Portugues', 'B1', 'Tarde', 15, 28000.00),
       ('FRA-A2', 'Frances', 'A2', 'Noche', 10, 30000.00);

-- Inserción de Alumnos (id 1 al 8 en el mismo orden)
INSERT INTO alumnos (dni, nombre, apellido, correo) VALUES
        (41111222, 'Lucia', 'Gomez', 'lucia@gmail.com'),
        (38222333, 'Carlos', 'Perez', NULL),
        (40333444, 'Mariana', 'Lopez', 'mariana@gmail.com'),
        (42444555, 'Juan', 'Martinez', 'juan@gmail.com'),
        (39555666, 'Sofia', 'Ruiz', 'sofia@gmail.com'),
        (43666777, 'Mateo', 'Fernandez', 'mateo@gmail.com'),
        (37777888, 'Camila', 'Diaz', 'camila@gmail.com'),
        (44888999, 'Lucas', 'Torres', 'lucas@gmail.com');

-- Inserción de Inscripciones
INSERT INTO inscripciones (id_alumno, codigo_curso, fecha, cuota_paga) VALUES
       (1, 'ING-A1', '2026-03-01', TRUE),
       (2, 'ING-A1', '2026-03-02', FALSE),
       (3, 'POR-B1', '2026-03-01', TRUE),
       (4, 'POR-B1', '2026-03-03', TRUE),
       (5, 'POR-B1', '2026-03-04', FALSE),
       (6, 'POR-B1', '2026-03-05', TRUE),
       (7, 'FRA-A2', '2026-03-01', TRUE),
       (8, 'FRA-A2', '2026-03-02', FALSE),
       (1, 'FRA-A2', '2026-03-03', TRUE),
       (3, 'FRA-A2', '2026-03-04', FALSE),
       (4, 'FRA-A2', '2026-03-05', TRUE),
       (5, 'FRA-A2', '2026-03-06', TRUE);
