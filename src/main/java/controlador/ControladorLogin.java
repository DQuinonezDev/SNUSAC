/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Administrador;
import modelo.Estudiante;
import modelo.Instructor;
import modelo.Listas;
import modelo.Usuario;

/**
 *
 * @author david
 */
public class ControladorLogin {
    
// Metodo que sirve para iniciar sesion
// Recibe las listas (donde están todos los usuarios), el codigo y la contrasen
    public static Usuario iniciarSesion(Listas modelo, String codigo, String contrasena) {

        // login si es admin
        for (int i = 0; i < modelo.cantidadAdministradores; i++) {

            // Obtiene un administrador del arreglo
            Administrador admin = modelo.administradores[i];

            // Verifica si el codigo y contrasena coinciden
            if (admin.getCodigo().equals(codigo) && admin.getContrasena().equals(contrasena)) {

                // Si coincide, retorna ese usuario (login exitoso)
                return admin;
            }
        }

        // login si es instructor
        for (int i = 0; i < modelo.cantidadInstructores; i++) {

            // Obtiene un instructor
            Instructor instructor = modelo.instructores[i];

            // Verifica credenciales
            if (instructor.getCodigo().equals(codigo) && instructor.getContrasena().equals(contrasena)) {

                // Retorna el instructor si coincide
                return instructor;
            }
        }

        // login si es estudiante
        for (int i = 0; i < modelo.cantidadEstudiantes; i++) {

            // Obtiene un estudiante
            Estudiante estudiante = modelo.estudiantes[i];

            // Verifica credenciales
            if (estudiante.getCodigo().equals(codigo) && estudiante.getContrasena().equals(contrasena)) {

                // Retorna el estudiante si coincide
                return estudiante;
            }
        }

        // si no encuentra nada
        return null; // Login fallido
    }
}
