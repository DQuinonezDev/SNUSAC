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
public class Curso implements Serializable {

    //atributos de curso
    private String codigo;
    private String nombre;
    private String descripcion;
    private int creditos;
    private String seccion;
    private String codigoInstructor; // Codigo del instructor asignado a este curso

    //Constructor de la clase Curso se usa cuando creas un nuevo curso
    public Curso(String codigo, String nombre, String descripcion, int creditos, String seccion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.creditos = creditos;
        this.seccion = seccion;
        this.codigoInstructor = ""; // Sin instructor asignado por defecto
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public String getCodigoInstructor() {
        return codigoInstructor;
    }

    public void setCodigoInstructor(String codigoInstructor) {
        this.codigoInstructor = codigoInstructor;
    }
}
