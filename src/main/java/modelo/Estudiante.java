/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author david
 */
public class Estudiante extends Usuario {

    // Atributo propio del estudiante
    private int cantidadCursosInscritos; //este va a guardar los cursos a los cuales esta inscrito el estudiatne

    public Estudiante(String codigo, String nombre, String fechaNacimiento, String genero, String contrasena) {

        // el super llama al constructor del modelo Usuario y aca se envian los datos necesarios para crear al usuario
        // Estudiante da de una el rol de estudiante al crear un usuario con este modelo
        super(codigo, nombre, fechaNacimiento, genero, contrasena, "Estudiante");

        // Inicializa la cantidad de cursos en 0 esto pq empieza sin cursos
        this.cantidadCursosInscritos = 0;
    }

    public int getCantidadCursosInscritos() {
        return cantidadCursosInscritos;
    }

    public void setCantidadCursosInscritos(int cantidadCursosInscritos) {
        this.cantidadCursosInscritos = cantidadCursosInscritos;
    }

}
