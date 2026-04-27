# Manual de Usuario — Sancarlista Academy

**Autora:** Emily González  
**Curso:** Introducción a la Programación y Computación 1  
**Universidad de San Carlos de Guatemala**

---

## ¿Qué es Sancarlista Academy?

Es un sistema de gestión académica de escritorio. Permite administrar instructores, estudiantes, cursos, inscripciones y calificaciones. Tiene tres tipos de usuario: **Administrador**, **Instructor** y **Estudiante**.

---

## Cómo iniciar el sistema

1. Ejecutar el proyecto desde NetBeans o el archivo `.jar`.
2. Se abre la pantalla de Login.
3. Los datos se guardan automáticamente en el archivo `datos.ser`.

---

## Inicio de Sesión

Ingresar el **Código** y la **Contraseña** y presionar **Iniciar Sesión**.

El sistema redirige al panel según el rol. Si los datos son incorrectos, muestra un mensaje de error.

**Credenciales por defecto del administrador:**
- Código: `admin`
- Contraseña: `IPC1B`

---

## Panel del Administrador

### Pestaña Instructores

- **Crear:** Llenar Código, Nombre, Fecha de Nacimiento, Género y Contraseña → presionar **Crear**.
- **Actualizar:** Seleccionar instructor en la tabla → modificar campos → presionar **Actualizar**.
- **Eliminar:** Seleccionar instructor en la tabla → presionar **Eliminar**.
- **Cargar CSV:** Presionar **Cargar CSV** y seleccionar el archivo.

Formato del archivo CSV:
```
Codigo,Nombre,FechaNacimiento,Genero,Contrasena
I001,Carlos Lopez,1990-05-10,Masculino,pass123
```

### Pestaña Estudiantes

Funciona igual que Instructores. Los campos son los mismos.

### Pestaña Cursos

- **Crear:** Llenar Código, Nombre, Descripción, Créditos y Sección → **Crear Curso**.
- **Actualizar:** Seleccionar → modificar → **Actualizar Curso**.
- **Eliminar:** Seleccionar → **Eliminar Curso**.
- **Asignar instructor:** Seleccionar curso → elegir instructor del combo → **Asignar Instructor**.
- **Cargar CSV:** Formato: `Codigo,Nombre,Descripcion,Creditos,Seccion`.

### Monitoreo (consola de hilos)

Al entrar al panel se activan tres hilos automáticamente. Sus mensajes aparecen en el área de texto:

| Hilo | Qué hace |
|---|---|
| Sesiones Activas | Muestra cuántos usuarios están conectados |
| Inscripciones | Procesa inscripciones pendientes cada 8 segundos |
| Estadísticas | Muestra totales de cursos, estudiantes y notas |

### Pestaña Reportes

Genera archivos PDF en la carpeta del proyecto. El nombre incluye fecha y hora automáticamente.

| Botón | Contenido del reporte |
|---|---|
| Mejor Desempeño | Top 5 estudiantes con mayor promedio |
| Peor Desempeño | Top 5 estudiantes con menor promedio |
| Secciones por Rendimiento | Secciones ordenadas por promedio |
| Historial Individual | Notas de un estudiante (pide el código) |
| Inscripciones por Curso | Cantidad de inscritos por cada curso |
| Calificación por Sección | Promedios por sección con detalle de estudiantes |

### Pestaña Bitácora

Muestra todas las acciones realizadas en el sistema.

- **Filtrar:** Escribir en los campos Tipo de Usuario y/u Operación → **Filtrar**.
- **Limpiar:** Vuelve a mostrar todos los registros.
- **Exportar:** Guarda la bitácora en un archivo CSV.

### Información del Desarrollador

Presionar el botón para ver los datos del autor del sistema.

---

## Panel del Instructor

### Pestaña Mis Cursos

Muestra solo los cursos que tiene asignados el instructor activo.

### Pestaña Notas

Permite gestionar las calificaciones de los estudiantes.

- **Agregar:** Llenar Código de Curso, Sección, Estudiante, Ponderación, Nota y Fecha → **Agregar Nota**.
- **Actualizar:** Seleccionar nota en la tabla → modificar → **Actualizar Nota**.
- **Eliminar:** Seleccionar nota → **Eliminar Nota**.
- **Filtrar:** Usar los campos de búsqueda → **Filtrar** / **Limpiar**.
- **Cargar CSV:** Formato: `CodigoCurso,CodigoSeccion,CodigoEstudiante,Ponderacion,Nota,FechaRegistro`.
- **Exportar CSV:** Guarda todas las notas en un archivo.

La tabla muestra el **Promedio Ponderado** y el estado **Aprobado** (≥ 61) o **Reprobado** de cada estudiante.

Reglas de validación:
- La ponderación debe ser mayor a 0.
- La nota debe estar entre 0 y 100.
- No se puede registrar dos notas para el mismo curso + sección + estudiante.

---

## Panel del Estudiante

### Pestaña Cursos Disponibles

Muestra todos los cursos con el estado del estudiante en cada uno:

| Estado | Significado |
|---|---|
| Disponible | Puede inscribirse |
| Pendiente | Inscripción enviada, esperando procesamiento |
| Inscrito | Inscripción confirmada |

- **Inscribirse:** Seleccionar un curso con estado Disponible → **Inscribirse**.  
  El sistema bloquea la inscripción si ya existe otro curso con la misma sección (choque de horario).
- **Desasignar:** Seleccionar un curso Inscrito o Pendiente → **Desasignar**.

### Pestaña Calificaciones

Muestra las notas del estudiante con Promedio Ponderado y Estado (Aprobado/Reprobado).

- **Exportar calificaciones:** Guarda el historial en un archivo CSV.

### Pestaña Perfil

Permite actualizar Nombre, Fecha de Nacimiento, Género y Contraseña.

- La contraseña actual es **obligatoria** para guardar cualquier cambio.
- Si no se escribe contraseña nueva, se conserva la actual.
- El código no es editable.

---

## Cierre de Sesión

Presionar **Cerrar Sesión** en cualquier panel. El sistema guarda todos los datos automáticamente antes de regresar al Login.

---

## Notas Generales

- Los campos de código son obligatorios y no pueden repetirse.
- Los archivos CSV deben tener el encabezado en la primera fila (se omite automáticamente).
- Las filas del CSV con datos inválidos se ignoran sin interrumpir la carga.
- Los reportes PDF se guardan en la carpeta desde donde se ejecuta el sistema.
