/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;

/**
 *
 * @author david
 */
public class Listas implements Serializable {

    // Aqui vamos a  guardar todos los objetos del sistema
    public Administrador[] administradores;
    public Instructor[] instructores;
    public Estudiante[] estudiantes;
    public Curso[] cursos;
    public Nota[] notas;
    public Bitacora[] bitacora;
    public Inscripcion[] inscripcionesPendientes;
    // inscripciones que ya fueron procesadas y confirmadas por el hilo
    public Inscripcion[] inscripcionesConfirmadas;

    //estos artibutos nos dicen cuandos elementos hay en cada arreglo
    public int cantidadAdministradores;
    public int cantidadInstructores;
    public int cantidadEstudiantes;
    public int cantidadCursos;
    public int cantidadNotas;
    public int cantidadEventos;
    public int cantidadInscripcionesPendientes;
    public int cantidadInscripcionesConfirmadas;

    //constructor
    public Listas() {
        administradores = new Administrador[20]; //areglos con tamanos definidos
        instructores = new Instructor[100];
        estudiantes = new Estudiante[500];
        cursos = new Curso[200];
        notas = new Nota[2000];
        bitacora = new Bitacora[3000];
        inscripcionesPendientes = new Inscripcion[500];
        inscripcionesConfirmadas = new Inscripcion[500];

        //iniciamos en 0 todos los contadores
        cantidadAdministradores = 0;
        cantidadInstructores = 0;
        cantidadEstudiantes = 0;
        cantidadCursos = 0;
        cantidadNotas = 0;
        cantidadEventos = 0;
        cantidadInscripcionesPendientes = 0;
        cantidadInscripcionesConfirmadas = 0;
        //creamos el admin por defecto
        crearAdminPorDefecto();
    }

    private void crearAdminPorDefecto() {
        // Crea un administrador con el constructor del modelo admin

        Administrador admin = new Administrador(
                "admin", //codigo
                "Administrador General", //nombre
                "2005-09-24", //fecha
                "No definido", //genero
                "IPC1B" //contrasena
                // rol - ADMINISTRADOr se pone por defecto al crear un adminnistrador
        );
        
        // lo guarda en el arregl
        administradores[cantidadAdministradores] = admin;
        cantidadAdministradores++; // Aumentar contador

    }

}
