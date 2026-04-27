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
//  implements Serializable significa que este objeto se puede guardar y recuperar desde archivos
public class Usuario implements Serializable {

    // Atributos protegidos, protected = estos se pueden usar con herencia en otras clases
    protected String codigo;
    protected String nombre;
    protected String fechaNacimiento;
    protected String genero;
    protected String contrasena;
    protected String rol;

    //constructor
    public Usuario(String codigo, String nombre, String fechaNacimiento, String genero, String contrasena, String rol) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    //metodo para autentificar al usuario, compara que el codigo sea igual y que la contrasena sea igual
    public boolean autenticar(String codigo, String contrasena) {
        return this.codigo.equals(codigo) && this.contrasena.equals(contrasena);
    }
}
