/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Inscripcion;
import modelo.Instructor;
import modelo.Listas;
import modelo.Usuario;

/**
 *
 * @author emily
 */
public class AppController {
    // Atributo que guarda todo el sistema: usuarios, cursos, notas, etc.

    private Listas lista;

// Atributo que lleva el conteo de cuántos usuarios han iniciado sesión
    private int usuariosActivos;

// Guarda el usuario que inicio sesion actualmente
    private Usuario usuarioActual;

// Constructor del controlador principal
    public AppController(Listas l) {
        // Guarda el modelo recibido para poder trabajar con los datos del sistema
        this.lista = l;

        // Al iniciar el sistema, no hay usuarios activos
        this.usuariosActivos = 0;
    }

//   obtener el modelo
    public Listas getModelo() {
        return lista;
    }

//  hacer login
    public Usuario login(String codigo, String contrasena) {
        // Llama al controlador de login y le pasa el modelo junto con las credenciales
        usuarioActual = ControladorLogin.iniciarSesion(lista, codigo, contrasena);
        return usuarioActual;
    }

// Devuelve el usuario que tiene la sesion activa actualmente
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

//  guardar el estado actual del sistema en archivo
    public void guardarSistema() {
        // Llama al controlador de serializacion para guardar el modelo
        ControladorSerializacion.guardarListas(lista);
    }

//  saber cuántos usuarios activos hay
    public int getUsuariosActivos() {
        return usuariosActivos;
    }

// aumentar el contador de usuarios activos
    public void aumentarUsuariosActivos() {
        usuariosActivos++;
    }

// Metodo para disminuir el contador de usuarios activos
    public void disminuirUsuariosActivos() {
        // Solo resta si hay al menos un usuario activo
        if (usuariosActivos > 0) {
            usuariosActivos--;
        }
    }

// saber cuantas inscripciones pendientes hay
    public int getInscripcionesPendientes() {
        return lista.cantidadInscripcionesPendientes;
    }

// Procesa la primera inscripcion de la cola y la guarda en confirmadas
    public Inscripcion procesarInscripcion() {
        if (lista.cantidadInscripcionesPendientes > 0) {

            // toma la primera inscripcion de la cola
            Inscripcion procesada = lista.inscripcionesPendientes[0];

            // corre todos los elementos una posicion a la izquierda para sacarla de la cola
            for (int i = 0; i < lista.cantidadInscripcionesPendientes - 1; i++) {
                lista.inscripcionesPendientes[i] = lista.inscripcionesPendientes[i + 1];
            }
            lista.inscripcionesPendientes[lista.cantidadInscripcionesPendientes - 1] = null;
            lista.cantidadInscripcionesPendientes--;

            // la guardamos en inscripcionesConfirmadas para saber que el estudiante ya esta inscrito
            if (lista.cantidadInscripcionesConfirmadas < lista.inscripcionesConfirmadas.length) {
                lista.inscripcionesConfirmadas[lista.cantidadInscripcionesConfirmadas] = procesada;
                lista.cantidadInscripcionesConfirmadas++;
            }

            return procesada;
        }

        return null;
    }

// Revisa si un estudiante ya fue confirmado en un curso especifico
    public boolean estaInscrito(String codigoEstudiante, String codigoCurso) {
        for (int i = 0; i < lista.cantidadInscripcionesConfirmadas; i++) {
            Inscripcion ins = lista.inscripcionesConfirmadas[i];
            if (ins.getCodigo().equals(codigoEstudiante) && ins.getCodigoCurso().equals(codigoCurso)) {
                return true;
            }
        }
        return false;
    }

// Revisa si un estudiante tiene una inscripcion PENDIENTE (aun no procesada) en un curso
    public boolean estaInscritoPendiente(String codigoEstudiante, String codigoCurso) {
        for (int i = 0; i < lista.cantidadInscripcionesPendientes; i++) {
            Inscripcion ins = lista.inscripcionesPendientes[i];
            if (ins.getCodigo().equals(codigoEstudiante) && ins.getCodigoCurso().equals(codigoCurso)) {
                return true;
            }
        }
        return false;
    }

// Devuelve el arreglo de inscripciones confirmadas para poder contarlas por curso
    public Inscripcion[] getInscripcionesConfirmadas() {
        return lista.inscripcionesConfirmadas;
    }

    public int getCantidadInscripcionesConfirmadas() {
        return lista.cantidadInscripcionesConfirmadas;
    }

// agregar una nueva inscripción pendiente
    public void agregarInscripcionPendiente(Inscripcion inscripcion) {
        // Verifica que todavía haya espacio en el arreglo
        if (lista.cantidadInscripcionesPendientes < lista.inscripcionesPendientes.length) {

            // Guarda la nueva inscripcion en la siguiente posicion libre
            lista.inscripcionesPendientes[lista.cantidadInscripcionesPendientes] = inscripcion;

            // Aumenta el contador
            lista.cantidadInscripcionesPendientes++;
        }
    }

    // metodos para saber las cantidades de algo
// Metodo para obtener cuantos cursos hay registrados
    public int getCantidadCursosActivos() {
        return lista.cantidadCursos;
    }

    public int getCantidadEstudiantesRegistrados() {
        return lista.cantidadEstudiantes;
    }

    public int getCantidadCalificacionesRegistradas() {
        return lista.cantidadNotas;
    }

    // Devuelve true si el estudiante ya tiene una inscripcion (confirmada o pendiente)
    // en cualquier curso cuya seccion coincida con la seccion dada.
    // Se usa para evitar choques de horario al inscribirse.
    public boolean tieneChoqueSeccion(String codigoEstudiante, String seccion) {
        for (int i = 0; i < lista.cantidadInscripcionesConfirmadas; i++) {
            Inscripcion ins = lista.inscripcionesConfirmadas[i];
            if (!ins.getCodigo().equals(codigoEstudiante)) continue;
            for (int j = 0; j < lista.cantidadCursos; j++) {
                if (lista.cursos[j].getCodigo().equals(ins.getCodigoCurso())) {
                    if (lista.cursos[j].getSeccion().equals(seccion)) return true;
                    break;
                }
            }
        }
        for (int i = 0; i < lista.cantidadInscripcionesPendientes; i++) {
            Inscripcion ins = lista.inscripcionesPendientes[i];
            if (!ins.getCodigo().equals(codigoEstudiante)) continue;
            for (int j = 0; j < lista.cantidadCursos; j++) {
                if (lista.cursos[j].getCodigo().equals(ins.getCodigoCurso())) {
                    if (lista.cursos[j].getSeccion().equals(seccion)) return true;
                    break;
                }
            }
        }
        return false;
    }

    // Elimina la inscripcion de un estudiante en un curso especifico.
    // Busca primero en confirmadas y luego en pendientes.
    public boolean eliminarInscripcion(String codigoEstudiante, String codigoCurso) {
        for (int i = 0; i < lista.cantidadInscripcionesConfirmadas; i++) {
            Inscripcion ins = lista.inscripcionesConfirmadas[i];
            if (ins.getCodigo().equals(codigoEstudiante) && ins.getCodigoCurso().equals(codigoCurso)) {
                for (int j = i; j < lista.cantidadInscripcionesConfirmadas - 1; j++) {
                    lista.inscripcionesConfirmadas[j] = lista.inscripcionesConfirmadas[j + 1];
                }
                lista.inscripcionesConfirmadas[lista.cantidadInscripcionesConfirmadas - 1] = null;
                lista.cantidadInscripcionesConfirmadas--;
                return true;
            }
        }
        for (int i = 0; i < lista.cantidadInscripcionesPendientes; i++) {
            Inscripcion ins = lista.inscripcionesPendientes[i];
            if (ins.getCodigo().equals(codigoEstudiante) && ins.getCodigoCurso().equals(codigoCurso)) {
                for (int j = i; j < lista.cantidadInscripcionesPendientes - 1; j++) {
                    lista.inscripcionesPendientes[j] = lista.inscripcionesPendientes[j + 1];
                }
                lista.inscripcionesPendientes[lista.cantidadInscripcionesPendientes - 1] = null;
                lista.cantidadInscripcionesPendientes--;
                return true;
            }
        }
        return false;
    }
}
