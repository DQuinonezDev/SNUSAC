# Manual de Usuario — Sancarlista Academy

**Desarrollado por:** Emily González  
**Curso:** Introducción a la Programación y Computación 1  
**Universidad de San Carlos de Guatemala**

---

## Tabla de Contenidos

1. [Introducción](#introducción)
2. [Requisitos del Sistema](#requisitos-del-sistema)
3. [Inicio del Sistema](#inicio-del-sistema)
4. [Inicio de Sesión](#inicio-de-sesión)
5. [Módulo Administrador](#módulo-administrador)
6. [Módulo Instructor](#módulo-instructor)
7. [Módulo Estudiante](#módulo-estudiante)
8. [Cierre de Sesión](#cierre-de-sesión)

---

## Introducción

Sancarlista Academy es un sistema de gestión académica que permite administrar instructores, estudiantes, cursos, inscripciones, calificaciones, reportes y una bitácora de actividad. El sistema tiene tres tipos de usuario: **Administrador**, **Instructor** y **Estudiante**, cada uno con su propio panel de funciones.

---

## Requisitos del Sistema

- Java 11 o superior instalado
- Sistema operativo: Windows, macOS o Linux
- Resolución de pantalla mínima: 1024 x 768

---

## Inicio del Sistema

1. Ejecutar el archivo `.jar` del proyecto o correrlo directamente desde NetBeans.
2. Se abrirá automáticamente la pantalla de inicio de sesión.
3. Los datos del sistema se guardan automáticamente en el archivo `datos.ser` en la carpeta raíz del proyecto.

---

## Inicio de Sesión

Al abrir el sistema se muestra la pantalla de **Login**.

| Campo | Descripción |
|---|---|
| Código | Identificador único del usuario |
| Contraseña | Contraseña del usuario |

**Cuenta de administrador por defecto:**
- Código: `admin`
- Contraseña: `IPC1B`

Después de ingresar los datos correctos, el sistema redirige automáticamente al panel correspondiente según el rol del usuario. Si las credenciales son incorrectas, se muestra un mensaje de error.

---

## Módulo Administrador

El panel del administrador tiene las siguientes pestañas:

### Instructores

Permite gestionar los instructores del sistema.

- **Crear instructor:** Completar los campos Código, Nombre, Fecha de Nacimiento, Género y Contraseña, luego presionar **Crear**.
- **Actualizar instructor:** Seleccionar un instructor de la tabla, modificar los campos deseados y presionar **Actualizar**.
- **Eliminar instructor:** Seleccionar un instructor de la tabla y presionar **Eliminar**.
- **Cargar desde CSV:** Presionar **Cargar CSV** y seleccionar un archivo con el formato: `Codigo,Nombre,FechaNacimiento,Genero,Contrasena` (la primera fila es el encabezado y se omite).

> El código es obligatorio y no puede repetirse.

### Estudiantes

Funciona igual que el módulo de Instructores. Los campos son: Código, Nombre, Fecha de Nacimiento, Género y Contraseña.

### Cursos

Permite gestionar los cursos del sistema.

- **Crear curso:** Completar Código, Nombre, Descripción, Créditos y Sección, luego presionar **Crear Curso**.
- **Actualizar curso:** Seleccionar un curso de la tabla, modificar los campos y presionar **Actualizar Curso**.
- **Eliminar curso:** Seleccionar un curso y presionar **Eliminar Curso**.
- **Asignar instructor:** Seleccionar un curso de la tabla, elegir el instructor del combo y presionar **Asignar Instructor**.
- **Cargar desde CSV:** Formato requerido: `Codigo,Nombre,Descripcion,Creditos,Seccion`.

### Monitoreo (Consola de Hilos)

Al ingresar al panel, se activan automáticamente tres hilos que se muestran en el área de texto inferior:

- **Hilo de Sesiones Activas:** Muestra cuántos usuarios están conectados en tiempo real.
- **Hilo de Inscripciones:** Procesa las inscripciones pendientes de los estudiantes cada 8 segundos y las confirma.
- **Hilo de Estadísticas:** Muestra el total de cursos, estudiantes y calificaciones registradas.

### Reportes

Genera reportes en formato PDF que se guardan en la carpeta del proyecto con el nombre `DD_MM_YYYY_HH_mm_ss_TipoReporte.pdf`.

| Botón | Reporte generado |
|---|---|
| Mejor Desempeño | Top 5 estudiantes con mayor promedio |
| Peor Desempeño | Top 5 estudiantes con menor promedio |
| Secciones por Rendimiento | Secciones ordenadas por promedio de notas |
| Historial Individual | Notas de un estudiante específico (pide el código) |
| Inscripciones por Curso | Cantidad de inscritos por curso |
| Calificación por Sección | Promedio de cada estudiante por sección |

### Bitácora

Registra todas las acciones realizadas en el sistema.

- **Filtrar:** Escribir en los campos **Tipo de Usuario** y/o **Operación** y presionar **Filtrar**. Los filtros se pueden combinar.
- **Limpiar filtros:** Presionar **Limpiar** para ver todos los registros.
- **Exportar CSV:** Presionar **Exportar** y elegir dónde guardar el archivo. El CSV incluye: Fecha, Tipo de Usuario, Código, Operación, Estado y Descripción.

### Información del Desarrollador

Presionar el botón **Información del Desarrollador** para ver los datos del autor del sistema.

---

## Módulo Instructor

El panel del instructor tiene dos pestañas:

### Mis Cursos

Muestra únicamente los cursos que tienen asignado al instructor actual. La tabla indica código, nombre, descripción, créditos y sección.

### Notas

Permite gestionar las calificaciones de los estudiantes en los cursos asignados al instructor.

- **Agregar nota:** Completar los campos Código de Curso, Código de Sección, Código de Estudiante, Ponderación, Nota y Fecha, luego presionar **Agregar Nota**.
- **Actualizar nota:** Seleccionar una nota de la tabla, modificar los campos y presionar **Actualizar Nota**.
- **Eliminar nota:** Seleccionar una nota y presionar **Eliminar Nota**.
- **Filtrar:** Usar los campos de filtro por curso, sección o estudiante y presionar **Filtrar**. Presionar **Limpiar** para ver todas las notas.
- **Cargar CSV:** Formato: `CodigoCurso,CodigoSeccion,CodigoEstudiante,Ponderacion,Nota,FechaRegistro`.
- **Exportar CSV:** Guarda todas las notas del sistema en un archivo CSV.

La tabla muestra el **Promedio Ponderado** y el **Estado** (Aprobado / Reprobado) de cada estudiante en la sección. La nota mínima para aprobar es **61**.

> La ponderación debe ser mayor a 0. La nota debe estar entre 0 y 100. No se permiten notas duplicadas para la misma combinación de curso + sección + estudiante.

---

## Módulo Estudiante

El panel del estudiante tiene tres pestañas:

### Cursos Disponibles

Muestra todos los cursos del sistema con el estado del estudiante en cada uno:

| Estado | Significado |
|---|---|
| Disponible | El estudiante puede inscribirse |
| Pendiente | La inscripción fue enviada pero aún no procesada |
| Inscrito | La inscripción fue confirmada por el sistema |

- **Inscribirse:** Seleccionar un curso con estado **Disponible** y presionar **Inscribirse**. El sistema valida que no exista un choque de sección (no se puede estar inscrito en dos cursos de la misma sección).
- **Desasignar:** Seleccionar un curso con estado **Inscrito** o **Pendiente** y presionar **Desasignar** para cancelar la inscripción.

> Las inscripciones se procesan automáticamente por el hilo de inscripciones cada 8 segundos mientras el administrador esté activo.

### Calificaciones

Muestra todas las notas del estudiante con: Curso, Sección, Ponderación, Nota, Fecha, Promedio Ponderado y Estado (Aprobado/Reprobado).

- **Exportar calificaciones:** Presionar **Exportar calificaciones** para guardar el historial en un archivo CSV. El nombre sugerido es `historial_CODIGO.csv`.

### Perfil

Permite actualizar los datos personales del estudiante.

- Para guardar cambios es obligatorio ingresar la **contraseña actual**.
- Si se deja vacío el campo de contraseña nueva, se conserva la contraseña actual.
- El código del estudiante no es editable.

---

## Cierre de Sesión

Presionar el botón **Cerrar Sesión** en cualquier panel. El sistema guarda automáticamente todos los datos antes de regresar a la pantalla de Login.

---

## Formatos CSV de Importación

### Instructores y Estudiantes
```
Codigo,Nombre,FechaNacimiento,Genero,Contrasena
E001,Juan Perez,2000-01-15,Masculino,pass123
```

### Cursos
```
Codigo,Nombre,Descripcion,Creditos,Seccion
MAT01,Matematica I,Algebra y calculo,4,A
```

### Notas
```
CodigoCurso,CodigoSeccion,CodigoEstudiante,Ponderacion,Nota,FechaRegistro
MAT01,A,E001,25.0,85.0,2024-03-15
```

> La primera fila de cada archivo siempre es el encabezado y es omitida automáticamente. Las filas con datos inválidos se ignoran sin interrumpir la carga.
