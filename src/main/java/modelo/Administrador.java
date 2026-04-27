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
    public class Administrador extends Usuario {

        //constructor, se ejecuta cuando se crea un administrador
        public Administrador(String codigo, String nombre, String fechaNacimiento, String genero, String contrasena) {
            
            // el super llama al constructor del modelo Usuario y aca se envian los datos necesarios para crear al usuario
            // Administrador le da de una el rol de admin
            super(codigo, nombre, fechaNacimiento, genero, contrasena, "Administrador");
        }
    }
