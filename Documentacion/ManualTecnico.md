# Manual Técnico — Sancarlista Academy

**Autora:** Emily González  
**Curso:** Introducción a la Programación y Computación 1  
**Universidad de San Carlos de Guatemala**

---

## Descripción General

Sancarlista Academy es una aplicación de escritorio en Java con interfaz Swing. Gestiona usuarios, cursos, inscripciones, calificaciones, reportes PDF y una bitácora de actividad. Usa arreglos estáticos como única estructura de datos (sin ArrayList ni HashMap).

---

## Tecnologías Utilizadas

| Tecnología | Versión | Uso |
|---|---|---|
| Java SE | 11+ | Lenguaje principal |
| Java Swing | JDK incluido | Interfaz gráfica |
| NetBeans IDE | 17+ | Entorno de desarrollo |
| Apache PDFBox | 2.0.32 | Generación de reportes PDF |
| Java Serialization | JDK incluido | Guardado de datos en `.ser` |
| Maven | 3.x | Gestión de dependencias |

Dependencia en `pom.xml`:
```xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>2.0.32</version>
</dependency>
```

---

## Arquitectura MVC

El sistema sigue el patrón Modelo-Vista-Controlador:

- **Modelo:** clases de datos (`Listas`, `Usuario`, `Curso`, `Nota`, etc.)
- **Controlador:** lógica de negocio (`AppController`, `ControladorUsuarios`, etc.)
- **Vista:** paneles Swing (`PanelAdministrador`, `PanelInstructor`, `PanelEstudiante`, `PanelLogin`)

La navegación entre vistas usa un `CardLayout` dentro de `VentanaPrincipal`.

---

## Estructura de Paquetes

```
src/main/java/
├── modelo/
│   ├── Listas.java              (objeto raíz con todos los arreglos)
│   ├── Usuario.java             (clase base abstracta)
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

## Modelo de Datos

### Clase Listas

Es el objeto raíz. Contiene todos los arreglos del sistema y sus contadores. Implementa `Serializable` para guardarse completa en un archivo `.ser`.

| Arreglo | Capacidad |
|---|---|
| `administradores[]` | 20 |
| `instructores[]` | 100 |
| `estudiantes[]` | 500 |
| `cursos[]` | 200 |
| `notas[]` | 2000 |
| `bitacora[]` | 3000 |
| `inscripcionesPendientes[]` | 500 |
| `inscripcionesConfirmadas[]` | 500 |

Al iniciar, el constructor crea automáticamente un administrador por defecto (`admin` / `IPC1B`).

### Clases de Usuario

`Usuario` es la clase base con: `codigo`, `nombre`, `fechaNacimiento`, `genero`, `contrasena`, `rol`.  
`Administrador`, `Instructor` y `Estudiante` la extienden. `Estudiante` agrega `cantidadCursosInscritos`.

### Clase Curso

Atributos: `codigo`, `nombre`, `descripcion`, `creditos` (int), `seccion`, `codigoInstructor`.

### Clase Nota

Atributos: `codigoCurso`, `codigoSeccion`, `codigoEstudiante`, `ponderacion`, `nota`, `fechaRegistro`.  
La clave única es la combinación de los tres códigos.

### Clase Inscripcion

Atributos: `codigo` (del estudiante), `nombre` (del estudiante), `codigoCurso`.  
Se usa tanto en la cola de pendientes como en el arreglo de confirmadas.

### Clase Bitacora

Atributos: `fecha`, `tipoUsuario`, `codigoUsuario`, `operacion`, `estado`, `descripcion`.

---

## Controladores

### AppController

Controlador principal. Guarda la referencia a `Listas`, el `usuarioActual` y el contador de `usuariosActivos`.

Métodos importantes:

| Método | Descripción |
|---|---|
| `login(codigo, contrasena)` | Autentica al usuario delegando a ControladorLogin |
| `procesarInscripcion()` | Mueve la primera inscripción de pendientes a confirmadas |
| `estaInscrito(estudiante, curso)` | Busca en inscripcionesConfirmadas |
| `tieneChoqueSeccion(estudiante, seccion)` | Evita inscribirse en dos cursos con la misma sección |
| `eliminarInscripcion(estudiante, curso)` | Borra del arreglo desplazando elementos |
| `guardarSistema()` | Llama a ControladorSerializacion |

### ControladorLogin

Método estático `iniciarSesion()`. Recorre administradores, instructores y estudiantes buscando código y contraseña coincidentes. Devuelve el `Usuario` encontrado o `null`.

### ControladorUsuarios

Gestiona instructores y estudiantes. Valida que el código no esté vacío ni duplicado. Los cargadores CSV usan `synchronized` al insertar.

### ControladorCursos

Gestiona cursos. `asignarInstructor()` actualiza el campo `codigoInstructor` del curso encontrado.

### ControladorNotas

Validaciones al agregar nota: ponderación > 0, nota entre 0 y 100, estudiante existe, sin duplicado.

Fórmula del promedio ponderado:
```
promedio = Σ(nota × ponderacion) / Σ(ponderacion)
```
Devuelve `-1` si no hay notas para esa combinación estudiante + sección.

### ControladorBitacora

Métodos estáticos: `registrar()`, `filtrar()` (búsqueda por contains insensible a mayúsculas), `exportarCSV()` (campos entre comillas dobles).

### ControladorReportes

Genera PDFs con PDFBox 2.0.32. Cada reporte llena un arreglo `String[]` de líneas y lo pasa a `generarPDF()`. Las líneas con prefijo `##` se renderizan en negrita. Los caracteres acentuados se limpian antes de escribir para evitar errores de codificación. El nombre del archivo tiene formato `DD_MM_YYYY_HH_mm_ss_Tipo.pdf`.

### ControladorSerializacion

Guarda y carga el objeto `Listas` completo en `datos.ser`. Si el archivo no existe al cargar, devuelve un `new Listas()`.

---

## Hilos del Sistema

Los tres hilos se inician en `PanelAdministrador.iniciarHilos()` y solo se crean una vez (flag `hilosIniciados`). Todos actualizan la interfaz con `SwingUtilities.invokeLater()`.

| Clase | Intervalo | Función |
|---|---|---|
| `HiloSesionesActivas` | 5 segundos | Muestra usuarios conectados |
| `HiloInscripciones` | 8 segundos | Procesa una inscripción pendiente |
| `HiloEstadisticas` | 10 segundos | Muestra totales del sistema |

Cada hilo tiene `detener()` que pone `activo = false`, complementado con `Thread.interrupt()` para salir del `sleep`.

---

## Persistencia

Todos los modelos implementan `Serializable`. Al serializar `Listas` se persiste todo el estado del sistema en un único archivo.

- **Guardar:** `AppController.guardarSistema()` → `ControladorSerializacion.guardarListas(lista)`
- **Cargar:** al inicio en `VentanaPrincipal` → `ControladorSerializacion.cargarListas()`

El guardado se llama automáticamente después de cada operación de escritura (crear, actualizar, eliminar, inscribir, cerrar sesión).

---

## Formatos CSV

### Instructores y Estudiantes
```
Codigo,Nombre,FechaNacimiento,Genero,Contrasena
```

### Cursos
```
Codigo,Nombre,Descripcion,Creditos,Seccion
```

### Notas
```
CodigoCurso,CodigoSeccion,CodigoEstudiante,Ponderacion,Nota,FechaRegistro
```

La primera fila siempre es el encabezado y se omite. Las filas con errores se ignoran sin detener la carga.
