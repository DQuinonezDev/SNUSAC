# Manual Técnico — Sancarlista Academy

**Desarrollado por:** Emily González  
**Curso:** Introducción a la Programación y Computación 1  
**Universidad de San Carlos de Guatemala**

---

## Tabla de Contenidos

1. [Descripción General](#descripción-general)
2. [Tecnologías Utilizadas](#tecnologías-utilizadas)
3. [Arquitectura del Sistema](#arquitectura-del-sistema)
4. [Estructura de Paquetes](#estructura-de-paquetes)
5. [Capa Modelo](#capa-modelo)
6. [Capa Controlador](#capa-controlador)
7. [Capa Vista](#capa-vista)
8. [Hilos del Sistema](#hilos-del-sistema)
9. [Persistencia y Serialización](#persistencia-y-serialización)
10. [Bitácora](#bitácora)
11. [Generación de Reportes PDF](#generación-de-reportes-pdf)
12. [Manejo de CSV](#manejo-de-csv)
13. [Capacidades de los Arreglos](#capacidades-de-los-arreglos)
14. [Flujo de Autenticación](#flujo-de-autenticación)
15. [Flujo de Inscripción](#flujo-de-inscripción)

---

## Descripción General

Sancarlista Academy es una aplicación de escritorio desarrollada en Java con interfaz gráfica Swing. Gestiona usuarios (administradores, instructores y estudiantes), cursos, inscripciones, calificaciones, reportes PDF y una bitácora de actividad. La arquitectura sigue el patrón MVC y utiliza arreglos estáticos como estructura de datos principal, sin colecciones del API de Java (ArrayList, HashMap, etc.).

---

## Tecnologías Utilizadas

| Tecnología | Versión | Uso |
|---|---|---|
| Java SE | 11+ | Lenguaje principal |
| Java Swing | JDK incluido | Interfaz gráfica |
| NetBeans IDE | 17+ | Entorno de desarrollo y diseñador de formularios |
| Apache PDFBox | 2.0.32 | Generación de reportes en PDF |
| Java Serialization | JDK incluido | Persistencia de datos en archivo `.ser` |
| Maven | 3.x | Gestión de dependencias |

### Dependencia Maven (pom.xml)

```xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>2.0.32</version>
</dependency>
```

---

## Arquitectura del Sistema

El sistema implementa el patrón **MVC (Modelo-Vista-Controlador)**:

```
┌─────────────────────────────────────────────────┐
│                    VISTA                        │
│  PanelLogin  PanelAdministrador                 │
│  PanelInstructor  PanelEstudiante               │
│  VentanaPrincipal (CardLayout)                  │
└────────────────────┬────────────────────────────┘
                     │ llama a
┌────────────────────▼────────────────────────────┐
│                 CONTROLADOR                     │
│  AppController        ControladorLogin          │
│  ControladorUsuarios  ControladorCursos         │
│  ControladorNotas     ControladorBitacora       │
│  ControladorReportes  ControladorSerializacion  │
└────────────────────┬────────────────────────────┘
                     │ lee y escribe
┌────────────────────▼────────────────────────────┐
│                   MODELO                        │
│  Listas (objeto raíz con todos los arreglos)    │
│  Usuario  Administrador  Instructor  Estudiante │
│  Curso  Nota  Inscripcion  Bitacora             │
│  HiloSesionesActivas  HiloInscripciones         │
│  HiloEstadisticas                               │
└─────────────────────────────────────────────────┘
```

La navegación entre vistas se realiza mediante **CardLayout** en `VentanaPrincipal`, que intercambia los paneles según el rol del usuario autenticado.

---

## Estructura de Paquetes

```
src/main/java/
├── com.mycompany.proyecto_2_emily_gonzalez/
│   └── Proyecto_2_emily_gonzalez.java   (main)
├── modelo/
│   ├── Listas.java
│   ├── Usuario.java
│   ├── Administrador.java
│   ├── Instructor.java
│   ├── Estudiante.java
│   ├── Curso.java
│   ├── Nota.java
│   ├── Inscripcion.java
│   ├── Bitacora.java
│   ├── HiloSesionesActivas.java
│   ├── HiloInscripciones.java
│   └── HiloEstadisticas.java
├── controlador/
│   ├── AppController.java
│   ├── ControladorLogin.java
│   ├── ControladorUsuarios.java
│   ├── ControladorCursos.java
│   ├── ControladorNotas.java
│   ├── ControladorBitacora.java
│   ├── ControladorReportes.java
│   └── ControladorSerializacion.java
└── vista/
    ├── VentanaPrincipal.java
    ├── PanelLogin.java
    ├── PanelAdministrador.java
    ├── PanelInstructor.java
    └── PanelEstudiante.java
```

---

## Capa Modelo

### Listas.java

Clase raíz del sistema. Contiene todos los arreglos de datos y sus contadores. Implementa `Serializable` para persistencia completa con una sola operación.

| Arreglo | Tamaño | Contenido |
|---|---|---|
| `administradores[]` | 20 | Objetos Administrador |
| `instructores[]` | 100 | Objetos Instructor |
| `estudiantes[]` | 500 | Objetos Estudiante |
| `cursos[]` | 200 | Objetos Curso |
| `notas[]` | 2000 | Objetos Nota |
| `bitacora[]` | 3000 | Objetos Bitacora |
| `inscripcionesPendientes[]` | 500 | Cola de inscripciones |
| `inscripcionesConfirmadas[]` | 500 | Inscripciones procesadas |

El constructor crea automáticamente un administrador por defecto: código `admin`, contraseña `IPC1B`.

### Jerarquía de Usuarios

```
Usuario (abstracto)
├── Administrador
├── Instructor
└── Estudiante
```

**Usuario** — atributos base: `codigo`, `nombre`, `fechaNacimiento`, `genero`, `contrasena`, `rol`.

**Estudiante** — extiende Usuario, agrega: `cantidadCursosInscritos`.

**Instructor** e **Administrador** — extienden Usuario sin atributos adicionales.

### Curso.java

Atributos: `codigo`, `nombre`, `descripcion`, `creditos` (int), `seccion`, `codigoInstructor`.  
El instructor se asigna como string vacío por defecto y se actualiza con `setCodigoInstructor()`.

### Nota.java

Atributos: `codigoCurso`, `codigoSeccion`, `codigoEstudiante`, `ponderacion` (double), `nota` (double), `fechaRegistro`.  
La combinación `codigoCurso + codigoSeccion + codigoEstudiante` actúa como clave única.

### Inscripcion.java

Atributos: `codigo` (código del estudiante), `nombre` (nombre del estudiante), `codigoCurso`.  
Se usa tanto para la cola de pendientes como para el arreglo de confirmadas.

### Bitacora.java

Atributos: `fecha`, `tipoUsuario`, `codigoUsuario`, `operacion`, `estado`, `descripcion`.  
La fecha se asigna automáticamente en el momento del registro.

---

## Capa Controlador

### AppController.java

Controlador principal. Mantiene referencia a `Listas`, al `usuarioActual` y al contador de `usuariosActivos`.

| Método | Descripción |
|---|---|
| `login(codigo, contrasena)` | Delega a ControladorLogin, guarda usuarioActual |
| `procesarInscripcion()` | Extrae la primera inscripción de la cola y la mueve a confirmadas |
| `estaInscrito(estudiante, curso)` | Busca en inscripcionesConfirmadas |
| `estaInscritoPendiente(estudiante, curso)` | Busca en inscripcionesPendientes |
| `tieneChoqueSeccion(estudiante, seccion)` | Verifica si hay otra inscripción (confirmada o pendiente) con la misma sección |
| `eliminarInscripcion(estudiante, curso)` | Elimina de confirmadas o pendientes desplazando el arreglo |
| `guardarSistema()` | Llama a ControladorSerializacion |

### ControladorLogin.java

Método estático `iniciarSesion(lista, codigo, contrasena)`. Recorre administradores, instructores y estudiantes buscando coincidencia de código y contraseña. Devuelve el objeto `Usuario` encontrado o `null`.

### ControladorUsuarios.java

Gestiona instructores y estudiantes. Valida código no vacío y no duplicado antes de agregar. Los métodos `cargarInstructoresCSV()` y `cargarEstudiantesCSV()` usan `BufferedReader` y delegan la inserción a `agregarInstructor()` / `agregarEstudiante()` con `synchronized`.

### ControladorCursos.java

Gestiona cursos. `agregarCurso()` valida código no vacío y no duplicado. `asignarInstructor(codigoCurso, codigoInstructor)` actualiza el campo `codigoInstructor` del curso encontrado.

### ControladorNotas.java

Gestiona calificaciones. Validaciones en `agregarNota()`:
- Ponderación > 0
- Nota entre 0 y 100
- El estudiante debe existir en el sistema
- No duplicado (curso + sección + estudiante)

`calcularPromedio(codigoEstudiante, codigoSeccion)` usa la fórmula:

```
promedio = Σ(nota × ponderacion) / Σ(ponderacion)
```

Devuelve `-1` si no hay notas registradas para esa combinación.

### ControladorBitacora.java

Métodos estáticos:

| Método | Descripción |
|---|---|
| `registrar(lista, tipoUsuario, codigoUsuario, operacion, estado, descripcion)` | Agrega un evento al arreglo bitacora |
| `filtrar(lista, filtroTipo, filtroOperacion)` | Filtra por tipoUsuario (contains, insensible a mayúsculas) y operacion (contains) |
| `exportarCSV(lista, rutaArchivo)` | Escribe todos los eventos en CSV con campos entre comillas |

### ControladorReportes.java

Métodos estáticos que generan PDFs con Apache PDFBox 2.0.32. Todos los reportes:
1. Crean un arreglo `String[] lineas` de tamaño `MAX_LINEAS` (3000).
2. Llenan las líneas con datos formateados.
3. Llaman a `generarPDF(lineas, cantLineas, tipo)` que escribe el archivo y devuelve el nombre.

El nombre del archivo sigue el patrón: `DD_MM_YYYY_HH_mm_ss_TipoReporte.pdf`.

Las líneas con prefijo `##` se renderizan en **HELVETICA_BOLD** tamaño 13. Las demás en HELVETICA tamaño 10. La paginación es automática cuando `y < MARGEN + 15`.

El método `limpiar(String)` reemplaza caracteres acentuados (á→a, é→e, ñ→n, etc.) para evitar errores de `WinAnsiEncoding` de PDFBox.

### ControladorSerializacion.java

Guarda y carga el objeto `Listas` completo en el archivo `datos.ser` usando `ObjectOutputStream` / `ObjectInputStream`. Si el archivo no existe al cargar, devuelve un `new Listas()` con el administrador por defecto.

---

## Capa Vista

### VentanaPrincipal.java

Frame principal que contiene un `JPanel` con `CardLayout`. Instancia y registra los cuatro paneles:

```
"Login"          → PanelLogin
"Administrador"  → PanelAdministrador
"Instructor"     → PanelInstructor
"Estudiante"     → PanelEstudiante
```

`cambiarVista(String)` llama al método de inicialización correspondiente del panel destino (`iniciarHilos()` para Admin e Instructor, `inicializar()` para Estudiante).

`cerrarSesion()` decrementa usuariosActivos, guarda el sistema y vuelve a Login.

### PanelLogin.java

Captura código y contraseña. Al autenticar exitosamente registra el evento `INICIO_SESION / EXITO` en la bitácora y redirige según el rol. Los intentos fallidos registran `INICIO_SESION / ERROR`.

### PanelAdministrador.java

El panel más completo. Usa `DefaultTableModel` con `isCellEditable = false` para las cuatro tablas (instructores, estudiantes, cursos, bitácora). Los botones de bitácora y reportes se conectan en `configurarBotonesBitacora()` y `configurarBotonesReportes()` desde el constructor, ya que NetBeans no los conecta automáticamente al no estar ligados a eventos del form editor.

Los hilos se inician solo una vez gracias al flag `hilosIniciados`. `recargarTablaBitacora()` se llama en `iniciarHilos()` para mostrar los eventos existentes al entrar al panel.

### PanelInstructor.java

Muestra solo los cursos donde `codigoInstructor` coincide con el instructor en sesión. El CRUD de notas valida que el curso pertenezca al instructor antes de operar.

### PanelEstudiante.java

La tabla de cursos muestra el estado dinámico (Disponible / Pendiente / Inscrito) consultando `estaInscrito()` y `estaInscritoPendiente()` en cada recarga. `inscribirse()` valida tres condiciones antes de encolar: ya inscrito confirmado, ya inscrito pendiente, y choque de sección.

---

## Hilos del Sistema

Se crean e inician desde `PanelAdministrador.iniciarHilos()`. Todos implementan `Runnable` y actualizan la interfaz con `SwingUtilities.invokeLater()`.

| Clase | Intervalo | Función |
|---|---|---|
| `HiloSesionesActivas` | 5 segundos | Muestra `usuariosActivos` en areaConsola |
| `HiloInscripciones` | 8 segundos | Llama a `procesarInscripcion()`, mueve una inscripción de pendientes a confirmadas |
| `HiloEstadisticas` | 10 segundos | Muestra cantidadCursos, cantidadEstudiantes y cantidadNotas |

Cada hilo tiene un método `detener()` que pone `activo = false` y se complementa con `Thread.interrupt()` para salir del `Thread.sleep()`.

---

## Persistencia y Serialización

Todas las clases del modelo implementan `java.io.Serializable`. El objeto `Listas` actúa como raíz del grafo de objetos; al serializar `Listas` se serializa todo el estado del sistema en un único archivo `datos.ser`.

```
Guardar: guardarSistema() → ControladorSerializacion.guardarListas(lista)
Cargar:  al inicio → ControladorSerializacion.cargarListas()
```

`guardarSistema()` se llama automáticamente después de cada operación de escritura (crear, actualizar, eliminar, inscribir, desasignar, actualizar perfil, cerrar sesión).

---

## Bitácora

Se registra un evento en cada operación relevante del sistema. Estructura del evento:

| Campo | Tipo | Ejemplo |
|---|---|---|
| fecha | String (timestamp) | `2024-03-15 10:30:45` |
| tipoUsuario | String | `ADMINISTRADOR` |
| codigoUsuario | String | `admin` |
| operacion | String | `CREAR_INSTRUCTOR` |
| estado | String | `EXITO` o `ERROR` |
| descripcion | String | `Instructor creado: E001` |

Operaciones registradas: `INICIO_SESION`, `CREAR_INSTRUCTOR`, `ACTUALIZAR_INSTRUCTOR`, `ELIMINAR_INSTRUCTOR`, `CREAR_ESTUDIANTE`, `ACTUALIZAR_ESTUDIANTE`, `ELIMINAR_ESTUDIANTE`, `CREAR_CURSO`, `ACTUALIZAR_CURSO`, `ELIMINAR_CURSO`, `ASIGNAR_INSTRUCTOR`.

---

## Generación de Reportes PDF

Los 6 reportes disponibles y su fuente de datos:

| Reporte | Fuente | Ordenamiento |
|---|---|---|
| Mejor Desempeño | `lista.notas` agrupado por estudiante | Burbuja descendente por promedio |
| Peor Desempeño | `lista.notas` agrupado por estudiante | Burbuja ascendente por promedio |
| Secciones por Rendimiento | `lista.notas` agrupado por sección | Burbuja descendente por promedio |
| Historial Individual | `lista.notas` filtrado por código de estudiante | Sin ordenamiento |
| Inscripciones por Curso | `lista.inscripcionesConfirmadas` contadas por curso | Orden de cursos en arreglo |
| Calificación por Sección | `lista.notas` agrupado por sección y estudiante | Sin ordenamiento |

La acumulación por clave usa arreglos paralelos (`String[] claves`, `double[] sumas`, `int[] counts`) sin colecciones del API.

---

## Manejo de CSV

### Importación

Todos los loaders usan `BufferedReader` con la misma estructura:
1. Abrir el archivo con `FileReader`.
2. Saltar la primera línea (encabezado).
3. Dividir cada línea con `split(",")`.
4. Validar que `partes.length >= N` y que el código no esté vacío.
5. Llamar al método `agregar*()` correspondiente dentro de un bloque `synchronized`.
6. Ignorar silenciosamente filas con datos inválidos o duplicados.

### Exportación

Los exportadores (bitácora, notas del instructor, historial del estudiante) usan `BufferedWriter` con `FileWriter`. La bitácora envuelve los campos en comillas dobles para manejar comas en las descripciones.

---

## Capacidades de los Arreglos

| Entidad | Capacidad máxima |
|---|---|
| Administradores | 20 |
| Instructores | 100 |
| Estudiantes | 500 |
| Cursos | 200 |
| Notas | 2000 |
| Eventos de bitácora | 3000 |
| Inscripciones pendientes | 500 |
| Inscripciones confirmadas | 500 |

Cuando un arreglo está lleno, la operación de agregar devuelve `false` sin lanzar excepción.

---

## Flujo de Autenticación

```
PanelLogin
    │
    ▼ login(codigo, contrasena)
AppController
    │
    ▼ ControladorLogin.iniciarSesion(lista, codigo, contrasena)
        Recorre administradores → instructores → estudiantes
        Si encuentra coincidencia → devuelve el Usuario
        Si no → devuelve null
    │
    ▼ (si usuario != null)
AppController.aumentarUsuariosActivos()
ControladorBitacora.registrar(..., "INICIO_SESION", "EXITO", ...)
VentanaPrincipal.cambiarVista(usuario.getRol())
```

---

## Flujo de Inscripción

```
PanelEstudiante.inscribirse()
    │
    ├── Validar: estaInscrito() → rechaza si ya está confirmado
    ├── Validar: estaInscritoPendiente() → rechaza si ya está pendiente
    ├── Validar: tieneChoqueSeccion() → rechaza si ya usa esa sección
    │
    ▼ AppController.agregarInscripcionPendiente(inscripcion)
        Agrega a lista.inscripcionesPendientes[]
    │
    ▼ (después de hasta 8 segundos)
HiloInscripciones.run()
    │
    ▼ AppController.procesarInscripcion()
        Extrae posición [0] de inscripcionesPendientes[]
        Desplaza el arreglo hacia la izquierda
        Agrega a inscripcionesConfirmadas[]
```
