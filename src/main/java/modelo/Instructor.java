/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author david
 */
// el extends usuario quiere decir que *hereda* todo del usuario
//esto quiere decir que obtiene el codigo, nombre, etc, todos los atributos del usuario y metodos
public class Instructor extends Usuario {

    //atributo propio del instructor,
    private int cantidadSeccionesAsignadas; // va a guardar la cantidad de secciones que tiene el instructor

    //constructor, se ejecuta cuando se crea un instructor
    public Instructor(String codigo, String nombre, String fechaNacimiento, String genero, String contrasena) {
        
         // Llama al constructor de Usuario
        // y automáticamente define el rol como "Instructor"
        super(codigo, nombre, fechaNacimiento, genero, contrasena, "Instructor");
        
        // Inicializa las secciones en 0 ya que al inicio no tiene ninguna asignada
        this.cantidadSeccionesAsignadas = 0;
    }

    //getter obtiene las secciones asginadas
    public int getCantidadSeccionesAsignadas() {
        return cantidadSeccionesAsignadas;
    }

    //setter modifica las secciones asignadas
    public void setCantidadSeccionesAsignadas(int cantidadSeccionesAsignadas) {
        this.cantidadSeccionesAsignadas = cantidadSeccionesAsignadas;
    }
}
